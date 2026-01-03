package org.example.api.member.service;

import org.example.api.common.exception.NotFoundException;
import org.example.api.member.repository.JpaMemberRepository;
import org.example.core.domain.member.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class MemberQueryService {

    private final JpaMemberRepository memberRepository;

    public Member getById(long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 멤버입니다."));
    }
}
