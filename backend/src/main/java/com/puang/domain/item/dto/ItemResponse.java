package com.puang.domain.item.dto;

import com.puang.domain.item.entity.Item;
import com.puang.domain.item.entity.ItemType;

/**
 * An item lying on the map. Same shape as result.items[] in GET /api/chunks.
 */
public record ItemResponse(
        Long itemId,
        ItemType itemType,
        int x,
        int y
) {
    public static ItemResponse from(Item item) {
        return new ItemResponse(item.getId(), item.getItemType(), item.getPosX(), item.getPosY());
    }
}
