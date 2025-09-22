# Быстрый старт (Русский)

Краткая инструкция для разработчиков по запуску News Platform локально.

## Требования
- JDK 21+
- Docker & Docker Compose
- Git

## Быстрый запуск

### 1. Клонирование и настройка
```bash
git clone https://github.com/rkonoplev/news-platform.git
cd news-platform
cp .env.dev.example .env.dev
```

### 2. Запуск через Docker
```bash
docker compose --env-file .env.dev up -d
```

### 3. Проверка работы
- **Контейнеры**: `docker ps` → должны быть `news-app` и `news-mysql`
- **API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **Админка**: логин `admin`, пароль `admin`

## Разработка

### Запуск тестов
```bash
cd backend
./gradlew clean test
```

### Сборка проекта
```bash
./gradlew build
```

### Проверка качества кода
```bash
./gradlew checkstyleMain checkstyleTest
```

## Решение проблем

### Тесты не запускаются (конфликт с MySQL)
- Проверь, что в `application.yml` **нет** `spring.profiles.active: local`
- В `application-test.yml` должен быть отключён Flyway
- Запускай тесты с чистой сборкой: `./gradlew clean test`

### Сброс базы данных
```bash
docker compose down -v  # удалит все данные
docker compose up -d    # создаст чистую базу
```

### Проблемы с портами
- MySQL: порт 3306 (может конфликтовать с локальным MySQL)
- Spring Boot: порт 8080
- Останови локальные сервисы или измени порты в `.env.dev`

## Дополнительная документация

- **[Полное описание проекта](TASK_DESCRIPTION.md)** - архитектура, технологии, API
- **[Руководство разработчика](DEVELOPER_GUIDE.md)** - детальная настройка IDE
- **[Docker Guide](DOCKER_GUIDE.md)** - продвинутая работа с контейнерами
- **[Миграция с Drupal 6 (RU)](MIGRATION_DRUPAL6_RU.txt)** - процесс миграции

## CI/CD

- **GitHub Actions**: автоматически запускается профиль `ci` с H2
- **Тесты**: выполняются на каждый push и PR
- **Качество кода**: Checkstyle, JaCoCo coverage

## Продакшн

- Используется `docker-compose.override.yml` с секретами
- Профиль `prod` с настоящей MySQL
- Переменные окружения через Docker secrets