package com.puang.domain.user.entity;

import com.puang.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "users")
@SQLRestriction("deleted_at IS NULL")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Random UUID issued when the account is created. The app stores it and uses it to get access tokens.
    // Unique (indexed) because every token request looks the user up by it. Never changes.
    @Column(nullable = false, unique = true, updatable = false, length = 36)
    private String uuid;

    // 게스트 로그인은 이메일/비밀번호 없이 가입하므로 nullable
    @Column(unique = true)
    private String email;

    @Column(nullable = false, length = 30)
    private String nickname;

    // 암호화된 값만 저장
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guild_id")
    private Guild guild;

    @Column(nullable = false)
    private boolean allowNotification;

    @Builder
    private User(String uuid, String email, String nickname, String password, Guild guild, boolean allowNotification) {
        this.uuid = uuid;
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.guild = guild;
        this.allowNotification = allowNotification;
    }

    public static User createWithUuid(String uuid, String nickname) {
        return User.builder()
                .uuid(uuid)
                .nickname(nickname)
                .allowNotification(true)
                .build();
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    public void joinGuild(Guild guild) {
        this.guild = guild;
    }

    public void leaveGuild() {
        this.guild = null;
    }

    public void changeAllowNotification(boolean allowNotification) {
        this.allowNotification = allowNotification;
    }
}
