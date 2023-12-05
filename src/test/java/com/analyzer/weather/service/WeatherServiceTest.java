package com.analyzer.weather.service;

import com.analyzer.weather.entity.WeatherEntity;
import com.analyzer.weather.exception.WeatherAlreadyExists;
import com.analyzer.weather.repository.WeatherRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.text.DateFormatter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.ToDoubleFunction;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WeatherServiceTest {
    @InjectMocks
    private WeatherService weatherService;
    @Mock
    private WeatherRepo weatherRepo;

    @Test
    void postWeatherTest_Success() throws WeatherAlreadyExists {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse("2023-12-04T12:30:00", formatter);
        WeatherEntity weather = new WeatherEntity(
                1L, 25.0f, 10.0f, 1010.0f, 60.0f, "Minsk", localDateTime);

        Mockito.when(weatherRepo.findByLocationAndDateTime(weather.getLocation(), weather.getDateTime())).thenReturn(null);
        weatherService.postWeather(weather);
        Mockito.verify(weatherRepo, Mockito.times(1)).save(weather);
    }

    @Test
    void postWeatherTest_WeatherAlreadyExists() throws WeatherAlreadyExists {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse("2023-12-04T12:30:00", formatter);
        WeatherEntity weather = new WeatherEntity(
                1L, 25.0f, 10.0f, 1010.0f, 60.0f, "Minsk", localDateTime);

        Mockito.when(weatherRepo.findByLocationAndDateTime(weather.getLocation(), weather.getDateTime())).thenReturn(weather);
        Assertions.assertThrows(WeatherAlreadyExists.class, () -> weatherService.postWeather(weather));
    }


    @Test
    void getWeather_Success() {
        WeatherEntity sampleWeather = new WeatherEntity();
        sampleWeather.setId(1L);
        Mockito.when(weatherRepo.findById(1L)).thenReturn(Optional.of(sampleWeather));

        WeatherEntity responseEntity = weatherService.getWeather(1L);
        Mockito.verify(weatherRepo, Mockito.times(1)).findById(1L);
        Assertions.assertEquals(sampleWeather, responseEntity);
    }

    @Test
    void getWeather_NoSuchElementException() {
        Mockito.when(weatherRepo.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(NoSuchElementException.class, () -> weatherService.getWeather(1L));
    }


    @Test
    void getAllWeatherTest() {
        List<WeatherEntity> sampleWeatherList = Arrays.asList(
                new WeatherEntity(1L, 25.0f, 10.0f, 1010.0f, 60.0f, "Minsk", LocalDateTime.now()),
                new WeatherEntity(2L, 22.5f, 12.0f, 1015.0f, 65.0f, "Minsk", LocalDateTime.now())
        );

        Mockito.when(weatherRepo.findAll()).thenReturn(sampleWeatherList);

        List<WeatherEntity> responseEntity = weatherService.getAllWeather();
        Mockito.verify(weatherRepo, Mockito.times(1)).findAll();

        Assertions.assertEquals(sampleWeatherList, responseEntity);
    }

    @Test
    void getLatestWeather_Success() {
        WeatherEntity weather = new WeatherEntity();
        when(weatherRepo.findFirstByOrderByIdDesc()).thenReturn(Optional.of(weather));
        WeatherEntity result = weatherService.getLatestWeather();
        Assertions.assertEquals(weather, result);
    }

    @Test
    void getLatestWeather_NoSuchElementException() {
        when(weatherRepo.findFirstByOrderByIdDesc()).thenReturn(Optional.empty());
        Assertions.assertThrows(NoSuchElementException.class, () -> weatherService.getLatestWeather());
    }

    @Test
    void getWeatherInTimeRange() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime localDateTime1 = LocalDateTime.parse("2023-12-04T12:30:00", formatter);
        LocalDateTime localDateTime2 = LocalDateTime.parse("2023-12-04T12:45:00", formatter);

        WeatherEntity weather1 = new WeatherEntity(1L, 25.0f, 10.0f, 1010.0f, 60.0f, "Minsk", localDateTime1);
        WeatherEntity weather2 = new WeatherEntity(2L, 22.0f, 8.0f, 1005.0f, 65.0f, "Minsk", localDateTime2);
        List<WeatherEntity> weatherList = Arrays.asList(weather1, weather2);

        when(weatherRepo.findByDateTimeBetween(localDateTime1, localDateTime2)).thenReturn(weatherList);

        WeatherEntity result = weatherService.getWeatherInTimeRange(localDateTime1, localDateTime2);
        WeatherEntity check = calculateAverageIndicators(weatherList);
        Assertions.assertEquals(check, result);
    }

    @Test
    void getWeatherInDateRange() throws ParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate1 = LocalDate.parse("2023-12-04", formatter);
        LocalDate localDate2 = LocalDate.parse("2023-12-04", formatter);

        LocalDateTime localDateTime1 = localDate1.atStartOfDay();
        LocalDateTime localDateTime2 = localDate1.atTime(LocalTime.MAX);

        WeatherEntity weather1 = new WeatherEntity(1L, 25.0f, 10.0f, 1010.0f, 60.0f, "Minsk", localDateTime1);
        WeatherEntity weather2 = new WeatherEntity(2L, 22.0f, 8.0f, 1005.0f, 65.0f, "Minsk", localDateTime2);
        List<WeatherEntity> weatherList = Arrays.asList(weather1, weather2);

        when(weatherRepo.findByDateTimeBetween(localDateTime1, localDateTime2)).thenReturn(weatherList);

        WeatherEntity result = weatherService.getWeatherInDateRange(localDate1, localDate2);
        WeatherEntity check = calculateAverageIndicators(weatherList);
        Assertions.assertEquals(check, result);
    }

    private float calculateAverage(List<WeatherEntity> weatherList, ToDoubleFunction<WeatherEntity> mapper) {
        return (float) weatherList.stream()
                .mapToDouble(mapper)
                .average()
                .orElse(0);
    }

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
}
