package com.puang.domain.item.controller;

import com.puang.domain.item.dto.ItemResponse;
import com.puang.domain.item.service.ItemService;
import com.puang.global.response.ApiResponse;
import com.puang.global.response.code.GeneralSuccessCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    /**
     * Returns every item lying on the map.
     */
    @GetMapping
    public ApiResponse<List<ItemResponse>> getItems() {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, itemService.getItems());
    }

    /**
     * Returns one item on the map.
     */
    @GetMapping("/{itemId}")
    public ApiResponse<ItemResponse> getItem(@PathVariable Long itemId) {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, itemService.getItem(itemId));
    }
}
