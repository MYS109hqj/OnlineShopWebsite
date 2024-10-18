package com.example.demo.controller;

import com.example.demo.service.SalesReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequestMapping("/api/sales-report")
public class SalesReportController {

    @Autowired
    private SalesReportService salesReportService;

    @GetMapping
    public Map<String, Object> getSalesReport(
            @RequestParam Long merchantId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        return salesReportService.generateSalesReport(merchantId, startDate, endDate);
    }

    @GetMapping("/weekly")
    public Map<String, Object> getWeeklyReport(
            @RequestParam Long merchantId,
            @RequestParam String weekStart) {
        LocalDateTime weekStartDate = LocalDateTime.parse(weekStart, DateTimeFormatter.ISO_DATE_TIME);
        return salesReportService.generateWeeklyReport(merchantId, weekStartDate);
    }
}
