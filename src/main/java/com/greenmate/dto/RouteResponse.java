package com.greenmate.dto;

import com.greenmate.entity.Route;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteResponse {
    private List<Route> routes;
    
    public static RouteResponse of(List<Route> routes) {
        return new RouteResponse(routes);
    }
}