package com.myservice.app.controller;

import com.myservice.app.model.ApiResponse;
import com.myservice.app.model.Item;
import com.myservice.app.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Item endpoints.
 *
 * Base URL: /api/v1/items
 *
 * Endpoints:
 *   GET    /api/v1/items        - list all items
 *   GET    /api/v1/items/{id}   - get item by ID
 *   POST   /api/v1/items        - create new item
 *   PUT    /api/v1/items/{id}   - update item
 *   DELETE /api/v1/items/{id}   - delete item
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Item>>> getAll() {
        List<Item> items = itemService.findAll();
        return ResponseEntity.ok(ApiResponse.ok("Retrieved " + items.size() + " item(s)", items));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Item>> getById(@PathVariable Long id) {
        Item item = itemService.findById(id);
        return ResponseEntity.ok(ApiResponse.ok(item));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Item>> create(@Valid @RequestBody Item item) {
        Item created = itemService.create(item);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Item created successfully", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Item>> update(@PathVariable Long id,
                                                     @Valid @RequestBody Item item) {
        Item updated = itemService.update(id, item);
        return ResponseEntity.ok(ApiResponse.ok("Item updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        itemService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Item deleted successfully", null));
    }

}
