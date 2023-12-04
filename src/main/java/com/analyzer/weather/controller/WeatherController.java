package com.analyzer.weather.controller;

import com.analyzer.weather.entity.WeatherEntity;
import com.analyzer.weather.exception.WeatherAlreadyExists;
import com.analyzer.weather.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;


@RestController
@RequestMapping("/weather")
public class WeatherController {

    private final WeatherService weatherService;

    @Autowired
    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    /**
     * Handles HTTP GET requests to retrieve weather data by ID.
     *
     * @param id The ID of the weather data to retrieve.
     * @return ResponseEntity with the retrieved weather data or a bad request response if not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity getWeather(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(weatherService.getWeather(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    /**
     * Handles HTTP GET requests to retrieve all weather data.
     *
     * @return ResponseEntity with all retrieved weather data or a bad request response if not found.
     */
    @GetMapping("/all")
    public ResponseEntity getAllWeather() {
        try {
            return ResponseEntity.ok(weatherService.getAllWeather());
        } catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    /**
     * Handles HTTP GET requests to retrieve weather data within a specified time range.
     *
     * @param startDateTime The start date and time of the time range.
     * @param endDateTime   The end date and time of the time range.
     * @return ResponseEntity with the retrieved weather data or a bad request response if not found.
     */
    @GetMapping("/history")
    public ResponseEntity getWeatherInTimeRange(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTime,
                                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDateTime) {
        try {
            return ResponseEntity.ok(weatherService.getWeatherInTimeRange(startDateTime, endDateTime));
        } catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    /**
     * Handles HTTP GET requests to retrieve weather data within a specified date range.
     *
     * @param startDate The start date of the date range.
     * @param endDate   The end date of the date range.
     * @return ResponseEntity with the retrieved weather data or a bad request response if not found.
     */
    @GetMapping("/historyByDate")
    public ResponseEntity getWeatherInDateRange(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            return ResponseEntity.ok(weatherService.getWeatherInDateRange(startDate, endDate));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    /**
     * Handles HTTP GET requests to retrieve the latest weather data.
     *
     * @return ResponseEntity with the retrieved latest weather data or a bad request response if not found.
     */
    @GetMapping("/latest")
    public ResponseEntity getLatestWeather() {
        try {
            return ResponseEntity.ok(weatherService.getLatestWeather());
        } catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    /**
     * Handles HTTP POST requests to add new weather data.
     *
     * @param weather The weather data to be added.
     * @return ResponseEntity with a success message or a bad request response if data already exists.
     */
    @PostMapping
    public ResponseEntity postWeather(@RequestBody WeatherEntity weather) {
        try {
            weatherService.postWeather(weather);
            return ResponseEntity.ok("Weather was added");
        } catch (WeatherAlreadyExists e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}