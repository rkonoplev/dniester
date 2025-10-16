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
### Настройка автоформаттера кода

Проект использует единые настройки форматирования Java-кода с длиной строки **120 символов**. Конфигурация находится в `.idea/codeStyles/` и автоматически применяется при открытии проекта.

#### Автоматическая настройка
- Настройки форматирования подхватываются IntelliJ IDEA автоматически
- Проверьте: `File → Settings → Editor → Code Style → Scheme = "Project"`

#### Использование
- **Форматирование кода**: `Ctrl+Alt+L` (Win/Linux) или `Cmd+Alt+L` (Mac)
- **Форматирование при сохранении**: Включите в `Settings → Tools → Actions on Save → Reformat code`

#### Основные правила
- **Длина строки**: 120 символов
- **Автоперенос** длинных цепочек методов и параметров
- **Единый стиль** скобок и отступов
- **Автоформатирование** импортов и пустых строк

Все разработчики в команде используют одинаковые настройки форматирования.

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

> **Примечание:** Планируется миграция аутентификации на OAuth 2.0 + 2FA для всех ролей (ADMIN, EDITOR).

---

## Миграции базы данных (Flyway)

Проект использует Flyway для управления изменениями схемы БД. Для поддержки нескольких СУБД (MySQL и PostgreSQL)
скрипты миграций организованы в отдельных директориях для каждого вендора.

### Структура директорий
- `src/main/resources/db/migration/common/`: Содержит общие скрипты, совместимые со всеми поддерживаемыми СУБД.
  Эти миграции выполняются всегда.
- `src/main/resources/db/migration/mysql/`: Содержит скрипты со специфичным синтаксисом для MySQL.
  Выполняются только при активном профиле Spring `mysql`.
- `src/main/resources/db/migration/postgresql/`: Содержит скрипты для PostgreSQL. Выполняются только при
  активном профиле `postgresql`.

### Как это работает

Пути к скриптам Flyway настраиваются в зависимости от активного профиля Spring. Это определяется
в соответствующем файле `application-{profile}.yml`.

Например, в `application-mysql.yml`:
```yaml
spring:
  flyway:
    locations: classpath:db/migration/common,classpath:db/migration/mysql
```

А в `application-postgresql.yml`:
```yaml
spring:
  flyway:
    locations: classpath:db/migration/common,classpath:db/migration/postgresql
```

Такая конфигурация позволяет Flyway комбинировать общие и специфичные для СУБД миграции, гарантируя
корректное применение схемы для целевой среды.

---

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