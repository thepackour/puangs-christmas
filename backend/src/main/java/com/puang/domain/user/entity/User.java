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

    // Login id. Unique (indexed) because every login looks the user up by it.
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, length = 30)
    private String nickname;

    // BCrypt hash only, never the raw password
    @Column(nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guild_id")
    private Guild guild;

    @Column(nullable = false)
    private boolean allowNotification;

    @Builder
    private User(String email, String nickname, String password, Guild guild, boolean allowNotification) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.guild = guild;
        this.allowNotification = allowNotification;
    }

    public static User create(String email, String encodedPassword, String nickname) {
        return User.builder()
                .email(email)
                .password(encodedPassword)
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
