package com.analyzer.weather.repository;

import com.analyzer.weather.entity.WeatherEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Database repository interface for managing WeatherEntity entities. Extends Spring Data JPA's CrudRepository
 * to provide basic CRUD operations and additional custom queries.
 *
 * Custom Queries:
 * - findByLocationAndDateTime: Retrieves a WeatherEntity by location and date/time.
 * - findByPostDateBetween: Retrieves a list of WeatherEntity objects within a specified date range.
 * - findFirstByOrderByIdDesc: Retrieves the latest WeatherEntity by ordering results based on the ID in descending order.
 * - findByDateTimeBetween: Retrieves a list of WeatherEntity objects within a specified date and time range.
 *
 * Usage:
 * - Extend this interface in a service or component to interact with the WeatherEntity data in the database.
 * - Leverage the provided custom queries for specific retrieval needs.
 */
public interface WeatherRepo extends CrudRepository<WeatherEntity, Long> {
    WeatherEntity findByLocationAndDateTime(String location, LocalDateTime dateTime);
    @Query("SELECT w FROM WeatherEntity w WHERE w.dateTime BETWEEN ?1 AND ?2")
    List<WeatherEntity> findByPostDateBetween(Date startDate, Date endDate);
    Optional<WeatherEntity> findFirstByOrderByIdDesc();
    List<WeatherEntity> findByDateTimeBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
}
