package com.puang.global.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 생성/수정 시간 + soft delete가 필요한 엔티티용 (예: User, Guild).
 * 상속하는 엔티티 클래스에 @SQLRestriction("deleted_at IS NULL")을 붙여야
 * 조회 시 삭제된 행이 자동으로 빠진다.
 */
@Getter
@MappedSuperclass
public abstract class BaseEntity extends BaseTimeEntity {

    @Column
    private LocalDateTime deletedAt;

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    public void restore() {
        this.deletedAt = null;
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }
}
