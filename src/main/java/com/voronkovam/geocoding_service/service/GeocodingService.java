package com.voronkovam.geocoding_service.service;

import com.voronkovam.geocoding_service.exception.GeocodingException;
import com.voronkovam.geocoding_service.model.CachedLocation;
import com.voronkovam.geocoding_service.model.GeocodingResponse;
import com.voronkovam.geocoding_service.model.NominatimResponse;
import com.voronkovam.geocoding_service.repository.CachedLocationRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Сервис для геокодирования с кэшированием результатов в БД.
 */

@Service
public class GeocodingService {

    private final RestTemplate restTemplate;
    private final CachedLocationRepository repository;
    private final Logger logger = LoggerFactory.getLogger(GeocodingService.class);

    @Value("${external.api.url}")
    private String externalApiUrl;

    public GeocodingService(RestTemplate restTemplate, CachedLocationRepository repository) {
        this.restTemplate = restTemplate;
        this.repository = repository;
    }

    /**
     * Преобразует адрес в координаты.
     * Сначала проверяет кэш, затем вызывает внешний API (если необходимо).
     *
     * @param address Адрес для геокодирования.
     * @return Ответ с координатами.
     * @throws GeocodingException если адрес не найден.
     */
    public GeocodingResponse geocode(String address) {
        return repository.findByAddress(address).map(this::toResponse)
                .orElseGet(() -> {
                    logger.info("Отправка запроса к внешнему API: {}", address);
                    String url = externalApiUrl + "/search?q=" + address + "&format=json&limit=1";
                    // тут разбираем JSON, сохраняем в БД и возвращаем
                    ResponseEntity<NominatimResponse[]> response = restTemplate.getForEntity(url, NominatimResponse[].class);
                    if (response.getBody() != null && response.getBody().length > 0) {
                        NominatimResponse nominatim = response.getBody()[0];
                        Double lat = Double.parseDouble(String.valueOf(nominatim.getLat()));
                        Double lon = Double.parseDouble(String.valueOf(nominatim.getLon()));

                        CachedLocation saved = new CachedLocation();
                        saved.setAddress(address);
                        saved.setLat(round(lat, 5));
                        saved.setLon(round(lon, 5));
                        repository.save(saved);

                        return toResponse(saved);
                    } else {
                        logger.warn("Ничего не найдено по адресу: {}", address);
                        throw new GeocodingException("Адрес не найден");
                    }
                });
    }

    /**
     * Преобразует координаты в адрес.
     * Сначала проверяет кэш, затем вызывает внешний API (если необходимо).
     *
     * @param lat Широта.
     * @param lon Долгота.
     * @return Ответ с адресом.
     * @throws GeocodingException если координаты не найдены.
     */
    public GeocodingResponse reverseGeocode(Double lat, Double lon) {
        double roundedLat = round(lat, 5);
        double roundedLon = round(lon, 5);

        return repository.findByLatAndLon(roundedLat, roundedLon).map(this::toResponse)
                .orElseGet(() -> {
                    logger.info("Обратный запрос по координатам: {}, {}", roundedLat, roundedLon);
                    String url = externalApiUrl + "/reverse?lat=" + roundedLat + "&lon=" + roundedLon + "&format=json";
                    ResponseEntity<NominatimResponse> response = restTemplate.getForEntity(url, NominatimResponse.class);

                    NominatimResponse nominatim = response.getBody();
                    if (nominatim != null && nominatim.getDisplay_name() != null) {
                        CachedLocation saved = new CachedLocation();
                        saved.setAddress(nominatim.getDisplay_name());
                        saved.setLat(roundedLat);
                        saved.setLon(roundedLon);
                        repository.save(saved);

                        return toResponse(saved);
                    } else {
                        logger.warn("Ничего не найдено по координатам: {}, {}", roundedLat, roundedLon);
                        throw new GeocodingException("Координаты не найдены");
                    }
                });
    }


    /**
     * Округляет число до указанного количества знаков после запятой.
     *
     * @param value  Исходное число для округления.
     * @param places Количество знаков после запятой (должно быть >= 0).
     * @return Округленное значение.
     * @throws IllegalArgumentException если {@code places} отрицательное.
     */
    private double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException("Places must be >= 0");
        }
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }

    /**
     * Конвертирует сущность {@link CachedLocation} в DTO {@link GeocodingResponse}.
     *
     * @param loc Сущность с данными из кэша.
     * @return DTO с адресом и координатами.
     */
    private GeocodingResponse toResponse(CachedLocation loc) {
        GeocodingResponse resp = new GeocodingResponse();
        resp.setAddress(loc.getAddress());
        resp.setLat(loc.getLat());
        resp.setLon(loc.getLon());
        return resp;
    }
}


