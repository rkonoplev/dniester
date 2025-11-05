> **⚠️ Исторический документ**
> 
> Этот документ описывает **ручной процесс миграции** и теперь используется для **Сценария 2**
> (миграция нового сайта) из **[Руководства по установке](./SETUP_GUIDE_RU.md)**.
> 
> Для быстрой установки существующего проекта с уже включенными данными, пожалуйста, следуйте
> **[Актуальному руководству по миграции](./MODERN_MIGRATION_GUIDE_RU.md)**.

# Руководство по миграции: Drupal 6 → Phoebe CMS

Это подробное пошаговое руководство описывает процесс миграции контента с устаревшей
системы Drupal 6 на современную headless-архитектуру Phoebe CMS с использованием Docker и MySQL.

> Для определения ключевых терминов и технологий, пожалуйста, обратитесь к **[Глоссарию](./GLOSSARY_RU.md)**.

## Содержание
- [Общая схема процесса](#общая-схема-процесса)
- [Краткая версия (TL;DR)](#краткая-версия-tldr)
- [Полное руководство по миграции](#полное-руководство-по-миграции)
- [Полный конвейер миграции](#полный-конвейер-миграции)
- [Описание структуры данных после миграции](#описание-структуры-данных-после-миграции)
- [Запуск проекта после миграции](#запуск-проекта-после-миграции)
- [Устранение неполадок](#устранение-неполадок)
- [Справочник](#справочник)

---

## Общая схема процесса

Процесс миграции построен на принципе "извлечь, преобразовать, загрузить" (ETL)
и выглядит следующим образом:

1.  **Запуск временной среды**: Мы запускаем контейнер с MySQL 5.7, совместимый с дампом Drupal 6.
2.  **Импорт и нормализация**: Загружаем исходный дамп и применяем SQL-скрипты для очистки данных и
    приведения их к новой, современной схеме.
3.  **Экспорт чистого дампа**: Создаем финальный SQL-файл (`clean_schema.sql`), содержащий готовую
    структуру и данные.
4.  **Развертывание целевой среды**: Запускаем основной контейнер с MySQL 8.0.
5.  **Финальный импорт**: Загружаем `clean_schema.sql` в новую базу данных.

---

## Краткая версия (TL;DR)

Этот раздел для тех, у кого уже есть готовый файл `clean_schema.sql`.

```bash
# 1. Запустите контейнер с целевой базой данных MySQL 8.0
docker compose -f docker-compose.yml up -d mysql

# 2. Импортируйте очищенную схему и данные в базу 'dniester'
docker exec -i news-mysql mysql -uroot -proot dniester < legacy/archived_migration_scripts/clean_schema.sql

# 3. Убедитесь, что данные на месте (например, проверьте количество статей)
docker exec -it news-mysql mysql -uroot -proot -e "SELECT COUNT(*) FROM content;" dniester
```

---

## Полное руководство по миграции

### Шаг 1: Запуск временного MySQL 5.7 для дампа Drupal 6

Дампы старых версий Drupal могут быть несовместимы с последними версиями MySQL из-за синтаксических
различий. Поэтому мы используем временный контейнер с MySQL 5.7, который гарантирует совместимость.

**Запуск:**
```bash
# Запускаем контейнер в фоновом режиме. Файл docker-compose.drupal.yml специально
# настроен для этой задачи.
docker compose -f legacy/docker-compose.drupal.yml up -d
```

**Логи:**
```bash
# Наблюдаем за логами, чтобы убедиться, что сервер успешно стартовал
docker logs -f news-mysql-drupal6
```

**Подключение (для проверки):**
```bash
docker exec -it news-mysql-drupal6 mysql -u root -p
# пароль: root
```

**Проверка исходной базы данных Drupal 6:**
```sql
SHOW DATABASES;
USE a264971_dniester;
SHOW TABLES;
```

### Шаг 2: Экспорт исходных данных и импорт в новую базу

Теперь мы загружаем исходный дамп Drupal в нашу временную базу данных и применяем SQL-скрипты для его
преобразования.

**Экспорт исходного дампа:**
```bash
# Экспортируем исходный дамп (например, drupal6_fixed.sql) из базы a264971_dniester
# внутри нашего временного контейнера.
docker exec -i news-mysql-drupal6 mysqldump -uroot -proot a264971_dniester > legacy/archived_migration_scripts/drupal6_fixed.sql
```

**Импорт в чистую базу `dniester`:**
```bash
# Импортируем исходный дамп в базу 'dniester' внутри нашего временного контейнера.
docker exec -i news-mysql-drupal6 mysql -uroot -proot dniester < legacy/archived_migration_scripts/drupal6_fixed.sql
```

**Проверка импортированных таблиц:**
```sql
SHOW TABLES;
```

**Применение скриптов нормализации:**
```bash
# Применяем основной скрипт нормализации. Он создает новые таблицы и переносит в них
# очищенные данные из старых таблиц Drupal.
docker exec -i news-mysql-drupal6 mysql -uroot -proot dniester < legacy/archived_migration_scripts/migrate_from_drupal6_universal.sql

# Если в вашем Drupal-сайте использовался модуль CCK для создания кастомных полей,
# примените этот дополнительный скрипт для их миграции.
docker exec -i news-mysql-drupal6 mysql -uroot -proot dniester < legacy/archived_migration_scripts/migrate_cck_fields.sql
```

### Шаг 3: Экспорт очищенной схемы (`clean_schema.sql`)

После того как все данные преобразованы, мы создаем финальный, "чистый" дамп. Этот файл является главным
артефактом всего процесса миграции.

```bash
# Создаем дамп базы 'dniester' из временного контейнера и сохраняем его в файл.
docker exec -i news-mysql-drupal6 mysqldump -uroot -proot dniester > legacy/archived_migration_scripts/clean_schema.sql
```

### Шаг 4: Подготовка и запуск целевой среды (MySQL 8.0)

Временная среда нам больше не нужна. Мы останавливаем ее и запускаем основную базу данных проекта.

**Остановка временного окружения:**
```bash
# Останавливаем и полностью удаляем временный контейнер и его том.
docker compose -f legacy/docker-compose.drupal.yml down -v
```

**Запуск целевого MySQL 8.0:**
```bash
# Запускаем основной контейнер с MySQL 8.0, который будет использоваться приложением.
docker compose -f docker-compose.yml up -d mysql
```

**Проверка логов MySQL 8.0:**
```bash
docker logs news-mysql
# В логах должно быть что-то вроде: [Entrypoint]: Creating database dniester
```

### Шаг 5: Импорт финального дампа в MySQL 8.0

Загружаем наш `clean_schema.sql` в новую, основную базу данных.

```bash
docker exec -i news-mysql mysql -uroot -proot dniester < legacy/archived_migration_scripts/clean_schema.sql
```

### Шаг 6: Проверка результата

Финальный шаг — убедиться, что все данные были успешно перенесены в новую базу данных.

**Проверка таблиц:**
```bash
docker exec -it news-mysql mysql -uroot -proot -e "USE dniester; SHOW TABLES;" dniester
```

**Проверка количества записей:**
```bash
# Проверяем количество статей в таблице content
docker exec -it news-mysql mysql -uroot -proot -e "SELECT COUNT(*) FROM content;" dniester

# Проверяем количество пользователей
docker exec -it news-mysql mysql -uroot -proot -e "SELECT COUNT(*) FROM users;" dniester
# Ожидается ~12186 строк (12172 story + 14 book) для контента.
```

---

## Полный конвейер миграции

Для выполнения полной миграции от дампа Drupal 6 до чистой базы данных Phoebe CMS на MySQL 8.0
вы можете выполнить следующую последовательность команд:

```bash
# 1. Запуск временного окружения для миграции Drupal 6 (контейнер MySQL 5.7)
docker compose -f legacy/docker-compose.drupal.yml up -d

# 2. Экспорт исходных данных Drupal 6, импорт в чистую базу и запуск скриптов нормализации
docker exec -i news-mysql-drupal6 mysqldump -uroot -proot a264971_dniester > legacy/archived_migration_scripts/drupal6_fixed.sql
docker exec -i news-mysql-drupal6 mysql -uroot -proot dniester < legacy/archived_migration_scripts/drupal6_fixed.sql
docker exec -i news-mysql-drupal6 mysql -uroot -proot dniester < legacy/archived_migration_scripts/migrate_from_drupal6_universal.sql
# Опционально: Если у вас были CCK-поля в Drupal 6, раскомментируйте и выполните следующую строку:
# docker exec -i news-mysql-drupal6 mysql -uroot -proot dniester < legacy/archived_migration_scripts/migrate_cck_fields.sql

# 3. Экспорт очищенной и нормализованной схемы в clean_schema.sql
docker exec -i news-mysql-drupal6 mysqldump -uroot -proot dniester > legacy/archived_migration_scripts/clean_schema.sql

# 4. Остановка и удаление временного окружения для миграции Drupal 6
docker compose -f legacy/docker-compose.drupal.yml down -v

# 5. Запуск целевого контейнера MySQL 8.0 для Phoebe CMS
docker compose -f docker-compose.yml up -d mysql

# 6. Импорт финальной очищенной схемы в MySQL 8.0
docker exec -i news-mysql mysql -uroot -proot dniester < legacy/archived_migration_scripts/clean_schema.sql

# 7. Проверка миграции (например, подсчет строк контента)
docker exec -it news-mysql mysql -uroot -proot -e "SELECT COUNT(*) FROM content;" dniester
```

---

## Описание структуры данных после миграции

После выполнения скриптов нормализации, данные из Drupal 6 будут преобразованы в новую,
более структурированную схему.

### 1. Пользователи (`users`)
- **Источник**: Drupal `users`
- **Целевая таблица**: `users`
- **Импортируемые поля**:
  - `uid` → `id` (первичный ключ)
  - `name` → `username`
  - `mail` → `email`
  - `status` → `active` (BOOLEAN: `1` = активен, `0` = заблокирован)

### 2. Роли (`roles`) и связка пользователей с ролями (`user_roles`)
- **Источник**: Drupal `role`, `users_roles`
- **Целевые таблицы**:
  - `roles` (id, name)
  - `user_roles` (user_id, role_id)
- **Описание**: Связь многие-ко-многим между пользователями и ролями.

### 3. Контент (`content`)
- **Источник**: `node`, `node_revisions`
- **Целевая таблица**: `content` (универсальная таблица, без поля `type`)
- **Импортируемые поля**:
  - `nid` → `id`
  - `title` → `title`
  - `body` → `body`
  - `teaser` → `teaser`
  - `created` (UNIX timestamp) → `publication_date` (DATETIME)
  - `uid` → `author_id` (внешний ключ на `users.id`)

> **Важно**: Различные типы контента Drupal (`story`, `page`, `book`) теперь объединены и хранятся в одной таблице `content`.

### 4. Таксономия (`terms`, `content_terms`)
- **Источник**: `term_data`, `vocabulary`, `term_node`
- **Целевые таблицы**:
  - `terms` (id, name, vocabulary)
  - `content_terms` (content_id, term_id)
- **Описание**: Система таксономии с группировкой по словарям и таблица связей контента с терминами.

### 5. Дополнительные CCK‑поля (`custom_fields`)
- **Источник**: Drupal `content_type_*` таблицы (если присутствуют).
- **Целевая таблица**: `custom_fields` (универсальная модель ключ→значение).
- **Импортируемые поля**:
  - `nid` → `content_id`
  - Имя колонки → `field_name`
  - Значение колонки → `field_value`

### Итоговые таблицы в чистой схеме:
- `users`
- `roles`
- `user_roles`
- `content`
- `terms`
- `content_terms`
- `custom_fields`

---

## Запуск проекта после миграции

После того как база данных `clean_schema.sql` успешно импортирована в MySQL 8.0, вы можете запустить проект Phoebe CMS.

### Вариант А — Только база данных (для проверки данных)

Если вам нужно только проверить данные в MySQL без запуска Spring Boot приложения:

1.  **Остановить старое окружение (если запущено):**
    ```bash
    docker compose -f docker-compose.yml down -v
    ```

2.  **Поднять только MySQL:**
    ```bash
    docker compose -f docker-compose.yml up -d mysql
    ```

3.  **Проверить логи MySQL:**
    ```bash
    docker logs -f news-mysql
    ```

4.  **Импортировать базу (если еще не залита):**
    ```bash
    docker exec -i news-mysql mysql -uroot -proot dniester < legacy/archived_migration_scripts/clean_schema.sql
    ```

5.  **Проверить содержимое базы данных:**
    ```bash
    docker exec -it news-mysql mysql -uroot -proot -e "SHOW TABLES;" dniester
    docker exec -it news-mysql mysql -uroot -proot -e "SELECT COUNT(*) FROM content;" dniester
    ```

### Вариант Б — Полное окружение (MySQL + Backend Spring Boot)

Для запуска всего приложения:

1.  **Убедиться, что создан файл `.env.dev` с настройками:**
    ```dotenv
    MYSQL_ROOT_PASSWORD=root
    MYSQL_DATABASE=dniester
    SPRING_LOCAL_PORT=8080
    SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/dniester?useUnicode=true&characterEncoding=utf8mb4&useSSL=false
    SPRING_DATASOURCE_USERNAME=root
    SPRING_DATASOURCE_PASSWORD=root
    ```

2.  **Запустить сервисы:**
    ```bash
    docker compose --env-file .env.dev up -d
    ```
    В результате будут запущены:
    - `news-mysql`: база MySQL 8 с данными
    - `news-app`: приложение Spring Boot

3.  **При необходимости импортировать базу:**
    ```bash
    docker exec -i news-mysql mysql -uroot -proot dniester < legacy/archived_migration_scripts/clean_schema.sql
    ```

4.  **Проверить базу данных:**
    ```bash
    docker exec -it news-mysql mysql -uroot -proot -e "SELECT COUNT(*) FROM content;" dniester
    ```

5.  **Проверить логи приложения:**
    ```bash
    docker logs -f news-app
    ```

---

## Устранение неполадок

### Проблемы с кодировкой (UTF-8, кириллица)

Если вы сталкиваетесь с ошибками вида `ERROR 1366 (HY000): Incorrect string value: '\xD0\x98\xD0\xBD...' for column 'title'`, это означает, что ваша база данных создала целевую таблицу с неверной кодировкой по умолчанию (например, `latin1`). По умолчанию MySQL 5.7 (и иногда 8.0, в зависимости от конфигурации) может использовать `latin1`, если явно не указано иное.

#### Шаг 1: Проверка кодировки таблицы
Внутри вашего MySQL-контейнера (например, `news-mysql-drupal6` или `news-mysql`):
```sql
SHOW CREATE TABLE a264971_dniester.node \G
```
Обычно таблицы Drupal 6 имеют `DEFAULT CHARSET=utf8`. Теперь проверьте вашу новую таблицу `content` (вероятно, она имеет `DEFAULT CHARSET=latin1`).

#### Шаг 2: Пересоздание целевой таблицы с UTF-8
Если вы обнаружили проблемы с кодировкой, возможно, потребуется удалить и пересоздать затронутую таблицу с правильным набором символов и сопоставлением. Например, для таблицы `content`:

```sql
DROP TABLE IF EXISTS content;

CREATE TABLE content (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    body TEXT,
    teaser TEXT,
    publication_date DATETIME NOT NULL,
    published BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME,
    updated_at DATETIME,
    version BIGINT,
    author_id BIGINT NOT NULL,
    FOREIGN KEY (author_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

#### Шаг 3: Повторная вставка данных
После пересоздания таблицы с правильной кодировкой повторите шаг вставки данных из руководства по миграции. С `utf8mb4_unicode_ci` кириллица и другие многобайтовые символы должны быть вставлены корректно.

### Проблемы с паролем root в MySQL 8.0

Иногда после инициализации контейнера могут возникнуть проблемы с аутентификацией пользователя `root` (например, если MySQL 8.0 создает пользователя root с пустым паролем или аутентификацией по сокету по умолчанию). В этом случае может потребоваться ручной сброс пароля с использованием флага `--skip-grant-tables`.

#### Шаг 1: Остановите контейнер MySQL
```bash
docker stop news-mysql
```

#### Шаг 2: Запустите временный контейнер с `--skip-grant-tables`
Это позволяет подключиться к MySQL без аутентификации по паролю.
```bash
docker run -it --rm \
--name mysql-fix \
-v news-platform_mysql_data:/var/lib/mysql \
mysql:8.0 \
--skip-grant-tables --skip-networking
```

#### Шаг 3: Подключитесь к временному экземпляру MySQL
Откройте **новое окно терминала** и подключитесь к временному контейнеру:
```bash
docker exec -it mysql-fix mysql
```

#### Шаг 4: Сбросьте пароль root
В командной строке MySQL выполните следующие команды, чтобы сбросить привилегии и установить новый пароль для пользователя `root` (например, `root`):
```sql
FLUSH PRIVILEGES;
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'root';
ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY 'root';
```

#### Шаг 5: Остановите временный контейнер и перезапустите основной MySQL
Выйдите из командной строки MySQL, затем остановите контейнер `mysql-fix` (Ctrl+C в первом терминале или `docker stop mysql-fix`). Наконец, перезапустите ваш основной контейнер `news-mysql`:
```bash
docker compose -f docker-compose.yml up -d mysql
```

---

## Справочник

### Перенос устаревших файлов

После успешного завершения миграции несколько файлов, которые использовались в процессе миграции,
были перенесены в папку `legacy/` для исторической справки:

- **`DatabaseProperties.java`**: Класс конфигурации базы данных для миграции.
- **`Makefile`**: Устаревшая утилита для тестирования API.
- **`docker-compose.drupal.yml`**: Временная Docker-конфигурация для совместимости с MySQL 5.7.
- **`docker-compose.override.yml`**: Продакшн-настройки Docker с улучшениями безопасности.
- **`ExampleTest.java`**: Начальный тестовый файл из ранней разработки.

Эти файлы сохранены для образовательных целей и понимания эволюции миграции.
Подробное описание см. в `legacy/README.md`.

### Описание SQL-файлов для миграции

В процессе миграции было создано несколько SQL-скриптов, каждый со своей целью:

Подробную информацию о всех миграционных скриптах и файлах см. в [Database Migration Scripts](../db_data/README.md).

- **`drupal6_fixed.sql`**:
  Чистый дамп исходной базы Drupal 6 (`a264971_dniester`), импортированный во временный экземпляр MySQL 5.7.
  Назначение: нормализация имени базы данных и обеспечение совместимости для дальнейших шагов миграции.

- **`migrate_from_drupal6_universal.sql`**:
  Основной миграционный скрипт. Он создает новую чистую схему (`users`, `roles`, `user_roles`, `content`,
  `terms`, `content_terms`) и переносит очищенные данные из старых таблиц Drupal. Ядро миграции.

- **`detect_custom_fields.sql`**:
  Вспомогательный скрипт (опционально). Выполняет запросы к `information_schema` для обнаружения таблиц вида
  `content_type_*` с дополнительными CCK-полями. Примечание: Если этот скрипт возвращает строки, это означает,
  что у вас были кастомные поля. Если результат пуст, у вас **не было CCK-полей** в дампе Drupal 6.

- **`migrate_cck_fields.sql`**:
  Опциональный скрипт. Требуется только при наличии CCK-полей. Он копирует поля из таблиц `content_type_*`
  в универсальную таблицу `custom_fields` (модель ключ→значение). Если CCK не использовался, этот скрипт можно игнорировать.

- **`clean_schema.sql`**:
  Финальный экспорт после применения `migrate_from_drupal6_universal.sql` (и опционально `migrate_cck_fields.sql`).
  Это схема и данные, используемые в MySQL 8.0 платформой Phoebe CMS.

### Проверка кастомных полей

Крайне важно проверить, присутствовали ли кастомные поля (CCK) из вашей установки Drupal 6 и были ли они корректно мигрированы.

#### 1. Проверка в MySQL 5.7 (контейнер Drupal)
Если ваш временный контейнер Drupal 6 все еще запущен, вы можете проверить наличие таблиц `content_type_*`:
```sql
SHOW TABLES LIKE 'content_type%';
```
- Если вы видите строки, такие как `content_type_article`, `content_type_news` и т.д., это указывает на наличие кастомных CCK-полей.
- Если ничего не возвращается, вы не использовали Drupal CCK, и таблица `custom_fields` останется пустой и может быть проигнорирована.

#### 2. Проверка кастомных полей в новой базе данных (MySQL 8.0)
Если исходный контейнер Drupal 6 уже остановлен или удален, вы не сможете напрямую проверять таблицы `content_type_*`. Однако вы все еще можете проверить, были ли мигрированы какие-либо CCK-поля, проверив таблицу `custom_fields` в вашей новой схеме `dniester`:

```sql
SELECT COUNT(*) FROM custom_fields;
```
- Если результат равен `0`, у вас не было кастомных CCK-полей, либо их миграция была пропущена.
- Если результат больше `0`, у вас были кастомные поля в Drupal 6, и они были мигрированы в `custom_fields` как записи ключ→значение.

### Очистка после миграции

После завершения миграции и успешного импорта `clean_schema.sql` в MySQL 8.0 рекомендуется очистить временное окружение Drupal 6, чтобы освободить ресурсы.

#### Docker-тома

- **`news-platform_mysql_data`**: **Сохраните этот том**. Он используется вашим основным контейнером MySQL 8.0 (`news-mysql`) для данных Phoebe CMS.
- **`news-platform_mysql_data_drupal6`**: **Этот том можно безопасно удалить**. Это остаток от процесса миграции Drupal 6, и он больше не нужен.

#### Варианты очистки

**Вариант А — Просто остановить контейнер Drupal 6 (сохранить том на всякий случай):**
Это более мягкий подход, сохраняющий том на случай, если вам потребуется повторно его изучить позже.
```bash
docker stop news-mysql-drupal6
```

**Вариант Б — Полностью удалить контейнер Drupal 6 и его том (рекомендуется после успешного экспорта):**
Этот вариант освобождает дисковое пространство и рекомендуется после того, как вы убедитесь, что миграция завершена и успешна.
```bash
docker compose -f legacy/docker-compose.drupal.yml down -v
```
После очистки останутся только MySQL 8.0 (`news-mysql`) и его постоянный том (`phoebe_mysql_data`) для дальнейшей работы с Phoebe CMS.
