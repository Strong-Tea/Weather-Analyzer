package com.analyzer.weather.service;

import com.analyzer.weather.entity.WeatherEntity;
import com.analyzer.weather.exception.WeatherAlreadyExists;
import com.analyzer.weather.json.WeatherJson;
import com.analyzer.weather.repository.WeatherRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.ToDoubleFunction;

@Service
@Slf4j
@Component
public class WeatherService {

    private static final long REFRESH_RATE = 60000;

    private final WeatherRepo weatherRepo;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String apiUrl;
    private final String apiKey;
    private final String apiHost;

    @Autowired
    public WeatherService(WeatherRepo weatherRepo, RestTemplate restTemplate, ObjectMapper objectMapper,
                          @Value("${weather.api.url}")  String apiUrl,
                          @Value("${weather.api.key}")  String apiKey,
                          @Value("${weather.api.host}") String apiHost) {
        this.weatherRepo  = weatherRepo;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.apiUrl       = apiUrl;
        this.apiKey       = apiKey;
        this.apiHost      = apiHost;
    }


    /**
     * Scheduled method to fetch weather data from the external API at a fixed rate.
     * Calls the weather API, maps the JSON response to a WeatherEntity, and posts it to the database.
     *
     * @throws WeatherAlreadyExists if the weather data already exists in the database.
     * @throws ResponseStatusException for internal server errors during API calls.
     */
    @Scheduled(fixedRate = REFRESH_RATE)
    public void getWeatherDataFromApi() {
        try {
            HttpHeaders headers = createApiHeaders();
            ResponseEntity<String> response = callWeatherApi(headers);
            WeatherJson weatherJson = mapJsonToWeatherEntity(response.getBody());
            postWeather(weatherJson.convertToWeatherEntity());
        } catch (WeatherAlreadyExists e) {
            log.error(e.getMessage());
        } catch (Exception e) {
            handleApiException(e);
        }
    }


    /**
     * Helper method to create HTTP headers for API requests.
     *
     * @return HttpHeaders containing the necessary API headers.
     */
    private HttpHeaders createApiHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", apiKey);
        headers.set("X-RapidAPI-Host", apiHost);
        return headers;
    }


    /**
     * Helper method to make an HTTP request to the weather API.
     *
     * @param headers HttpHeaders containing API request headers.
     * @return ResponseEntity with the JSON response from the API.
     */
    private ResponseEntity<String> callWeatherApi(HttpHeaders headers) {
        return restTemplate.exchange(apiUrl, HttpMethod.GET, new HttpEntity<>(headers), String.class);
    }


    /**
     * Helper method to map JSON response to a WeatherEntity.
     *
     * @param json JSON response from the weather API.
     * @return WeatherJson mapped to WeatherEntity.
     * @throws RuntimeException if there's an error mapping JSON to WeatherEntity.
     */
    private WeatherJson mapJsonToWeatherEntity(String json) {
        try {
            WeatherJson weatherJson = objectMapper.readValue(json, WeatherJson.class);
            log.info("Output from RapidAPI: {}", json);
            return weatherJson;
        } catch (IOException e) {
            log.error("Error mapping JSON to WeatherEntity", e);
            throw new RuntimeException("Error mapping JSON to WeatherEntity", e);
        }
    }


    /**
     * Helper method to handle exceptions during API calls.
     *
     * @param e Exception thrown during API calls.
     * @throws ResponseStatusException for internal server errors during API calls.
     */
    private void handleApiException(Exception e) {
        log.error("Something went wrong while getting value from RapidAPI", e);
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Exception while calling endpoint of RapidAPI", e);
    }


    /**
     * Helper method to calculate average indicators from a list of WeatherEntity.
     *
     * @param weatherList List of WeatherEntity to calculate averages.
     * @return WeatherEntity with average temperature, wind speed, pressure, and humidity.
     */
    private WeatherEntity calculateAverageIndicators(List<WeatherEntity> weatherList) {
        WeatherEntity averageIndicators = new WeatherEntity();
        if (!weatherList.isEmpty()) {
            float averageTemperature = calculateAverage(weatherList, WeatherEntity::getTemperature);
            float averageWindSpeed = calculateAverage(weatherList, WeatherEntity::getWind);
            float averagePressure = calculateAverage(weatherList, WeatherEntity::getPressure);
            float averageHumidity = calculateAverage(weatherList, WeatherEntity::getHumidity);

            averageIndicators.setTemperature(averageTemperature);
            averageIndicators.setWind(averageWindSpeed);
            averageIndicators.setPressure(averagePressure);
            averageIndicators.setHumidity(averageHumidity);
        }
        return averageIndicators;
    }


    /**
     * Helper method to calculate the average of a specific property in a list of WeatherEntity.
     *
     * @param weatherList List of WeatherEntity.
     * @param mapper      ToDoubleFunction to map WeatherEntity property to double.
     * @return Average value as a float.
     */
    private float calculateAverage(List<WeatherEntity> weatherList, ToDoubleFunction<WeatherEntity> mapper) {
        return (float) weatherList.stream()
                .mapToDouble(mapper)
                .average()
                .orElse(0);
    }


    /**
     * Retrieve weather data by ID.
     *
     * @param id The ID of the weather data to retrieve.
     * @return ResponseEntity with the retrieved weather data or a bad request response if not found.
     */
    public WeatherEntity getWeather(Long id) {
        return weatherRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Weather not found"));
    }


    /**
     * Retrieve all weather data.
     *
     * @return List of WeatherEntity containing all weather data.
     */
    public List<WeatherEntity> getAllWeather() {
        return (List<WeatherEntity>) weatherRepo.findAll();
    }


    /**
     * Retrieve average weather data within a specified time range.
     *
     * @param startDateTime Start date and time of the time range.
     * @param endDateTime   End date and time of the time range.
     * @return WeatherEntity with average indicators in the specified time range.
     */
    public WeatherEntity getWeatherInTimeRange(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<WeatherEntity> weatherList = weatherRepo.findByDateTimeBetween(startDateTime, endDateTime);
        return calculateAverageIndicators(weatherList);
    }


    /**
     * Retrieve the latest weather data.
     *
     * @return ResponseEntity with the retrieved latest weather data or a bad request response if not found.
     */
    public WeatherEntity getLatestWeather() {
        return weatherRepo.findFirstByOrderByIdDesc()
                .orElseThrow(() -> new NoSuchElementException("No weather data available"));
    }


    /**
     * Retrieve average weather data within a specified date range.
     *
     * @param startDate Start date of the date range.
     * @param endDate   End date of the date range.
     * @return WeatherEntity with average indicators in the specified date range.
     */
    public WeatherEntity getWeatherInDateRange(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        List<WeatherEntity> weatherList = weatherRepo.findByDateTimeBetween(startDateTime, endDateTime);
        return calculateAverageIndicators(weatherList);
    }


    /**
     * Post new weather data to the database.
     *
     * @param weather WeatherEntity to be added to the database.
     * @throws WeatherAlreadyExists if the weather data already exists in the database.
     */
    public void postWeather(WeatherEntity weather) throws WeatherAlreadyExists {
        WeatherEntity existingWeather = weatherRepo.findByLocationAndDateTime(weather.getLocation(), weather.getDateTime());
        if (existingWeather != null) {
            log.warn("Attempted to add duplicate weather entry for location {} and date/time {}. Entry already exists.",
                    weather.getLocation(), weather.getDateTime());
            throw new WeatherAlreadyExists("Weather entry already exists");
        }

        weatherRepo.save(weather);
        log.info("Adding new weather entry for location {} and date/time {}.",
                weather.getLocation(), weather.getDateTime());
    }
}

