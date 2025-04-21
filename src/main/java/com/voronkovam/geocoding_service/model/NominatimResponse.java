package com.voronkovam.geocoding_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//  Вспомогательный DTO для разбора ответа от Nominatim
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NominatimResponse {
    private String display_name;
    private double lat;
    private double lon;


}
