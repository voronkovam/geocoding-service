# Geocoding Service

Java Spring Boot-приложение для прямого и обратного геокодирования с использованием внешнего API Nominatim (OpenStreetMap). 
Приложение реализует кэширование результатов в PostgreSQL и предоставляет REST API.

## Функциональность
- Прямое геокодирование: адрес → координаты
- Обратное геокодирование: координаты → адрес
- Кэширование результатов в базе данных
- Обработка ошибок и валидация
- REST API с JSON-ответами
- Метрики и состояние через Spring Actuator
- Интеграционные и unit-тесты

## Стек технологий
- Java 17
- Spring Boot 3
- Spring Data JPA
- PostgreSQL
- OpenStreetMap Nominatim API
- Lombok
- JUnit 5 + Mockito
- Docker (опционально)
- Swagger (опционально)

## Примеры API-запросов

### Прямое геокодирование
POST /api/geocode/direct

Запрос:

{
  "address": "Москва"
}

Ответ:

{
  "address": "Москва",
  "lat": 55.7558,
  "lon": 37.6173
}

### Обратное геокодирование
POST /api/geocode/reverse

Запрос:

{
  "lat": 55.75,
  "lon": 37.62
}

Ответ:

{
  "address": "Москва, Россия",
  "lat": 55.75,
  "lon": 37.62
}

## База данных
Таблица cached_location используется для хранения адресов и координат, полученных из внешнего API.
При повторном запросе данные берутся из БД без повторного обращения к API.

## Тестирование
Приложение содержит:
- Unit-тесты сервиса с Mockito (GeocodingServiceTest)
- Интеграционные тесты контроллера (GeocodingControllerIT) с использованием TestRestTemplate

### Запуск тестов:

./mvnw test

## Сборка и запуск 

### Сборка проекта
mvn clean install

### Запуск приложения
mvn spring-boot:run

### Или в Docker:
docker-compose up --build

## Метрики и здоровье
Доступно через Spring Actuator:
- GET /actuator/health
- GET /actuator/metrics

## Автор
Мария Воронкова