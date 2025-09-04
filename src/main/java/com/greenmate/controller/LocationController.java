package com.greenmate.controller;

import com.greenmate.entity.Location;
import com.greenmate.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LocationController {
    
    private final LocationService locationService;
    
    @GetMapping("/search")
    public ResponseEntity<List<Location>> searchLocations(@RequestParam String query) {
        List<Location> locations = locationService.searchLocations(query);
        return ResponseEntity.ok(locations);
    }
    
    @GetMapping("/autocomplete")
    public ResponseEntity<List<Location>> getAutocompleteSuggestions(
            @RequestParam String query,
            @RequestParam(defaultValue = "5") int limit) {
        List<Location> suggestions = locationService.getAutocompleteSuggestions(query, limit);
        return ResponseEntity.ok(suggestions);
    }
}