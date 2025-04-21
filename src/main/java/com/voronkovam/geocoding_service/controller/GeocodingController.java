package com.voronkovam.geocoding_service.controller;

import com.voronkovam.geocoding_service.model.DirectGeocodingRequest;
import com.voronkovam.geocoding_service.model.GeocodingResponse;
import com.voronkovam.geocoding_service.model.ReverseGeocodingRequest;
import com.voronkovam.geocoding_service.service.GeocodingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для обработки запросов геокодирования (адрес - координаты).
 */
@RestController
@RequestMapping("/api/geocode")
@Tag(name = "Geocoding Controller", description = "Операции геокодирования и обратного геокодирования")
public class GeocodingController {

    private final GeocodingService service;

    public GeocodingController(GeocodingService service) {
        this.service = service;
    }

    /**
     * Прямое геокодирование: преобразует адрес в координаты (широту/долготу).
     *
     * @param request Запрос с адресом (поле {@code address}).
     * @return Ответ с координатами и адресом.
     */
    @Operation(summary = "Прямое геокодирование", description = "Получение координат по адресу")
    @PostMapping("/direct")
    public ResponseEntity<GeocodingResponse> direct(@Valid @RequestBody DirectGeocodingRequest request) {
        if (request.getAddress() == null || request.getAddress().isBlank()) {
            throw new IllegalArgumentException("Поле 'address' обязательно для прямого геокодирования");
        }
        GeocodingResponse response = service.geocode(request.getAddress());
        return ResponseEntity.ok(response);
    }

    /**
     * Обратное геокодирование: преобразует координаты в адрес.
     *
     * @param request Запрос с широтой ({@code lat}) и долготой ({@code lon}).
     * @return Ответ с нормализованным адресом.
     */
    @Operation(summary = "Обратное геокодирование", description = "Получение адреса по координатам")
    @PostMapping("/reverse")
    public ResponseEntity<GeocodingResponse> reverse(@Valid @RequestBody ReverseGeocodingRequest request) {
        if (request.getLat() == null || request.getLon() == null) {
            throw new IllegalArgumentException("Поля 'lat' и 'lon' обязательны для обратного геокодирования");
        }
        GeocodingResponse response = service.reverseGeocode(request.getLat(), request.getLon());
        return ResponseEntity.ok(response);
    }
}
