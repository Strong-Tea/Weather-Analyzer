package com.analyzer.weather.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

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
}
