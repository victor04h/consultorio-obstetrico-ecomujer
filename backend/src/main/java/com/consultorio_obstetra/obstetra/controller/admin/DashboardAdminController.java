package com.consultorio_obstetra.obstetra.controller.admin;

import com.consultorio_obstetra.obstetra.dto.admin.dashboard.ResumenDashboardResponse;
import com.consultorio_obstetra.obstetra.service.admin.DashboardAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/resumen")
@RequiredArgsConstructor
public class DashboardAdminController {

    private final DashboardAdminService dashboardAdminService;

    @GetMapping
    public ResponseEntity<ResumenDashboardResponse> resumen() {
        return ResponseEntity.ok(dashboardAdminService.resumen());
    }
}
