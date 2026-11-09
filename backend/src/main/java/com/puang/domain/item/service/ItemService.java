package com.puang.domain.item.service;

import com.puang.domain.item.dto.ItemResponse;
import com.puang.domain.item.repository.ItemRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    // TODO: return every item on the map as ItemResponse
    public List<ItemResponse> getItems() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    // TODO: return one item as ItemResponse, throw ItemErrorCode.ITEM_NOT_FOUND if missing
    public ItemResponse getItem(Long itemId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
