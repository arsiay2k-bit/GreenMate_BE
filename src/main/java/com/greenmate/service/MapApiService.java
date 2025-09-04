package com.greenmate.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class MapApiService {
    
    private final WebClient webClient;
    
    @Value("${maps.api.key:your-api-key}")
    private String apiKey;
    
    @Value("${maps.api.base-url:https://maps.googleapis.com/maps/api}")
    private String baseUrl;
    
    public MapApiService() {
        this.webClient = WebClient.builder().build();
    }
    
    public Mono<String> getDirections(double startLat, double startLng, double endLat, double endLng) {
        String url = String.format("%s/directions/json?origin=%f,%f&destination=%f,%f&mode=walking&key=%s",
                baseUrl, startLat, startLng, endLat, endLng, apiKey);
        
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorReturn("Error fetching directions");
    }
    
    public Mono<String> geocodeAddress(String address) {
        String url = String.format("%s/geocode/json?address=%s&key=%s",
                baseUrl, address, apiKey);
        
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorReturn("Error geocoding address");
    }
    
    public Mono<String> searchPlaces(String query, double lat, double lng, int radius) {
        String url = String.format("%s/place/nearbysearch/json?location=%f,%f&radius=%d&keyword=%s&key=%s",
                baseUrl, lat, lng, radius, query, apiKey);
        
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorReturn("Error searching places");
    }
}