CREATE TABLE IF NOT EXISTS WeatherEntity (
    id INT AUTO_INCREMENT PRIMARY KEY,
    temperature FLOAT,
    wind FLOAT,
    pressure FLOAT,
    humidity FLOAT,
    location VARCHAR(255),
    dateTime DATETIME
);