package com.puang.domain.item.entity;

import com.puang.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 맵 위에 떨어져 있는 아이템. 플레이어가 주우면 Inventory로 옮기고 이 행은 삭제.
 */
@Entity
@Table(
        name = "items",
        indexes = @Index(name = "idx_items_pos", columnList = "pos_x, pos_y")
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false, length = 30)
    private ItemType itemType;

    @Column(name = "pos_x", nullable = false)
    private int posX;

    @Column(name = "pos_y", nullable = false)
    private int posY;

    @Builder
    private Item(ItemType itemType, int posX, int posY) {
        this.itemType = itemType;
        this.posX = posX;
        this.posY = posY;
    }
}
