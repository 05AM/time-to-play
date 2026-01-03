package org.example.api.member.repository;

import org.example.core.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaMemberRepository extends JpaRepository<Member, Long> {
}
