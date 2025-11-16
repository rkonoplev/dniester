> [Вернуться к содержанию документации](./README.md)

# Руководство по разработке с локальной базой данных (без Docker)

Это руководство описывает, как настроить и вести разработку проекта Phoebe CMS, если вы предпочитаете использовать локально установленные базы данных MySQL или PostgreSQL вместо Docker-контейнеров.

---

## Содержание
- [Предварительные требования](#предварительные-требования)
- [Шаг 1: Клонирование проекта](#шаг-1-клонирование-проекта)
- [Шаг 2: Настройка локальной базы данных](#шаг-2-настройка-локальной-базы-данных)
- [Шаг 3: Настройка проекта Spring Boot](#шаг-3-настройка-проекта-spring-boot)
- [Шаг 4: Запуск приложения](#шаг-4-запуск-приложения)
- [Рабочий процесс](#рабочий-процесс)
- [Устранение проблем](#устранение-проблем)

---

## Предварительные требования

Перед началом убедитесь, что на вашем компьютере установлено следующее:

1.  **Java Development Kit (JDK) 21** или выше.
2.  **Gradle 8.x** (обычно поставляется с проектом через `gradlew`, но убедитесь, что он работает).
3.  **Локально установленная база данных**:
    *   **MySQL 8.0** или выше, ИЛИ
    *   **PostgreSQL 12** или выше.
4.  **Инструмент управления базой данных** (рекомендуется):
    *   Для MySQL: **phpMyAdmin**, **MySQL Workbench** или аналогичный.
    *   Для PostgreSQL: **pgAdmin** или аналогичный.
5.  **Git**.

---

## Шаг 1: Клонирование проекта

Если вы еще не сделали этого, клонируйте репозиторий проекта Phoebe:

```bash
git clone <URL_вашего_репозитория>
cd phoebe
```

Убедитесь, что скрипт `gradlew` исполняемый:
```bash
chmod +x gradlew
```

---

## Шаг 2: Настройка локальной базы данных

Вам необходимо создать базу данных и пользователя для проекта Phoebe.

### Для MySQL

1.  **Подключитесь к вашему локальному серверу MySQL** (например, через MySQL Workbench, phpMyAdmin или командную строку `mysql -u root -p`).
2.  **Создайте базу данных `phoebe_db`**:
    ```sql
    CREATE DATABASE IF NOT EXISTS phoebe_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
    ```
3.  **Создайте пользователя `phoebe_user` и предоставьте ему права** (замените `your_password` на надежный пароль):
    ```sql
    CREATE USER 'phoebe_user'@'localhost' IDENTIFIED BY 'your_password';
    GRANT ALL PRIVILEGES ON phoebe_db.* TO 'phoebe_user'@'localhost';
    FLUSH PRIVILEGES;
    ```
    *   **Примечание**: Если вы подключаетесь не с `localhost`, замените `localhost` на соответствующий IP-адрес или `%`.

### Для PostgreSQL

1.  **Подключитесь к вашему локальному серверу PostgreSQL** (например, через pgAdmin или командную строку `psql -U postgres`).
2.  **Создайте базу данных `phoebe_db`**:
    ```sql
    CREATE DATABASE phoebe_db;
    ```
3.  **Создайте пользователя `phoebe_user` и предоставьте ему права** (замените `your_password` на надежный пароль):
    ```sql
    CREATE USER phoebe_user WITH PASSWORD 'your_password';
    GRANT ALL PRIVILEGES ON DATABASE phoebe_db TO phoebe_user;
    ```

---

## Шаг 3: Настройка проекта Spring Boot

Вам нужно будет создать или изменить файл конфигурации, чтобы Spring Boot подключался к вашей локальной базе данных.

1.  **Создайте файл `application-local-db.yml`** в директории `backend/src/main/resources/`.
2.  **Добавьте следующую конфигурацию** в зависимости от вашей базы данных:

### Для MySQL (`application-local-db.yml`)

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/phoebe_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
    username: phoebe_user
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: validate # Или update для автоматического обновления схемы (использовать осторожно)
  flyway:
    enabled: true
    locations: classpath:db/migration/common,classpath:db/migration/mysql
```
*   **Важно**: Замените `your_password` на пароль, который вы установили для `phoebe_user`.

### Для PostgreSQL (`application-local-db.yml`)

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/phoebe_db
    username: phoebe_user
    password: your_password
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: validate # Или update для автоматического обновления схемы (использовать осторожно)
  flyway:
    enabled: true
    locations: classpath:db/migration/common,classpath:db/migration/postgresql
```
*   **Важно**: Замените `your_password` на пароль, который вы установили для `phoebe_user`.
*   **Примечание**: Если ваш PostgreSQL работает на другом порту, измените `5432` на соответствующий.

---

## Шаг 4: Запуск приложения

Теперь вы готовы запустить приложение Spring Boot, используя ваш новый профиль `local-db`.

1.  **Перейдите в директорию `backend`**:
    ```bash
    cd backend
    ```

2.  **Запустите приложение**:
    ```bash
    ./gradlew bootRun --args='--spring.profiles.active=local-db'
    ```
    *   При первом запуске Flyway автоматически применит все необходимые миграции к вашей базе данных, создав схему и заполнив начальными данными.

3.  **Проверьте работу приложения**:
    Откройте в браузере `http://localhost:8080/actuator/health` или используйте `curl`:
    ```bash
    curl http://localhost:8080/actuator/health
    ```
    Вы должны увидеть статус `UP`.

---

## Рабочий процесс

При разработке с локальной базой данных ваш рабочий процесс будет выглядеть следующим образом:

1.  **Запустите ваш локальный сервер MySQL/PostgreSQL**.
2.  **Запустите приложение Spring Boot** с профилем `local-db`:
    ```bash
    cd backend
    ./gradlew bootRun --args='--spring.profiles.active=local-db'
    ```
3.  **Пишите код и тестируйте**. Приложение будет взаимодействовать напрямую с вашей локальной базой данных.
4.  **Для юнит-тестов**:
    ```bash
    cd backend
    ./gradlew test
    ```
    *   Юнит-тесты не требуют запущенной базы данных.
5.  **Для интеграционных тестов**:
    *   Интеграционные тесты по умолчанию настроены на использование Testcontainers. Если вы хотите, чтобы они использовали вашу локальную базу данных, вам потребуется изменить их конфигурацию (что не рекомендуется, так как Testcontainers обеспечивают изолированную и воспроизводимую среду).
    *   Если вы все же хотите запускать интеграционные тесты без Testcontainers, вам нужно будет создать отдельный профиль для тестов, который будет указывать на вашу локальную базу данных, и убедиться, что ваша локальная база данных чиста перед каждым запуском тестов.

---

## Устранение проблем

*   **"Access denied for user 'phoebe_user'@'localhost'"**: Убедитесь, что имя пользователя и пароль в `application-local-db.yml` совпадают с теми, что вы установили в базе данных. Проверьте, что пользователь имеет права доступа с `localhost` (или соответствующего IP).
*   **"Unknown database 'phoebe_db'"**: Убедитесь, что вы создали базу данных `phoebe_db` на вашем локальном сервере.
*   **"Connection refused"**: Убедитесь, что ваш локальный сервер MySQL/PostgreSQL запущен и доступен по указанному порту (`3306` для MySQL, `5432` для PostgreSQL). Проверьте настройки брандмауэра.
*   **"Flyway migration failed"**: Это может произойти, если схема базы данных не соответствует ожидаемой Flyway. Убедитесь, что база данных `phoebe_db` пуста перед первым запуском Flyway, или что вы не пытаетесь применить миграции к уже измененной схеме. В случае проблем можно попробовать очистить схему Flyway:
    ```bash
    cd backend
    ./gradlew flywayClean --args='--spring.profiles.active=local-db'
    ```
    **ВНИМАНИЕ**: `flywayClean` удалит все таблицы из базы данных, указанной в профиле `local-db`! Используйте с осторожностью.

---

Это руководство должно помочь вам настроить и вести разработку проекта Phoebe CMS с использованием локально установленной базы данных.
