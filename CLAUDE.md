# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

GreenMate_BE is a Spring Boot backend service for the LifePlus GreenMate walking route recommendation app. The service provides APIs for route planning, location search, and walk record management to support a mobile app that promotes walking habits and ESG activities.

## Key Features

- **Route Recommendation**: Provides 3 walking route options (Less Walk, Recommended, More Walk) between start and end points
- **Location Search**: Autocomplete search for destinations with mock Seoul landmark data
- **Walk Records**: Store and retrieve walking history with statistics
- **Maps Integration**: Prepared for external Maps API integration (Google Maps)

## Commands

```bash
# Run the application
./gradlew bootRun

# Build the project
./gradlew build

# Compile only
./gradlew compileJava

# Run tests
./gradlew test

# Clean build
./gradlew clean build

# Access H2 Database Console
# http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:mem:greenmate
# Username: sa
# Password: (empty)
```

## Architecture

### Project Structure
```
src/main/java/com/greenmate/
├── GreenMateApplication.java     # Main application entry point
├── entity/                       # JPA entities
│   ├── Location.java            # Location data structure
│   ├── Route.java               # Route information
│   └── WalkRecord.java          # Walk history records
├── dto/                         # Data Transfer Objects
│   ├── RouteRequest.java        # Route search request
│   ├── RouteResponse.java       # Route search response
│   └── WalkRecordRequest.java   # Walk record creation request
├── controller/                  # REST Controllers
│   ├── RouteController.java     # Route recommendation endpoints
│   ├── LocationController.java  # Location search endpoints
│   └── WalkRecordController.java # Walk record endpoints
├── service/                     # Business logic
│   ├── RouteService.java        # Route calculation logic
│   ├── LocationService.java     # Location search with mock data
│   ├── WalkRecordService.java   # Walk record management
│   └── MapApiService.java       # External Maps API integration
├── repository/                  # Data access layer
│   └── WalkRecordRepository.java # JPA repository for walk records
└── config/                      # Configuration
    └── WebConfig.java           # CORS and web configuration
```

### API Endpoints

#### Route Recommendation
- `POST /api/routes/search` - Get 3 route options between start/end points

#### Location Search  
- `GET /api/locations/search?query={query}` - Search locations by name/address
- `GET /api/locations/autocomplete?query={query}&limit={limit}` - Autocomplete suggestions

#### Walk Records
- `POST /api/walk-records` - Save completed walk record
- `GET /api/walk-records` - Get all walk records
- `GET /api/walk-records/{id}` - Get specific walk record
- `GET /api/walk-records/date-range?startDate={date}&endDate={date}` - Filter by date range
- `GET /api/walk-records/statistics` - Get walk statistics summary

### Database

Uses H2 in-memory database for development. Walk records are persisted in `walk_records` table with the following fields:
- Route coordinates (start/end lat/lng)
- Addresses, distance, duration, steps
- Route type and creation timestamp

### Configuration

- **Port**: 8080
- **Database**: H2 in-memory (jdbc:h2:mem:greenmate)  
- **CORS**: Enabled for all origins on /api/** endpoints
- **Logging**: DEBUG level for application packages
- **H2 Console**: Available at /h2-console

### External Integrations

`MapApiService` is prepared for Google Maps API integration:
- Directions API for real route calculation
- Geocoding API for address-to-coordinates conversion  
- Places API for location search

Configure Maps API key in application.yml:
```yaml
maps:
  api:
    key: your-google-maps-api-key
    base-url: https://maps.googleapis.com/maps/api
```