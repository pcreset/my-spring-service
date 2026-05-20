package com.myservice.app.model;

import java.util.Map;

public record UpdateRowRequest(String file, int rowIndex, Map<String, Object> changes) {}