package com.greenmate.service;

import com.greenmate.dto.RouteRequest;
import com.greenmate.entity.Location;
import com.greenmate.entity.Route;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class RouteService {
    
    private static final double AVERAGE_WALKING_SPEED_KM_PER_MINUTE = 0.05; // 3km/h = 0.05km/min
    private static final int STEPS_PER_KM = 1250; // Average steps per kilometer
    
    public List<Route> getRouteOptions(RouteRequest request) {
        Location startLocation = Location.builder()
                .latitude(request.getStartLatitude())
                .longitude(request.getStartLongitude())
                .address(request.getStartAddress())
                .build();
                
        Location endLocation = Location.builder()
                .latitude(request.getEndLatitude())
                .longitude(request.getEndLongitude())
                .address(request.getEndAddress())
                .build();
        
        double baseDistance = calculateDistance(
            request.getStartLatitude(), request.getStartLongitude(),
            request.getEndLatitude(), request.getEndLongitude()
        );
        
        Route lessWalkRoute = createRoute("LESS_WALK", baseDistance * 1.0, startLocation, endLocation);
        Route recommendedRoute = createRoute("RECOMMENDED", baseDistance * 1.2, startLocation, endLocation);
        Route moreWalkRoute = createRoute("MORE_WALK", baseDistance * 1.5, startLocation, endLocation);
        
        return Arrays.asList(lessWalkRoute, recommendedRoute, moreWalkRoute);
    }
    
    private Route createRoute(String routeType, double distance, Location start, Location end) {
        int duration = (int) (distance / AVERAGE_WALKING_SPEED_KM_PER_MINUTE);
        int steps = (int) (distance * STEPS_PER_KM);
        
        return Route.builder()
                .routeType(routeType)
                .distanceKm(Math.round(distance * 100.0) / 100.0)
                .durationMinutes(duration)
                .estimatedSteps(steps)
                .startLocation(start)
                .endLocation(end)
                .polyline(generateMockPolyline(start, end))
                .build();
    }
    
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
    
    private String generateMockPolyline(Location start, Location end) {
        return String.format("mock_polyline_%f_%f_to_%f_%f", 
            start.getLatitude(), start.getLongitude(),
            end.getLatitude(), end.getLongitude());
    }
}