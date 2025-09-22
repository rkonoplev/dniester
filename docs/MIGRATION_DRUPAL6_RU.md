# ИНСТРУКЦИЯ ПО МИГРАЦИИ: Drupal 6 → News Platform (Spring Boot + MySQL 8)

## ШАГ 1. Запуск временного MySQL 5.7 для дампа Drupal 6

**Запуск:**
```bash
docker compose -f docker-compose.drupal.yml up -d
```

**Логи:**
```bash
docker logs -f news-mysql-drupal6
```

**Подключение:**
```bash
docker exec -it news-mysql-drupal6 mysql -u root -p
# пароль: root
```

**Проверка:**
```sql
SHOW DATABASES;
USE a264971_dniester;
SHOW TABLES;
```

## ШАГ 2. Экспорт + импорт в новую базу

**Экспорт:**
```bash
docker exec -i news-mysql-drupal6 mysqldump -uroot -proot a264971_dniester > db_data/drupal6_fixed.sql
```

**Импорт:**
```bash
docker exec -i news-mysql-drupal6 mysql -uroot -proot dniester < db_data/drupal6_fixed.sql
```

**Проверка:**
```sql
SHOW TABLES;
```

## LEGACY-ФАЙЛЫ

Файлы, связанные с миграцией и ранней разработкой, сохранены в папке legacy/:
- DatabaseProperties.java - класс конфигурации базы данных для миграции
- Makefile - устаревшие команды для тестирования API
- docker-compose.drupal.yml - временная Docker-конфигурация для миграции
- docker-compose.override.yml - продакшн-настройки Docker
- README.md - подробное описание legacy-файлов

Эти файлы оставлены для образовательных целей и понимания процесса миграции.

---

## ЧТО МЫ ФАКТИЧЕСКИ ИМПОРТИРОВАЛИ В НОВУЮ БАЗУ

После миграции структура базы dniester выглядит так:

### 1. Пользователи (users)
**Источник:** Drupal users  
**Поля:**
- uid → id
- name → username
- mail → email
- status → status (1=активен, 0=блокирован)

### 2. Роли (roles) и связка пользователей с ролями (user_roles)
**Источник:** Drupal role, users_roles  
**Заполняем таблицы:**
- roles (id, name)
- user_roles (user_id, role_id)

### 3. Контент (content)
**Источник:** node, node_revisions  
**Целевая таблица:** content (универсальная, без поля type)  
**Поля:**
- nid → id
- title → title
- body → body
- teaser → teaser
- created (UNIX timestamp) → publication_date (DATETIME)
- uid → author_id (FK → users.id)

> **Важно:** разные типы контента (story, page, book) больше НЕ разделяются и хранятся в одной таблице content.

### 4. Таксономия (terms, content_terms)
**Источник:** term_data, vocabulary, term_node  
**Таблицы:**
- terms (id, name, vocabulary)
- content_terms (content_id, term_id)

### 5. Дополнительные CCK‑поля
**Источник:** content_type_* таблицы (если есть)  
**Целевая таблица:** custom_fields (key-value)  
**Поля:**
- nid → content_id
- имя колонки → field_name
- значение → field_value

### ИТОГО в чистой схеме появляются таблицы:
- users
- roles
- user_roles
- content
- terms
- content_terms
- custom_fields

## ШАГ 3. Нормализация схемы

Выполнить db_data/migrate_from_drupal6_universal.sql  
Выполнить db_data/migrate_cck_fields.sql (если есть CCK)  
Проверка результата

## ШАГ 4. Проблемы с кодировкой (UTF-8, кириллица)

**Ошибка:** Incorrect string value '\xD0...'  
**Причина:** таблица создана в latin1  
**Проверка:**
```sql
SHOW CREATE TABLE a264971_dniester.node \G
```

**Решение:** пересоздать content с UTF8:
```sql
DROP TABLE IF EXISTS content;
CREATE TABLE content (...) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;
```
Заново вставить данные из node + node_revisions

## ШАГ 5. Экспорт clean_schema.sql
```bash
docker exec -i news-mysql-drupal6 mysqldump -uroot -proot dniester > db_data/clean_schema.sql
```

## ШАГ 6. Подготовка MySQL 8.0

**Если запускался неверно:**
```bash
docker compose -f docker-compose.yml down -v
```

**Запуск:**
```bash
docker compose -f docker-compose.yml up -d mysql
```

