package com.greenmate.controller;

import com.greenmate.dto.RouteRequest;
import com.greenmate.dto.RouteResponse;
import com.greenmate.entity.Route;
import com.greenmate.service.RouteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RouteController {
    
    private final RouteService routeService;
    
    @PostMapping("/search")
    public ResponseEntity<RouteResponse> getRouteOptions(@Valid @RequestBody RouteRequest request) {
        List<Route> routes = routeService.getRouteOptions(request);
        return ResponseEntity.ok(RouteResponse.of(routes));
    }
}