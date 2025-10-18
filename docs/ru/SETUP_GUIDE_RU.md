# Руководство по установке

Это руководство описывает, как настроить и запустить проект Phoebe CMS в первый раз. Существует два основных
сценария: миграция существующих данных из Drupal 6 или начало работы с чистой базой данных.

> Для определения ключевых терминов и технологий, пожалуйста, обратитесь к **[Глоссарию](./GLOSSARY_RU.md)**.

---

## Сценарий 1: Миграция с Drupal 6 (Только MySQL)

Этот сценарий предназначен для переноса существующей базы данных Drupal 6. Весь процесс миграции построен
вокруг MySQL, поэтому выбор другой СУБД здесь невозможен.

### Шаг 1: Выполните миграцию данных

Следуйте инструкциям в [Руководстве по миграции с Drupal 6](MIGRATION_DRUPAL6_RU.md), чтобы получить
финальный SQL-файл `clean_schema.sql`.

### Шаг 2: Настройте окружение для MySQL

1.  **Docker Compose**: Убедитесь, что в файле `docker-compose.yml` активен сервис `mysql`.

2.  **Переменные окружения**: Создайте файл `.env.dev` в корне проекта и укажите в нем учетные данные для MySQL.
    ```dotenv
    SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/dniester?useUnicode=true&characterEncoding=utf8mb4&useSSL=false
    SPRING_DATASOURCE_USERNAME=root
    SPRING_DATASOURCE_PASSWORD=root
    # ...другие переменные
    ```

### Шаг 3: Запустите проект

1.  **Запустите Docker-контейнер** с MySQL:
    ```bash
    docker compose up -d mysql
    ```

2.  **Импортируйте схему**:
    ```bash
    docker exec -i news-mysql mysql -uroot -proot dniester < db_data/clean_schema.sql
    ```

3.  **Запустите приложение** с профилями `local` и `mysql`:
    ```bash
    cd backend
    ./gradlew bootRun --args='--spring.profiles.active=local,mysql'
    ```

Приложение запустится, используя существующую, мигрированную базу данных.

---

## Сценарий 2: Чистая установка (MySQL или PostgreSQL)

Этот сценарий предназначен для начала работы с нуля. Вы можете выбрать любую из поддерживаемых СУБД.

### Шаг 1: Выберите вашу СУБД

Решите, будете ли вы использовать MySQL или PostgreSQL для вашего проекта.

### Шаг 2: Настройте Docker Compose

Откройте файл `docker-compose.yml` и оставьте активным только один сервис базы данных:

- **Для MySQL** (оставьте этот блок):
  ```yaml
  mysql:
    image: mysql:8.0
    # ... остальная конфигурация
  ```

- **Для PostgreSQL** (если вы добавили его, оставьте этот блок):
  ```yaml
  postgres:
    image: postgres:13
    # ... остальная конфигурация
  ```

### Шаг 3: Настройте переменные окружения (`.env.dev`)

Создайте файл `.env.dev` и укажите URL для выбранной вами СУБД.

- **Для MySQL**:
  ```dotenv
  SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/dniester?useUnicode=true&characterEncoding=utf8mb4&useSSL=false
  SPRING_DATASOURCE_USERNAME=root
  SPRING_DATASOURCE_PASSWORD=root
  ```

- **Для PostgreSQL**:
  ```dotenv
  SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/dniester
  SPRING_DATASOURCE_USERNAME=user
  SPRING_DATASOURCE_PASSWORD=password
  ```

### Шаг 4: Запустите приложение с правильным профилем

1.  **Запустите Docker-контейнер** с выбранной базой данных:
    ```bash
    # Для MySQL
    docker compose up -d mysql

    # Для PostgreSQL
    docker compose up -d postgres
    ```

2.  **Запустите приложение**, указав соответствующий профиль СУБД:
    ```bash
    cd backend

    # Для MySQL
    ./gradlew bootRun --args='--spring.profiles.active=local,mysql'

    # Для PostgreSQL
    ./gradlew bootRun --args='--spring.profiles.active=local,postgresql'
    ```

При первом запуске Flyway автоматически создаст всю необходимую структуру таблиц в вашей базе данных.
