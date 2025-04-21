package com.voronkovam.geocoding_service.service;

import com.voronkovam.geocoding_service.exception.GeocodingException;
import com.voronkovam.geocoding_service.model.CachedLocation;
import com.voronkovam.geocoding_service.model.GeocodingResponse;
import com.voronkovam.geocoding_service.model.NominatimResponse;
import com.voronkovam.geocoding_service.repository.CachedLocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class GeocodingServiceTest {

    @Mock
    private CachedLocationRepository repository;
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private GeocodingService service;
    private final String apiUrl = "https://nominatim.openstreetmap.org";

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(service, "externalApiUrl", apiUrl);
    }

    @Test
    public void testGeocode_FromCache() {
        String address = "Москва";
        CachedLocation cached = new CachedLocation();
        cached.setAddress(address);
        cached.setLat(55.62558);
        cached.setLon(37.60639);

        Mockito.when(repository.findByAddress(address)).thenReturn(Optional.of(cached));

        GeocodingResponse response = service.geocode(address);

        assertEquals(address, response.getAddress());
        assertEquals(55.62558, response.getLat());
        assertEquals(37.60639, response.getLon());
    }

    @Test
    public void testGeocode_FromExternalApi() {
        String address = "Москва";
        String url = apiUrl + "/search?q=" + address + "&format=json&limit=1";

        NominatimResponse[] nominatimResponses = {
                new NominatimResponse("Москва, Россия", 55.7558, 37.6173)
        };

        Mockito.when(repository.findByAddress(address)).thenReturn(Optional.empty());
        Mockito.when(restTemplate.getForEntity(url, NominatimResponse[].class))
                .thenReturn(new ResponseEntity<>(nominatimResponses, HttpStatus.OK));
        Mockito.when(repository.save(Mockito.any(CachedLocation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        GeocodingResponse response = service.geocode(address);

        assertEquals(address, response.getAddress());
        assertEquals(55.7558, response.getLat());
        assertEquals(37.6173, response.getLon());
    }

    @Test
    public void testGeocode_NotFound() {
        String address = "Несуществующий адрес";
        String url = apiUrl + "/search?q=" + address + "&format=json&limit=1";

        Mockito.when(repository.findByAddress(address)).thenReturn(Optional.empty());
        Mockito.when(restTemplate.getForEntity(url, NominatimResponse[].class))
                .thenReturn(new ResponseEntity<>(new NominatimResponse[0], HttpStatus.OK));

        assertThrows(GeocodingException.class, () -> service.geocode(address));
    }



    @Test
    public void testReverseGeocode_FromCache() {
        double lat = 55.75;
        double lon = 37.62;

        CachedLocation cached = new CachedLocation();
        cached.setAddress("Москва, Россия");
        cached.setLat(lat);
        cached.setLon(lon);

        Mockito.when(repository.findByLatAndLon(lat, lon)).thenReturn(Optional.of(cached));

        GeocodingResponse response = service.reverseGeocode(lat, lon);

        assertEquals("Москва, Россия", response.getAddress());
        assertEquals(lat, response.getLat());
        assertEquals(lon, response.getLon());
    }

    @Test
    public void testReverseGeocode_FromExternalApi() {
        double lat = 55.75;
        double lon = 37.62;
        String url = apiUrl + "/reverse?lat=" + lat + "&lon=" + lon + "&format=json";

        NominatimResponse nominatimResponse = new NominatimResponse();
        nominatimResponse.setDisplay_name("Москва, Россия");
        nominatimResponse.setLat(lat);
        nominatimResponse.setLon(lon);

        Mockito.when(repository.findByLatAndLon(lat, lon)).thenReturn(Optional.empty());
        Mockito.when(restTemplate.getForEntity(url, NominatimResponse.class))
                .thenReturn(new ResponseEntity<>(nominatimResponse, HttpStatus.OK));
        Mockito.when(repository.save(Mockito.any(CachedLocation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        GeocodingResponse response = service.reverseGeocode(lat, lon);

        assertEquals("Москва, Россия", response.getAddress());
        assertEquals(lat, response.getLat());
        assertEquals(lon, response.getLon());
    }

    @Test
    public void testReverseGeocode_NotFound() {
        double lat = 0.0;
        double lon = 0.0;
        String url = apiUrl + "/reverse?lat=" + lat + "&lon=" + lon + "&format=json";

        NominatimResponse nominatimResponse = new NominatimResponse(); // display_name = null

        Mockito.when(repository.findByLatAndLon(lat, lon)).thenReturn(Optional.empty());
        Mockito.when(restTemplate.getForEntity(url, NominatimResponse.class))
                .thenReturn(new ResponseEntity<>(nominatimResponse, HttpStatus.OK));

        assertThrows(GeocodingException.class, () -> service.reverseGeocode(lat, lon));
    }


}