**Проверка логов:**
```bash
docker logs news-mysql
# должно быть Creating database dniester
```

## ШАГ 7. Сброс пароля root при проблемах

```bash
docker stop news-mysql
docker run -it --rm --name mysql-fix -v news-platform_mysql_data:/var/lib/mysql mysql:8.0 --skip-grant-tables --skip-networking
```

**В другом окне:**
```bash
docker exec -it mysql-fix mysql
```

**В MySQL:**
```sql
FLUSH PRIVILEGES;
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'root';
ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY 'root';
```
Остановить mysql-fix, снова запустить MySQL 8.0

## ШАГ 8. Импорт финального дампа
```bash
docker exec -i news-mysql mysql -uroot -proot dniester < db_data/clean_schema.sql
```

## ШАГ 9. Проверка

**Список таблиц:**
```sql
SHOW TABLES;
```

**Количество:**
```sql
SELECT COUNT(*) FROM content;
# ожидание ~12186 строк
```

---

## КРАТКИЙ ПЛАН:

1. Поднять MySQL 8.0
2. Импортировать clean_schema.sql
3. Проверить наличие таблиц и данных

---

## ПЕРЕНОС LEGACY-ФАЙЛОВ

После успешного завершения миграции несколько файлов, которые использовались в процессе миграции, были перенесены в папку `legacy/` для исторической справки:

- **`DatabaseProperties.java`** - Класс конфигурации базы данных для миграции с расширенными таймаутами
- **`Makefile`** - Устаревшая утилита для тестирования API (заменена на `docs/API_USAGE.md`)
- **`docker-compose.drupal.yml`** - Временная Docker-конфигурация для совместимости с MySQL 5.7
- **`docker-compose.override.yml`** - Продакшн-настройки Docker с улучшениями безопасности
- **`ExampleTest.java`** - Начальный тестовый файл из ранней разработки

Эти файлы сохранены для образовательных целей и понимания эволюции миграции. Подробное описание см. в `legacy/README.md`.

---

## ОПИСАНИЕ SQL-ФАЙЛОВ ДЛЯ МИГРАЦИИ

В процессе миграции было создано несколько SQL-файлов, каждый со своей целью:

Подробную информацию о всех миграционных скриптах и файлах см. в [Database Migration Scripts](../db_data/README.md).

### drupal6_fixed.sql
Чистый дамп исходной базы Drupal 6, перелитый в базу dniester внутри MySQL 5.7.
Использовался для того, чтобы унифицировать работу и иметь новый старт.

### migrate_from_drupal6_universal.sql
Основной миграционный скрипт. Создаёт новые таблицы (users, roles, user_roles, content, terms, content_terms) и переносит очищенные данные из старых таблиц Drupal.
Это главный шаг миграции.

### detect_custom_fields.sql
Вспомогательный файл. Проверяет, есть ли в базе таблицы вида content_type_*.
Если в результате выполнения запросов список пуст → CCK-полей не было.
Если вернулись строки → в базе были кастомные поля, созданные через модуль CCK.

### migrate_cck_fields.sql
Дополнительный скрипт, используется только если реально есть content_type_* таблицы. Перекладывает их данные в универсальную таблицу custom_fields (схема key → value).
Если CCK не использовался, выполнять не нужно.

### clean_schema.sql
Финальный дамп после нормализации схемы (результат выполнения migrate_from_drupal6_universal.sql и при необходимости migrate_cck_fields.sql). Этот файл и загружается в MySQL 8.0 для работы News Platform.

---

## КАК ПРОВЕРИТЬ, ЕСТЬ ЛИ У ТЕБЯ CUSTOMFIELDS?

В MySQL 5.7 (контейнер news-mysql-drupal6) выполни команду:
```sql
SHOW TABLES LIKE 'content_type%';
```

Если список вернулся пустым → у тебя нет CCK полей.
Если есть строки вроде content_type_article, content_type_news и др. → значит были кастомные поля, их можно переложить при необходимости через migrate_cck_fields.sql.

### ДОПОЛНИТЕЛЬНАЯ ПРОВЕРКА В НОВОЙ БАЗЕ

Если контейнер со старым Drupal 6 (MySQL 5.7) уже удалён и таблицы content_type_* недоступны, проверить наличие кастомных CCK-полей можно в новой базе dniester.

Выполни:
```sql
SELECT COUNT(*) FROM custom_fields;
```

