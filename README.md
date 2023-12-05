# Задание
Необходимо разработать анализатор погоды. Нужно использовать сторонние API 
~~~
например https://rapidapi.com/weatherapi/api/weatherapi-com
~~~
 Приложение будет запрашивать погоду с заданной в настройках периодичностью по определенному городу.

Как это работает:

Приложение получает информацию о погоде в Минске от стороннего  API, по расписанию и  сохраняет ее в БД.
(Город по которому запрашивается погода всегда один и тот же)

Первый endpoint должен в качестве ответа отдавать информацию о текущей погоде - наиболее актуальная информация, которая хранится в БД сервиса. В ответе должна содержаться следующая информация:

1) Температура
2) Скорость ветра в м/ч
3) Атмосферное давление в гектопаскалях или миллибарах
4) Влажность воздуха
5) Погодные условия (солнечно, облачно и т.д.)
6) Локация

Второй endpoint должен выдавать рассчитанную на основании имеющихся в сервисе данных информацию о среднесуточной температуре. Пользователь должен будет иметь возможность получить информацию за указанный период. 
~~~
Request 
{
“from”: “22-08-2021”,
“to”: “24-08-2021”
}
Response
{
“average_temp”: 10
// и другие 
}
~~~
 
Взаимодействие пользователя с приложением происходит через REST API.

Требования:

 - приложение должно быть реализовано на языке Java версии 8+ ;
 - код должен соответствовать принципам Low Coupling, принципам ООП и Clean Code;
 - код должен соответствовать Java code style (именование переменных, структура класса и др.);
 - приложение должно содержать качественную обработку ошибок и логирование;
 - использовать Spring Framework(Spring Boot).
 - покрытие unit тестами.
 - Описать взаимодействие с приложением в README. 
 - Вместе с приложением должны содержаться скрипты для создания схемы БД

# Описание к программе
В программе использовалась база данных MySQL.

~~~
sudo apt update
sudo apt install mysql-server
udo systemctl start mysql.service
~~~

После установки нужно настроить конфигурацию MySQL (логин, пароль) [Настройка](https://www.digitalocean.com/community/tutorials/how-to-install-mysql-on-ubuntu-20-04#step-2-configuring-mysql)

Далее согласно с MySQL Spring Boot документацией 
https://spring.io/guides/gs/accessing-data-mysql/ создаем базу данных. После создания указываем в файле 'application.properties' какую базу данных будем использовать в приложении и логин и пароль для доступа к ней. В данном примере используется база данных weather с логин и пароль root.
~~~
spring.jpa.hibernate.ddl-auto=update
spring.datasource.url=jdbc:mysql://${MYSQL_HOST:localhost}:3306/weather
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.task.scheduling.pool.size=60
spring.jpa.show-sql = true

weather.api.url=https://weatherapi-com.p.rapidapi.com/current.json?q=minsk
weather.api.key=698810639amsh31ff76a7c594d89p1c6d84jsnd11b08b05adf
weather.api.host=weatherapi-com.p.rapidapi.com

spring.sql.init.mode=always
spring.sql.init.schema-locations=classpath:/database/create-tables.sql
~~~

# Пример работы с программой
При запуске программы, программа каждые 60 секунд будет обращаться к сервису погоды для получения обновленных данных. Если такие данные есть, то они добавляются в базу данных.

![database](https://github.com/Strong-Tea/Weather-Analyzer/assets/135996451/790bef41-4d1a-43be-acb2-de876ef6eaa0)

Для взаимодействия с приложением использовалась программа 'Postman'. [Скачать](https://www.postman.com/downloads/)

### Получение погоды по индексу 2 (если такой имеется)
![1](https://github.com/Strong-Tea/Weather-Analyzer/assets/135996451/acafaa31-7916-418a-9a71-1238841cf3d9)
### Получить весь список погоды из базы данных
![2](https://github.com/Strong-Tea/Weather-Analyzer/assets/135996451/65dfcab5-2a8f-4237-8fe7-4233d3458276)
### Получить средние значения на основе диапазона введенных 'дата и время'
![3](https://github.com/Strong-Tea/Weather-Analyzer/assets/135996451/ad48f11f-44f8-4029-9c25-6cae1c4c5181)
### Получить средние значения на основе диапазона только даты
![4](https://github.com/Strong-Tea/Weather-Analyzer/assets/135996451/c3295b24-7d8d-4334-a809-6beda7c105c9)
