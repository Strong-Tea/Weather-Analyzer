package com.analyzer.weather.json;

import com.analyzer.weather.entity.WeatherEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 * Represents the JSON structure received from a weather API response.
 * The class is annotated with @JsonIgnoreProperties(ignoreUnknown = true) to gracefully handle
 * unknown properties in the JSON, ignoring them during deserialization.
 *
 * The structure includes information about the weather location and current conditions.
 *
 * It also provides a method, convertToWeatherEntity(), to convert the JSON data into a
 * WeatherEntity, facilitating seamless integration with the application's domain model.
 *
 * Usage:
 * - Use this class to map JSON responses from a weather API into Java objects.
 * - Leverage the convertToWeatherEntity() method to transform the JSON data into a WeatherEntity
 *   for further processing and storage in the application.
 *
 * Example:
 * ```
 * ObjectMapper objectMapper = new ObjectMapper();
 * WeatherJson weatherJson = objectMapper.readValue(jsonString, WeatherJson.class);
 * WeatherEntity weatherEntity = weatherJson.convertToWeatherEntity();
 * ```
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherJson {
    public Location location;
    public Current current;

    public static class Location {
        public String name;
        public String region;
        public String country;
        public String localtime;
    }

    public static class Current {
        public String last_updated;
        public float temp_c;
        public float temp_f;
        public boolean is_day;
        public float wind_mph;
        public float wind_kph;
        public String wind_dir;
        public float pressure_mb;
        public float humidity;
    }


    /**
     * Converts the WeatherJson object to a WeatherEntity, facilitating integration with the application's domain model.
     * The method maps relevant properties from the JSON response to the corresponding fields of a WeatherEntity object.
     *
     * @return WeatherEntity: An entity representing weather information with properties such as temperature, humidity, wind speed,
     *         location, pressure, and date/time. The entity is populated with data from the WeatherJson object.
     */
    public WeatherEntity convertToWeatherEntity() {
        WeatherEntity weather = new WeatherEntity();
        weather.setTemperature(this.current.temp_c);
        weather.setHumidity(this.current.humidity);
        weather.setWind(this.current.wind_kph);
        weather.setLocation(this.location.name);
        weather.setPressure(this.current.pressure_mb);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        weather.setDateTime(LocalDateTime.parse(this.current.last_updated, formatter));

        return weather;
    }
}