package com.puang.domain.player.entity;

import com.puang.domain.user.entity.User;
import com.puang.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "players")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Player extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private int posX;

    @Column(nullable = false)
    private int posY;

    @Column(nullable = false)
    private long score;

    // 남은 일일 이동 횟수
    @Column(nullable = false)
    private int moves;

    @Column(nullable = false)
    private int maxMoves;

    @Column(nullable = false)
    private int coins;

    // 네트워크가 끊겼다 재전송된 요청을 걸러내기 위한 마지막 처리 시퀀스
    @Column(nullable = false)
    private long lastSeq;

    @Builder
    private Player(User user, int posX, int posY, int maxMoves) {
        this.user = user;
        this.posX = posX;
        this.posY = posY;
        this.maxMoves = maxMoves;
        this.moves = maxMoves;
    }

    public void moveTo(int posX, int posY, long seq) {
        this.posX = posX;
        this.posY = posY;
        this.lastSeq = seq;
    }

    public void useMove() {
        this.moves--;
    }

    public void resetMoves() {
        this.moves = this.maxMoves;
    }

    public void addScore(long amount) {
        this.score += amount;
    }

    public void addCoins(int amount) {
        this.coins += amount;
    }
}
