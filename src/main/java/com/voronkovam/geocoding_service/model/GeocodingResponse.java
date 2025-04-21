package com.voronkovam.geocoding_service.model;


import lombok.Data;
//DTO класс для ответа
@Data
public class GeocodingResponse {
    private String address;
    private Double lat;
    private Double lon;
}
