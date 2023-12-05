package com.analyzer.weather.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class WeatherEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private float temperature;
    private float wind;
    private float pressure;
    private float humidity;
    private String location;
    private LocalDateTime dateTime;

    public WeatherEntity() {
    }

    public WeatherEntity(Long id, float temperature, float wind, float pressure, float humidity, String location, LocalDateTime dateTime) {
        this.id = id;
        this.temperature = temperature;
        this.wind = wind;
        this.pressure = pressure;
        this.humidity = humidity;
        this.location = location;
        this.dateTime = dateTime;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }
    public void setWind(float wind) {
        this.wind = wind;
    }
    public void setPressure(float pressure) {
        this.pressure = pressure;
    }
    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
    public Long getId() {
        return id;
    }
    public float getTemperature() {
        return temperature;
    }
    public float getWind() {
        return wind;
    }
    public float getPressure() {
        return pressure;
    }
    public float getHumidity() {
        return humidity;
    }
    public String getLocation() {
        return location;
    }
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        WeatherEntity other = (WeatherEntity) obj;

        return Float.compare(other.temperature, temperature) == 0 &&
                Float.compare(other.wind, wind) == 0 &&
                Float.compare(other.pressure, pressure) == 0 &&
                Float.compare(other.humidity, humidity) == 0 &&
                Objects.equals(id, other.id) &&
                Objects.equals(location, other.location) &&
                Objects.equals(dateTime, other.dateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, temperature, wind, pressure, humidity, location, dateTime);
    }
}
