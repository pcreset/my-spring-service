package com.myservice.app.controller;

import com.myservice.app.model.ApiResponse;
import com.myservice.app.model.UpdateRowRequest;
import com.myservice.app.service.FileDataService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for loading Excel file data.
 *
 * GET  /api/v1/data/files               - list available .xlsx files
 * GET  /api/v1/data/load?file=name      - load file (cached after first read)
 * POST /api/v1/data/cache/evict?file=   - evict one file from cache
 * POST /api/v1/data/cache/clear         - clear entire cache
 */
@RestController
@RequestMapping("/api/v1/data")
@RequiredArgsConstructor
public class FileDataController {

    private static final Logger log = LoggerFactory.getLogger(FileDataController.class);

    private final FileDataService fileDataService;

    @GetMapping("/files")
    public ResponseEntity<ApiResponse<List<String>>> listFiles() {
        List<String> files = fileDataService.listFiles();
        return ResponseEntity.ok(ApiResponse.ok("Found " + files.size() + " file(s)", files));
    }

    @GetMapping("/load")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> loadFile(
            @RequestParam("file") String filename) {
        log.info("Request to load file: {}", filename);
        List<Map<String, Object>> rows = fileDataService.loadFile(filename);
        return ResponseEntity.ok(ApiResponse.ok("Loaded " + rows.size() + " row(s)", rows));
    }

    /** Update a single row in the cached data with the provided field changes. */
    @PutMapping("/row")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateRow(@RequestBody UpdateRowRequest req) {
        log.info("Update row {} in {}", req.rowIndex(), req.file());
        Map<String, Object> updated = fileDataService.updateRow(req.file(), req.rowIndex(), req.changes());
        return ResponseEntity.ok(ApiResponse.ok("Row updated", updated));
    }

    /** Write the current cached data for a file back to disk. */
    @PostMapping("/dump")
    public ResponseEntity<ApiResponse<Void>> dumpToFile(@RequestParam("file") String filename) {
        log.info("Dump cache to file: {}", filename);
        fileDataService.dumpToFile(filename);
        return ResponseEntity.ok(ApiResponse.ok("Dumped to: " + filename, null));
    }

    /** Evict a single file from the in-memory cache. */
    @PostMapping("/cache/evict")
    public ResponseEntity<ApiResponse<Void>> evictCache(@RequestParam("file") String filename) {
        fileDataService.evictCache(filename);
        return ResponseEntity.ok(ApiResponse.ok("Cache cleared for: " + filename, null));
    }

    /** Clear the entire file data cache. */
    @PostMapping("/cache/clear")
    public ResponseEntity<ApiResponse<Void>> clearAllCaches() {
        fileDataService.evictAllCaches();
        return ResponseEntity.ok(ApiResponse.ok("All file caches cleared", null));
    }
}
