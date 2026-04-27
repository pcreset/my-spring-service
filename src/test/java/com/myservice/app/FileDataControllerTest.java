package com.myservice.app;

import com.myservice.app.service.FileDataService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FileDataControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileDataService fileDataService;

    @Test
    void listFiles_returnsFileList() throws Exception {
        when(fileDataService.listFiles()).thenReturn(List.of("sales.xlsx", "report.xlsx"));

        mockMvc.perform(get("/api/v1/data/files"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0]").value("sales.xlsx"))
                .andExpect(jsonPath("$.data[1]").value("report.xlsx"));
    }

    @Test
    void loadFile_returnsRows() throws Exception {
        List<Map<String, Object>> rows = List.of(
                Map.of("Name", "Alice", "Age", 30),
                Map.of("Name", "Bob", "Age", 25)
        );
        when(fileDataService.loadFile("test.xlsx")).thenReturn(rows);

        mockMvc.perform(get("/api/v1/data/load?file=test.xlsx"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].Name").value("Alice"))
                .andExpect(jsonPath("$.data[1].Name").value("Bob"));
    }

    @Test
    void loadFile_invalidFilename_returns400() throws Exception {
        when(fileDataService.loadFile("../secret.xlsx"))
                .thenThrow(new IllegalArgumentException("Invalid filename: ../secret.xlsx"));

        mockMvc.perform(get("/api/v1/data/load?file=../secret.xlsx"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void loadFile_fileNotFound_returns404() throws Exception {
        when(fileDataService.loadFile("missing.xlsx"))
                .thenThrow(new com.myservice.app.exception.ResourceNotFoundException("File not found: missing.xlsx"));

        mockMvc.perform(get("/api/v1/data/load?file=missing.xlsx"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }
}
