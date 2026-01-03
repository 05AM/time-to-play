package org.example.core.domain.auth;

import org.example.core.domain.common.entity.BaseCreatedAtEntity;
import org.example.core.domain.member.Member;

import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(
    name = "member_oauth_account",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_member_oauth_provider_user",
            columnNames = {"provider", "provider_user_id"}
        ),
        @UniqueConstraint(
            name = "uk_member_oauth_member_provider",
            columnNames = {"member_id", "provider"}
        )
    }
)
public class OAuthAccount extends BaseCreatedAtEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private OAuthProvider provider;

    @Column(name = "provider_user_id", nullable = false, length = 100)
    private String providerUserId;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "refresh_token")
    private String refreshToken;

    public static OAuthAccount create(Member member, OAuthProvider provider, String providerUserId, String email, String refreshToken) {
        return new OAuthAccount(null, member, provider, providerUserId, email, refreshToken);
    }
}
