package com.puang.domain.item.service;

import com.puang.domain.item.dto.ItemResponse;
import com.puang.domain.item.repository.ItemRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;


    public List<ItemResponse> getItems() {
        return itemRepository.findAll().stream().map(ItemResponse::from).toList();
    }


    public ItemResponse getItem(Long itemId) {
        return itemRepository.findById(itemId).map(ItemResponse::from)
                .orElseThrow(() -> new ProjectException(ItemErrorCode.ITEM_NOT_FOUND));
    }
}