Если результат = 0 → кастомных полей в Drupal 6 не было, либо они не мигрированы.
Если результат > 0 → значит в старой базе они существовали и были перенесены как key-value записи в таблицу custom_fields.

---

## ПОСЛЕ ЗАВЕРШЕНИЯ МИГРАЦИИ

Когда вы удостоверились, что база clean_schema.sql успешно импортирована в MySQL 8.0 (контейнер news-mysql), временное окружение Drupal 6 больше не требуется.

### Docker volumes:

- **news-platform_mysql_data** → оставить (используется MySQL 8.0 контейнером news-mysql).  
- **news-platform_mysql_data_drupal6** → можно удалить (это временный том для миграции Drupal 6).

Далее есть два варианта действий:

### ВАРИАНТ 1 — просто остановить контейнер Drupal 6 и оставить volume «на всякий случай»:
```bash
docker stop news-mysql-drupal6
```

### ВАРИАНТ 2 — полностью удалить контейнер и лишний volume (рекомендуется после успешного экспорта):
```bash
docker compose -f docker-compose.drupal.yml down -v
```

После этого у вас останется только рабочий MySQL 8.0 (контейнер news-mysql) и его volume news-platform_mysql_data, которые нужны для дальнейшей работы News Platform.

---

## ЗАПУСК ПРОЕКТА

После того как новая база clean_schema.sql готова и импортирована, можно запускать проект.

Варианты:

### ВАРИАНТ А — только база (MySQL для проверки данных)

**Остановить старое окружение:**
```bash
docker compose -f docker-compose.yml down -v
```

**Поднять только MySQL:**
```bash
docker compose -f docker-compose.yml up -d mysql
```

**Проверить логи:**
```bash
docker logs -f news-mysql
```

**Импортировать базу (если ещё не залита):**
```bash
docker exec -i news-mysql mysql -uroot -proot dniester < db_data/clean_schema.sql
```

**Проверить содержимое:**
```bash
docker exec -it news-mysql mysql -uroot -proot -e "SHOW TABLES;" dniester
docker exec -it news-mysql mysql -uroot -proot -e "SELECT COUNT(*) FROM content;" dniester
```

### ВАРИАНТ Б — полное окружение (MySQL + backend Spring Boot)

**Убедиться, что создан файл .env.dev с настройками:**
```bash
MYSQL_ROOT_PASSWORD=root
MYSQL_DATABASE=dniester
SPRING_LOCAL_PORT=8080
SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/dniester?useUnicode=true&characterEncoding=utf8mb4&useSSL=false
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=root
```

**Запустить сервисы:**
```bash
docker compose --env-file .env.dev up -d
```

**В итоге:**
- news-mysql → база MySQL 8 с данными
- news-app → приложение Spring Boot

**При необходимости импортировать базу:**
```bash
docker exec -i news-mysql mysql -uroot -proot dniester < db_data/clean_schema.sql
```

**Проверить базу:**
```bash
docker exec -it news-mysql mysql -uroot -proot -e "SELECT COUNT(*) FROM content;" dniester
```

**Проверить логи приложения:**
```bash
docker logs -f news-app
```/clean_schema.sql
```

**Проверить содержимое:**
```bash
docker exec -it news-mysql mysql -uroot -proot -e "SHOW TABLES;" dniester
docker exec -it news-mysql mysql -uroot -proot -e "SELECT COUNT(*) FROM content;" dniester
```

### ВАРИАНТ Б — полное окружение (MySQL + backend Spring Boot)

**Убедиться, что создан файл .env.dev с настройками:**
```bash
MYSQL_ROOT_PASSWORD=root
MYSQL_DATABASE=dniester
SPRING_LOCAL_PORT=8080
SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/dniester?useUnicode=true&characterEncoding=utf8mb4&useSSL=false
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=root
```

**Запустить сервисы:**
```bash
docker compose --env-file .env.dev up -d
```

**В итоге:**
- news-mysql → база MySQL 8 с данными
- news-app → приложение Spring Boot

**При необходимости импортировать базу:**
```bash
docker exec -i news-mysql mysql -uroot -proot dniester < db_data/clean_schema.sql
```

**Проверить базу:**
```bash
docker exec -it news-mysql mysql -uroot -proot -e "SELECT COUNT(*) FROM content;" dniester
```

**Проверить логи приложения:**
```bash
docker logs -f news-app
```