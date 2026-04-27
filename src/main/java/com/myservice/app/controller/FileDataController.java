package com.myservice.app.controller;

import com.myservice.app.model.ApiResponse;
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
 * GET /api/v1/data/files          - list available .xlsx files
 * GET /api/v1/data/load?file=name - load file and return rows as JSON
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
}
