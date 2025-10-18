# Инструкция по миграции: Drupal 6 → Phoebe CMS

Это подробное руководство описывает процесс миграции с устаревшей системы Drupal 6 на современную headless-архитектуру Phoebe CMS с использованием MySQL.

## Содержание
- [Краткая версия (TL;DR)](#краткая-версия-tldr)
- [Полное руководство по миграции](#полное-руководство-по-миграции)
- [Описание структуры данных после миграции](#описание-структуры-данных-после-миграции)
- [Устранение неполадок](#устранение-неполадок)

---

## Краткая версия (TL;DR)

Если у вас уже есть готовый файл `clean_schema.sql`:

```bash
# 1. Запустите контейнер MySQL 8.0
docker compose -f docker-compose.yml up -d mysql

# 2. Импортируйте очищенную схему и данные
docker exec -i news-mysql mysql -uroot -proot dniester < db_data/clean_schema.sql

# 3. Проверьте результат
docker exec -it news-mysql mysql -uroot -proot -e "SELECT COUNT(*) FROM content;" dniester
```

---

## Полное руководство по миграции

Процесс состоит из нескольких ключевых этапов: от подготовки временной среды до импорта очищенных данных в целевую базу данных.

### Шаг 1: Запуск временного MySQL 5.7 для дампа Drupal 6

Поскольку дампы Drupal 6 могут быть несовместимы с последними версиями MySQL, мы используем временный контейнер с MySQL 5.7.

```bash
# Запуск контейнера
docker compose -f docker-compose.drupal.yml up -d

# Проверка логов
docker logs -f news-mysql-drupal6
```

### Шаг 2: Импорт и нормализация данных

На этом этапе мы импортируем исходный дамп Drupal и применяем скрипты для его очистки и приведения к новой схеме.

```bash
# Импорт исходного дампа в базу 'dniester'
docker exec -i news-mysql-drupal6 mysql -uroot -proot dniester < db_data/drupal6_fixed.sql

# Применение основного скрипта нормализации
docker exec -i news-mysql-drupal6 mysql -uroot -proot dniester < db_data/migrate_from_drupal6_universal.sql

# Применение скрипта для кастомных полей (если они были)
docker exec -i news-mysql-drupal6 mysql -uroot -proot dniester < db_data/migrate_cck_fields.sql
```

### Шаг 3: Экспорт очищенной схемы

После нормализации мы создаем финальный дамп, который будет использоваться в основной системе.

```bash
docker exec -i news-mysql-drupal6 mysqldump -uroot -proot dniester > db_data/clean_schema.sql
```

### Шаг 4: Подготовка и запуск MySQL 8.0

Теперь мы можем остановить временный контейнер и запустить целевую базу данных.

```bash
# Остановка и удаление временного окружения
docker compose -f docker-compose.drupal.yml down -v

# Запуск основного контейнера с MySQL 8.0
docker compose -f docker-compose.yml up -d mysql
```

### Шаг 5: Импорт финального дампа в MySQL 8.0

Загружаем наши очищенные данные в новую базу данных.

```bash
docker exec -i news-mysql mysql -uroot -proot dniester < db_data/clean_schema.sql
```

### Шаг 6: Проверка результата

Убедимся, что все данные были успешно перенесены.

```bash
# Проверка количества статей
docker exec -it news-mysql mysql -uroot -proot -e "SELECT COUNT(*) FROM content;" dniester

# Проверка количества пользователей
docker exec -it news-mysql mysql -uroot -proot -e "SELECT COUNT(*) FROM users;" dniester
```

---

## Описание структуры данных после миграции

После выполнения скриптов нормализации, данные из Drupal 6 будут преобразованы в новую, более структурированную схему.

- **Пользователи (`users`)**: Основная информация о пользователях.
- **Роли (`roles`)**: Роли, такие как ADMIN, EDITOR.
- **Контент (`content`)**: Все типы контента (статьи, страницы) объединены в одну таблицу.
- **Таксономия (`terms`)**: Термины (категории, теги) с сохранением исходных словарей (`vocabulary`).
- **Связующие таблицы**: `user_roles`, `content_terms`, `role_permissions` для управления связями.

> Для получения полного описания финальной схемы, обратитесь к [Руководству по базе данных](./DATABASE_GUIDE_RU.md).

---

## Устранение неполадок

### Проблемы с кодировкой (UTF-8)
Если при импорте возникают ошибки вида `Incorrect string value`, это означает, что ваша база данных использует неверную кодировку по умолчанию. Убедитесь, что все таблицы создаются с `DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci`.

### Проблемы с паролем root в MySQL 8.0
Иногда после инициализации контейнера могут возникнуть проблемы с аутентификацией пользователя `root`. В этом случае может потребоваться ручной сброс пароля с использованием флага `--skip-grant-tables`. Подробные инструкции можно найти в англоязычной документации.
