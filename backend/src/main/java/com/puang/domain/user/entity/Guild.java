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
@Table(name = "guilds")
@SQLRestriction("deleted_at IS NULL")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Guild extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String name;

    @Column(length = 255)
    private String description;

    // Guild leader. Only the leader can edit the guild.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_id", nullable = false)
    private User leader;

    @Builder
    private Guild(String name, String description, User leader) {
        this.name = name;
        this.description = description;
        this.leader = leader;
    }

    public void update(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public void changeLeader(User newLeader) {
        this.leader = newLeader;
    }

    // Compares ids so the lazy leader proxy is not initialized
    public boolean isLeader(Long userId) {
        return leader.getId().equals(userId);
    }
}
