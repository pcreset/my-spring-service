package com.myservice.app.service;

import com.myservice.app.exception.ResourceNotFoundException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Reads an Excel (.xlsx) file from the configured data directory
 * and converts it to a list of maps (JSON-ready).
 * First row = column headers. Each subsequent row = one JSON object.
 *
 * Results are cached in memory by filename. Call evictCache(filename)
 * or evictAllCaches() to force a fresh read from disk.
 */
@Service
public class FileDataService {

    private static final Logger log = LoggerFactory.getLogger(FileDataService.class);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM-dd-yyyy");

    @Value("${app.data.directory}")
    private String dataDirectory;

    public List<String> listFiles() {
        File dir = new File(dataDirectory);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new ResourceNotFoundException("Data directory not found: " + dataDirectory);
        }
        String[] files = dir.list((d, name) -> name.toLowerCase().endsWith(".xlsx"));
        return files != null ? Arrays.asList(files) : Collections.emptyList();
    }

    /**
     * Load file data — result is cached by filename.
     * Subsequent calls with the same filename return cached data instantly.
     */
    @Cacheable(value = "fileData", key = "#filename")
    public List<Map<String, Object>> loadFile(String filename) {
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            throw new IllegalArgumentException("Invalid filename: " + filename);
        }

        File file = new File(dataDirectory, filename);
        if (!file.exists()) {
            throw new ResourceNotFoundException("File not found: " + filename);
        }

        log.info("Cache miss — loading Excel file from disk: {}", file.getAbsolutePath());

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            List<Map<String, Object>> result = new ArrayList<>();

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) return result;

            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(cell.getStringCellValue().trim());
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Map<String, Object> rowMap = new LinkedHashMap<>();
                for (int j = 0; j < headers.size(); j++) {
                    Cell cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    rowMap.put(headers.get(j), getCellValue(cell));
                }
                result.add(rowMap);
            }

            log.info("Loaded {} rows from {} — stored in cache", result.size(), filename);
            return result;

        } catch (IOException e) {
            log.error("Failed to read file: {}", filename, e);
            throw new RuntimeException("Failed to read file: " + filename, e);
        }
    }

    /** Remove a single file from the cache so the next load re-reads from disk. */
    @CacheEvict(value = "fileData", key = "#filename")
    public void evictCache(String filename) {
        log.info("Cache evicted for: {}", filename);
    }

    /** Clear the entire file data cache. */
    @CacheEvict(value = "fileData", allEntries = true)
    public void evictAllCaches() {
        log.info("All file data caches cleared");
    }

    private Object getCellValue(Cell cell) {
        return switch (cell.getCellType()) {
            case STRING  -> cell.getStringCellValue();
            case NUMERIC -> DateUtil.isCellDateFormatted(cell)
                    ? cell.getLocalDateTimeCellValue().toLocalDate().format(DATE_FORMAT)
                    : cell.getNumericCellValue();
            case BOOLEAN -> cell.getBooleanCellValue();
            case FORMULA -> cell.getCachedFormulaResultType() == CellType.NUMERIC
                    ? cell.getNumericCellValue()
                    : cell.getStringCellValue();
            default      -> "";
        };
    }
}
