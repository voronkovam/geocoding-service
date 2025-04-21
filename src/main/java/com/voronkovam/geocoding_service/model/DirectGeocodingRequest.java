package com.voronkovam.geocoding_service.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
/**
 * DTO для прямого геокодирования.
 * Используется в запросах, где необходимо преобразовать адрес в координаты.
 */
@Data
public class DirectGeocodingRequest {

    /**
     * Адрес, который требуется преобразовать в координаты.
     * Не может быть пустым.
     */
    @NotBlank(message = "Адрес не должен быть пустым")
    private String address;

    public DirectGeocodingRequest() {}

    public DirectGeocodingRequest(String address) {
        this.address = address;
    }
}

