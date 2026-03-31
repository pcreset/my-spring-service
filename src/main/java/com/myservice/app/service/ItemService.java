package com.myservice.app.service;

import com.myservice.app.exception.ResourceNotFoundException;
import com.myservice.app.model.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Service layer for Item operations.
 * Uses an in-memory store for now — swap this for a JPA repository when ready.
 */
@Slf4j
@Service
public class ItemService {

    // In-memory data store (replace with JPA repository later)
    private final Map<Long, Item> store = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(1);

    public List<Item> findAll() {
        log.debug("Fetching all items");
        return new ArrayList<>(store.values());
    }

    public Item findById(Long id) {
        log.debug("Fetching item with id={}", id);
        Item item = store.get(id);
        if (item == null) {
            throw new ResourceNotFoundException("Item not found with id: " + id);
        }
        return item;
    }

    public Item create(Item item) {
        long newId = idSequence.getAndIncrement();
        item.setId(newId);
        item.setCreatedAt(LocalDateTime.now());
        item.setUpdatedAt(LocalDateTime.now());
        item.setActive(true);
        store.put(newId, item);
        log.info("Created item id={} name={}", newId, item.getName());
        return item;
    }

    public Item update(Long id, Item updated) {
        Item existing = findById(id);
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setActive(updated.isActive());
        existing.setUpdatedAt(LocalDateTime.now());
        store.put(id, existing);
        log.info("Updated item id={}", id);
        return existing;
    }

    public void delete(Long id) {
        if (!store.containsKey(id)) {
            throw new ResourceNotFoundException("Item not found with id: " + id);
        }
        store.remove(id);
        log.info("Deleted item id={}", id);
    }

}
