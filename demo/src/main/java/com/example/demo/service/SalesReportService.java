package com.example.demo.service;

import com.example.demo.model.Order;
import com.example.demo.model.OrderItem;
import com.example.demo.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SalesReportService {

    @Autowired
    private OrderRepository orderRepository;

    public Map<String, Object> generateSalesReport(Long merchantId, String startDate, String endDate) {
        LocalDateTime start = LocalDateTime.parse(startDate, DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime end = LocalDateTime.parse(endDate, DateTimeFormatter.ISO_DATE_TIME);
        
        List<Order> orders = orderRepository.findByMerchantIdAndOrderDateBetween(merchantId, start, end);
        
        BigDecimal totalSales = BigDecimal.ZERO;
        int totalOrders = orders.size();
        Map<Long, BigDecimal> productSalesMap = new HashMap<>();
        Map<Long, Integer> customerPurchaseCount = new HashMap<>();
        
        for (Order order : orders) {
            for (OrderItem item : order.getOrderItems()) {
                totalSales = totalSales.add(item.getTotalPrice());
                productSalesMap.put(item.getProduct().getId(), 
                    productSalesMap.getOrDefault(item.getProduct().getId(), BigDecimal.ZERO).add(item.getTotalPrice()));
            }
            customerPurchaseCount.put(order.getCustomer().getId(), 
                customerPurchaseCount.getOrDefault(order.getCustomer().getId(), 0) + 1);
        }

        Map<String, Object> report = new HashMap<>();
        report.put("totalSales", totalSales);
        report.put("totalOrders", totalOrders);
        report.put("productSales", productSalesMap);
        report.put("customerPurchaseCount", customerPurchaseCount);
        
        return report;
    }

    public Map<String, Object> generateWeeklyReport(Long merchantId, LocalDateTime weekStart) {
        LocalDateTime weekEnd = weekStart.plusDays(6); // 本周的结束日期
        LocalDateTime previousWeekStart = weekStart.minusWeeks(1);
        LocalDateTime previousWeekEnd = weekStart.minusDays(1); // 上周的结束日期
        LocalDateTime nextWeekStart = weekStart.plusWeeks(1);
        LocalDateTime nextWeekEnd = weekStart.plusDays(6); // 下周的结束日期

        Map<String, Object> currentWeekReport = generateSalesReport(merchantId, weekStart.toString(), weekEnd.toString());
        Map<String, Object> previousWeekReport = generateSalesReport(merchantId, previousWeekStart.toString(), previousWeekEnd.toString());
        Map<String, Object> nextWeekReport = generateSalesReport(merchantId, nextWeekStart.toString(), nextWeekEnd.toString());

        Map<String, Object> weeklyReport = new HashMap<>();
        weeklyReport.put("currentWeek", currentWeekReport);
        weeklyReport.put("previousWeek", previousWeekReport);
        weeklyReport.put("nextWeek", nextWeekReport);

        return weeklyReport;
    }
}
