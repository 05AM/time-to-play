package org.example.core.domain.member;

import jakarta.persistence.*;

import java.time.LocalDateTime;

import org.example.core.domain.common.entity.BaseCreatedAtEntity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Member extends BaseCreatedAtEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MemberRole role;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    private Member(String name, String email, MemberRole role) {
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public static Member create(String name, String email) {
        return new Member(name, email, MemberRole.MEMBER);
    }
}
