# Чек-лист разработчика – локальная работа и CI/CD

Этот файл описывает, как удобно работать над проектом на локальной машине (IntelliJ IDEA, Gradle, Docker) и что будет автоматически выполняться в GitHub Actions (CI/CD).

---

## Ежедневная работа локально

- Docker и базы данных НЕ нужно держать включёнными постоянно.
- Достаточно писать код и запускать юнит-тесты.
- Основные действия в IDEA:
  - **Build Project** (Ctrl+F9) для проверки сборки.
  - **Запуск юнит-тестов:**
    - через IDEA на классе/методе,
    - или командой: `./gradlew test`
  - **По желанию:** прогнать линтеры локально:
    ```bash
    ./gradlew checkstyleMain checkstyleTest
    ```

**Если нужно протестировать полное приложение:**
- Запусти Docker (например, контейнеры с БД).
- Подними сервис:
  ```bash
  ./gradlew bootRun
  ```
- После проверки можно остановить Docker, чтобы не грузить ноутбук.

---

## Перед пушем на GitHub

**Минимум проверки перед пушем:**
- Код компилируется (Build Project или `./gradlew build`)
- Все тесты проходят (`./gradlew test`)
- Проверка стиля — опционально, но желательно (`./gradlew checkstyleMain checkstyleTest`)

---

## Что делает CI/CD (на GitHub Actions)

После пуша будут автоматически выполнены:
- Полная Gradle сборка + юнит-тесты.
- Статический анализ кода: Checkstyle и PMD.
- Отчёт о покрытии тестов (JaCoCo) + загрузка в Codecov.
- GitLeaks поиск секретов.
- Интеграция с GitHub Security (Code scanning alerts).

---

## Итог

- Docker при обычной разработке держать не нужно.
- Локально запускай только build/test.
- Весь «тяжёлый» анализ (Checkstyle, PMD, Coverage, GitLeaks) будет в CI.

> **Примечание:** Планируется миграция аутентификации на OAuth 2.0 + 2FA для всех ролей (ADMIN, EDITOR, USER).

---
## Два сценария запуска проекта

В зависимости от ваших задач, вы можете начать работу одним из двух способов.

### Сценарий 1: С мигрированными данными (Drupal 6 → News Platform)

Если у вас есть старый сайт на Drupal 6, выполните полную миграцию согласно инструкции:
- **[Миграция с Drupal 6 (RU)](MIGRATION_DRUPAL6_RU.md)**

После завершения вы получите файл `db_data/clean_schema.sql`, содержащий все статьи и пользователей.
Запустите проект и импортируйте данные:

```bash
docker compose --env-file .env.dev up -d
docker exec -i news-mysql mysql -uroot -proot dniester < db_data/clean_schema.sql
```
Вход в админку осуществляется учётными данными из старой Drupal-базы.

### Сценарий 2: С чистой базой (для разработки или нового проекта)
Если вы начинаете с нуля, Spring Boot автоматически создаст пустые таблицы при запуске.

Убедитесь, что у вас нет старых данных:
```bash
docker compose down -v
```
Запустите проект:
```bash
docker compose --env-file .env.dev up -d
```
Создайте тестового администратора:
```bash
docker exec -i news-mysql mysql -uroot -proot dniester < db_data/init_admin.sql
```
Важно: Перед первым запуском откройте файл db_data/init_admin.sql и замените заглушку пароля на настоящий BCrypt-хэш от пароля admin.

Теперь вы можете войти в админку с логином admin и паролем admin.

## ШПАРГАЛКА ПО РАБОТЕ С MYSQL В КОНТЕЙНЕРЕ

### Подключение в интерактивный режим:
```bash
docker exec -it news-mysql mysql -uroot -proot
```

### Внутри MySQL (появляется mysql>):

**Посмотреть все базы:**
```sql
SHOW DATABASES;
```

**Переключиться в базу:**
```sql
USE dniester;
```

**Посмотреть таблицы:**
```sql
SHOW TABLES;
```

**Пример: посчитать записи:**
```sql
SELECT COUNT(*) FROM content;
```

**Выйти:**
```sql
EXIT;
```

### Экспорт (дамп БД):

**Дамп всей базы dniester:**
```bash
docker exec -i news-mysql mysqldump -uroot -proot dniester > db_data/exported_dump.sql
```

**Дамп конкретной таблицы (например users):**
```bash
docker exec -i news-mysql mysqldump -uroot -proot dniester users > db_data/users_dump.sql
```

### Импорт (залить дамп обратно):
```bash
docker exec -i news-mysql mysql -uroot -proot dniester < db_data/exported_dump.sql
```

### Примечания:

- В команде указывай имя базы (например dniester).
- Перед импортом база должна существовать.
- Дамп — это обычный .sql файл, его можно хранить в папке db_data для удобства.