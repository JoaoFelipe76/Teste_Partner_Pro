package br.com.partnerpro.product_manager.framework.controller;

import br.com.partnerpro.product_manager.application.usecase.DashboardUseCase;
import br.com.partnerpro.product_manager.framework.dto.DashboardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DashboardController {
    
    private final DashboardUseCase dashboardUseCase;
    
    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard() {
        return ResponseEntity.ok(dashboardUseCase.getDashboardData());
    }
}
