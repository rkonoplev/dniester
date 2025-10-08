# Быстрый старт (Русский)

Краткая инструкция для разработчиков по запуску Phoebe CMS локально.

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

## Два сценария запуска проекта

В зависимости от ваших задач, вы можете начать работу одним из двух способов.
Проект поддерживает два основных сценария запуска:

### Сценарий 1: Миграция из Drupal 6 (существующие данные)
Если у вас есть старый сайт на Drupal 6, вы можете перенести все статьи, пользователей и категории в новую платформу.
1.  Следуйте полной инструкции в **[Миграция с Drupal 6](MIGRATION_DRUPAL6_RU.md)**.
2.  В результате вы получите файл `db_data/clean_schema.sql` со всеми данными. Содержащий все статьи и пользователей.
3.  Запустите проект:
    ```bash
    docker compose --env-file .env.dev up -d
    ```
4.  Импортируйте мигрированные данные (если еще не сделано):
    ```bash
    docker exec -i news-mysql mysql -uroot -proot dniester < db_data/clean_schema.sql
    ```
5.  Готово! Ваши администраторы из Drupal могут войти со своими старыми логинами и паролями.

### Сценарий 2: Начало с чистого листа (новый проект)
Идеально подходит для новых проектов или разработки.
1.  Убедитесь, что у вас чистая база данных и запустите проект:
    ```bash
    docker compose down -v
    docker compose --env-file .env.dev up -d
    ```
2.  Spring Boot автоматически создаст пустые таблицы (`users`, `roles`, `content` и т.д.).
3.  Создайте тестового администратора:
    ```bash
    docker exec -i news-mysql mysql -uroot -proot dniester < db_data/create_admin_user.sql
    ```
    > **Важно**: Пароль по умолчанию: `admin` (BCrypt хэш уже включён в скрипт).
4.  Теперь вы можете войти в админку с логином `admin` и паролем `admin`.

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

- **[Полное описание проекта](TASK_DESCRIPTION_RU.md)** - архитектура, технологии, API
- **[Руководство разработчика](DEVELOPER_GUIDE_RU.md)** - детальная настройка IDE
- **[Docker Guide](../en/DOCKER_GUIDE.md)** - продвинутая работа с контейнерами
- **[Миграция с Drupal 6 (RU)](MIGRATION_DRUPAL6_RU.md)** - процесс миграции

## CI/CD

- **GitHub Actions**: автоматически запускается профиль `ci` с H2
- **Тесты**: выполняются на каждый push и PR
- **Качество кода**: Checkstyle, JaCoCo coverage

## Продакшн

- Используется `docker-compose.override.yml` с секретами
- Профиль `prod` с настоящей MySQL
- Переменные окружения через Docker secrets