package com.voronkovam.geocoding_service.repository;

import com.voronkovam.geocoding_service.model.CachedLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CachedLocationRepository extends JpaRepository<CachedLocation, Long> {
    Optional<CachedLocation> findByAddress(String address);
    Optional<CachedLocation> findByLatAndLon(Double lat, Double lon);


}
