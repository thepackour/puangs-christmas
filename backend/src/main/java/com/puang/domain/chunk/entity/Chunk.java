package com.puang.domain.chunk.entity;

import com.puang.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * A 32x32-tile piece of the map. Each chunk is one row, and each tile is 1 byte.
 */
@Entity
@Table(
        name = "chunks",
        uniqueConstraints = @UniqueConstraint(columnNames = {"center_x", "center_y"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Chunk extends BaseTimeEntity {

    public static final int SIZE = 32;
    public static final int TILE_COUNT = SIZE * SIZE;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "center_x", nullable = false)
    private int centerX;

    @Column(name = "center_y", nullable = false)
    private int centerY;

    /*
     * 32x32 = 1024 bytes, indexed as y * SIZE + x.
     * Each byte is one tile:
     *   upper 4 bits = tile type
     *     0 = empty
     *     1 = wall (normal block)
     *     2 = cracked block (breaks after the number of hits in the lower 4 bits)
     *     3 = spider web (the player stops on it instead of bouncing off)
     *     4 = color block (players can move it)
     *   lower 4 bits = extra value for the type
     *     cracked block: remaining durability (hits left, 1-15)
     *     color block: color id
     *     other types: 0
     */
    @Column(nullable = false, columnDefinition = "BLOB")
    private byte[] tiles;

    @Builder
    private Chunk(int centerX, int centerY, byte[] tiles) {
        if (tiles == null || tiles.length != TILE_COUNT) {
            throw new IllegalArgumentException("tiles must be " + TILE_COUNT + " bytes");
        }
        this.centerX = centerX;
        this.centerY = centerY;
        this.tiles = tiles;
    }

    public byte getTile(int x, int y) {
        return tiles[y * SIZE + x];
    }

    public void setTile(int x, int y, byte value) {
        tiles[y * SIZE + x] = value;
    }
}