package com.voronkovam.geocoding_service.controller;

import com.voronkovam.geocoding_service.model.CachedLocation;
import com.voronkovam.geocoding_service.model.DirectGeocodingRequest;
import com.voronkovam.geocoding_service.model.GeocodingResponse;
import com.voronkovam.geocoding_service.model.ReverseGeocodingRequest;
import com.voronkovam.geocoding_service.repository.CachedLocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GeocodingControllerIT {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CachedLocationRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void testDirectGeocode_FromExternalApi() {
        DirectGeocodingRequest request = new DirectGeocodingRequest();
        request.setAddress("Москва");

        ResponseEntity<GeocodingResponse> response = restTemplate.postForEntity(
                "/api/geocode/direct",
                request,
                GeocodingResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        GeocodingResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("Москва", body.getAddress());
        assertNotNull(body.getLat());
        assertNotNull(body.getLon());

        // Проверка на кэш
        Optional<CachedLocation> cached = repository.findByAddress("Москва");
        assertTrue(cached.isPresent());
    }

    @Test
    void testReverseGeocode_FromExternalApi() {
        ReverseGeocodingRequest request = new ReverseGeocodingRequest();
        request.setLat(55.75);
        request.setLon(37.62);

        ResponseEntity<GeocodingResponse> response = restTemplate.postForEntity(
                "/api/geocode/reverse",
                request,
                GeocodingResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        GeocodingResponse body = response.getBody();
        assertNotNull(body);
        assertTrue(body.getAddress().toLowerCase().contains("москва"));
        assertEquals(55.75, body.getLat());
        assertEquals(37.62, body.getLon());

        Optional<CachedLocation> cached = repository.findByLatAndLon(55.75, 37.62);
        assertTrue(cached.isPresent());
    }

    @Test
    void testGeocode_NotFound() {
        DirectGeocodingRequest request = new DirectGeocodingRequest();
        request.setAddress("несуществующий адрес");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/geocode/direct",
                request,
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Адрес не найден"));
    }

}