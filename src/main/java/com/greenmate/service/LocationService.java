package com.greenmate.service;

import com.greenmate.entity.Location;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LocationService {
    
    private static final List<Location> MOCK_LOCATIONS = initializeMockLocations();
    
    public List<Location> searchLocations(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        return MOCK_LOCATIONS.stream()
                .filter(location -> 
                    location.getName().toLowerCase().contains(query.toLowerCase()) ||
                    location.getAddress().toLowerCase().contains(query.toLowerCase())
                )
                .limit(10)
                .collect(Collectors.toList());
    }
    
    public List<Location> getAutocompleteSuggestions(String query, int limit) {
        return searchLocations(query).stream()
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    private static List<Location> initializeMockLocations() {
        List<Location> locations = new ArrayList<>();
        
        // Seoul landmarks
        locations.add(Location.builder()
                .name("강남역")
                .address("서울특별시 강남구 강남대로 390")
                .latitude(37.4981)
                .longitude(127.0276)
                .build());
        
        locations.add(Location.builder()
                .name("홍대입구역")
                .address("서울특별시 마포구 양화로 160")
                .latitude(37.5563)
                .longitude(126.9244)
                .build());
        
        locations.add(Location.builder()
                .name("명동")
                .address("서울특별시 중구 명동길 26")
                .latitude(37.5636)
                .longitude(126.9866)
                .build());
        
        locations.add(Location.builder()
                .name("이태원")
                .address("서울특별시 용산구 이태원로 200")
                .latitude(37.5347)
                .longitude(126.9947)
                .build());
        
        locations.add(Location.builder()
                .name("종로3가")
                .address("서울특별시 종로구 종로 115")
                .latitude(37.5703)
                .longitude(126.9929)
                .build());
        
        locations.add(Location.builder()
                .name("한강공원")
                .address("서울특별시 영등포구 여의도동")
                .latitude(37.5290)
                .longitude(126.9340)
                .build());
        
        locations.add(Location.builder()
                .name("경복궁")
                .address("서울특별시 종로구 사직로 161")
                .latitude(37.5788)
                .longitude(126.9770)
                .build());
        
        locations.add(Location.builder()
                .name("동대문")
                .address("서울특별시 중구 장충단로 247")
                .latitude(37.5663)
                .longitude(127.0090)
                .build());
        
        locations.add(Location.builder()
                .name("신촌")
                .address("서울특별시 서대문구 신촌로 90")
                .latitude(37.5596)
                .longitude(126.9430)
                .build());
        
        locations.add(Location.builder()
                .name("건대입구")
                .address("서울특별시 광진구 능동로 120")
                .latitude(37.5401)
                .longitude(127.0708)
                .build());
        
        return locations;
    }
}