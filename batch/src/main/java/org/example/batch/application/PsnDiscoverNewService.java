package org.example.batch.application;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.example.batch.infra.client.psn.dto.response.PsnConceptGameListRes;
import org.example.batch.infra.client.psn.dto.response.PsnConceptGameListRes.Media;
import org.example.batch.infra.persistence.GameDao;
import org.example.batch.infra.persistence.PlatformGameDao;
import org.example.batch.util.GameTitleNormalizer;
import org.example.batch.util.SlugNormalizer;
import org.example.core.domain.game.common.GamePlatform;
import org.example.core.domain.game.common.MediaType;
import org.example.core.domain.game.platform.PlatformRootIDType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class PsnDiscoverNewService {

    private static final Comparator<Media> VIDEO_FIRST =
        Comparator.comparingInt(m -> MediaType.VIDEO.name().equals(m.type()) ? 0 : 1);

    private final GameDao gameDao;
    private final PlatformGameDao platformGameDao;

    @Value("${external.psn.concept-base-url}")
    private String storeBaseUrl;

    public void persist(List<PsnConceptGameListRes.Concept> concepts) {
        // slug title 뽑기
        Map<String, String> conceptIdToSlug = concepts.stream()
            .collect(Collectors.toMap(
                PsnConceptGameListRes.Concept::id,
                c -> SlugNormalizer.toTempSlug(c.name()),
                (a, b) -> a
            ));

        // game 생성
        List<GameDao.GameInsert> games = concepts.stream()
            .map(c -> new GameDao.GameInsert(c.name(), conceptIdToSlug.get(c.id())))
            .toList();
        gameDao.batchInsertIgnore(games);

        // slug title을 gameId로 가져오기
        Map<String, Long> slugToGameId = gameDao.findIdsBySlugs(
            conceptIdToSlug.values().stream().distinct().toList()
        );

        List<PlatformGameDao.PlatformGameUpsert> upserts = concepts.stream()
            .map(c -> {
                String slug = conceptIdToSlug.get(c.id());
                Long gameId = slugToGameId.get(slug);

                if (gameId == null) {
                    log.warn("[PSN][DISCOVER] skip conceptId={} slug={}", c.id(), slug);
                    return null;
                }

                String mainImageUrl = findMasterImageUrl(c);

                return new PlatformGameDao.PlatformGameUpsert(
                    gameId,
                    GamePlatform.PSN.name(),
                    PlatformRootIDType.CONCEPT.name(),
                    c.id(),
                    c.name(),
                    GameTitleNormalizer.normalizeDisplay(c.name()),
                    GameTitleNormalizer.normalizeKey(c.name()),
                    storeBaseUrl + c.id(),
                    mainImageUrl
                );
            })
            .filter(Objects::nonNull)
            .toList();

        if (!upserts.isEmpty()) {
            platformGameDao.batchUpsert(upserts);

            Map<String, Long> conceptIdToPlatformGameId = platformGameDao.mapIdsByRootIds(
                GamePlatform.PSN.name(),
                PlatformRootIDType.CONCEPT.name(),
                upserts.stream().map(PlatformGameDao.PlatformGameUpsert::rootId).toList()
            );

            for (PsnConceptGameListRes.Concept c : concepts) {
                Long platformGameId = conceptIdToPlatformGameId.get(c.id());
                if (platformGameId == null) continue;

                platformGameDao.replacePlatformGameMedia(
                    platformGameId,
                    orderedPlatformMedia(c)
                );
            }
        }

        log.info("[PSN][DISCOVER] concepts={} upserts={} skipped={}",
            concepts.size(), upserts.size(), concepts.size() - upserts.size());
    }

    private List<PlatformGameDao.MediaRow> orderedPlatformMedia(PsnConceptGameListRes.Concept concept) {
        List<Media> medias = concept.media() == null ? List.of() : concept.media();

        List<Media> filtered = medias.stream()
            .filter(this::isKeepMedia)
            .sorted(VIDEO_FIRST)
            .toList();

        return IntStream.range(0, filtered.size())
            .mapToObj(i -> toPlatformMediaRow(filtered.get(i), i))
            .toList();
    }

    private boolean isKeepMedia(Media m) {
        if (m == null) return false;

        if (MediaType.VIDEO.name().equals(m.type())) return true;

        return "SCREENSHOT".equals(m.role()) && "IMAGE".equals(m.type());
    }

    private PlatformGameDao.MediaRow toPlatformMediaRow(Media m, int order) {
        String mediaType = MediaType.VIDEO.name().equals(m.type())
            ? MediaType.VIDEO.name()
            : MediaType.IMAGE.name();

        return new PlatformGameDao.MediaRow(
            mediaType,
            m.url(),
            order
        );
    }

    private String findMasterImageUrl(PsnConceptGameListRes.Concept concept) {
        if (concept.media() == null || concept.media().isEmpty()) return null;

        return concept.media().stream()
            .filter(m -> m != null
                && "MASTER".equals(m.role())
                && "IMAGE".equals(m.type())
                && m.url() != null
                && !m.url().isBlank()
            )
            .map(Media::url)
            .findFirst()
            .orElse(null);
    }
}
