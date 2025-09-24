package com.kgyp.kgypsystem.controller;

import com.kgyp.kgypsystem.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/kpi")
    public ResponseEntity<Map<String, Object>> getKPIDashboard() {
        Map<String, Object> kpiData = dashboardService.getKPIData();
        return ResponseEntity.ok(kpiData);
    }

    @GetMapping("/son-aktiviteler")
    public ResponseEntity<Object> getSonAktiviteler() {
        return ResponseEntity.ok(dashboardService.getSonAktiviteler());
    }

    @GetMapping("/kritik-uyarilar")
    public ResponseEntity<Object> getKritikUyarilar() {
        return ResponseEntity.ok(dashboardService.getKritikUyarilar());
    }

    @GetMapping("/performans-metrikleri")
    public ResponseEntity<Object> getPerformansMetrikleri() {
        return ResponseEntity.ok(dashboardService.getPerformansMetrikleri());
    }
}