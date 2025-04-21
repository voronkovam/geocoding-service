package com.voronkovam.geocoding_service.model;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO для обратного геокодирования.
 * Используется в запросах, где необходимо получить адрес по координатам.
 */
@Data
public class ReverseGeocodingRequest {

    /**
     * Широта точки, для которой нужно определить адрес.
     * Не может быть null.
     * Должна быть в диапазоне [-90.0, 90.0]
     */
    @NotNull(message = "Широта обязательна")
    @DecimalMin(value = "-90.0", message = "Широта должна быть не менее -90.0")
    @DecimalMax(value = "90.0", message = "Широта должна быть не более 90.0")
    private Double lat;

    /**
     * Долгота точки, для которой нужно определить адрес.
     * Не может быть null.
     * Должна быть в диапазоне [-180.0, 180.0]
     */
    @NotNull(message = "Долгота обязательна")
    @DecimalMin(value = "-180.0", message = "Долгота должна быть не менее -180.0")
    @DecimalMax(value = "180.0", message = "Долгота должна быть не более 180.0")
    private Double lon;

    public ReverseGeocodingRequest() {}

    public ReverseGeocodingRequest(Double lat, Double lon) {
        this.lat = lat;
        this.lon = lon;
    }
}
