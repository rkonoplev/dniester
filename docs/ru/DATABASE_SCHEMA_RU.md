# Схема базы данных – Phoebe CMS

Этот документ описывает **текущую схему базы данных MySQL 8**, используемую в Phoebe CMS.  
Схема поддерживает как **чистые установки**, так и **мигрированные данные из Drupal 6**.

## Возможности схемы

- **Управление пользователями**: Аутентификация с ролевым контролем доступа
- **Система разрешений**: Детализированные разрешения, назначаемые ролям
- **Управление контентом**: Единое хранилище для новостных статей и контента
- **Система таксономии**: Категории и теги с гибкими словарями
- **Рабочий процесс публикации**: Состояния черновик/опубликовано с аудитом
- **Поддержка миграции**: Обработка трансформации данных из Drupal 6

---

## Текущая схема базы данных (После всех миграций V1-V6)

```sql
-- ======================================
-- ТАБЛИЦА ПОЛЬЗОВАТЕЛЕЙ
-- ======================================
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,              -- BCrypt хешированные пароли
    email VARCHAR(255) UNIQUE,
    active BOOLEAN NOT NULL DEFAULT TRUE         -- true = активен, false = заблокирован
);

-- ======================================
-- ТАБЛИЦА РОЛЕЙ
-- ======================================
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,            -- 'ADMIN', 'EDITOR', и т.д.
    description VARCHAR(255)                     -- Описание роли (добавлено в V5)
);

-- ======================================
-- ТАБЛИЦА РАЗРЕШЕНИЙ (Добавлена в V5)
-- ======================================
CREATE TABLE permissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE            -- 'news:read', 'users:create', и т.д.
);

-- ======================================
-- USER_ROLES (Многие-ко-многим)
-- ======================================
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- ======================================
-- ROLE_PERMISSIONS (Многие-ко-многим, Добавлена в V5)
-- ======================================
CREATE TABLE role_permissions (
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

-- ======================================
-- ТАБЛИЦА КОНТЕНТА (Новостные статьи)
-- ======================================
CREATE TABLE content (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    body TEXT,                                   -- Полный текст статьи
    teaser TEXT,                                 -- Краткое описание/анонс
    publication_date DATETIME NOT NULL,
    published BOOLEAN NOT NULL DEFAULT FALSE,    -- Добавлено в V2: черновик/опубликовано
    created_at DATETIME,                         -- Аудит: время создания
    updated_at DATETIME,                         -- Аудит: последнее изменение
    version BIGINT,                              -- Оптимистичная блокировка
    author_id BIGINT NOT NULL,                   -- FK на users.id
    FOREIGN KEY (author_id) REFERENCES users(id)
);

-- ======================================
-- ТАБЛИЦА ТЕРМИНОВ (Таксономия)
-- ======================================
CREATE TABLE terms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    vocabulary VARCHAR(100),                     -- 'category', 'tag', и т.д.
    UNIQUE (name, vocabulary)
);

-- ======================================
-- CONTENT_TERMS (Многие-ко-многим)
-- ======================================
CREATE TABLE content_terms (
    content_id BIGINT NOT NULL,
    term_id BIGINT NOT NULL,
    PRIMARY KEY (content_id, term_id),
    FOREIGN KEY (content_id) REFERENCES content(id) ON DELETE CASCADE,
    FOREIGN KEY (term_id) REFERENCES terms(id) ON DELETE CASCADE
);
```

## Аутентификация и контроль доступа

### Пользователи и пароли по умолчанию

**⚠️ Важно**: Это пароли для входа на сайт (веб-интерфейс CMS), а не пароли к базе данных MySQL.

В документации описаны учетные данные пользователей системы Phoebe CMS для входа в CMS:

#### Для чистой базы данных (Новая установка)
После выполнения миграций V1-V6 на свежей базе данных:

| Имя пользователя | Пароль | Роль | Email | Назначение |
|------------------|--------|------|-------|------------|
| `admin` | `admin` | ADMIN | admin@example.com | Системный администратор |

#### Для мигрированной базы данных (Из Drupal 6)
После выполнения скриптов миграции + `update_migrated_users.sql`:

| Имя пользователя | Пароль | Роль | Email | Назначение |
|------------------|--------|------|-------|------------|
| `admin` | `admin` | ADMIN | admin@phoebe.local | Системный администратор |
| Все мигрированные пользователи | `changeme123` | - | user{id}@migrated.local | Унаследованные (должны сменить пароль) |

### Безопасность паролей
- Все пароли хранятся как **BCrypt хеши** (сила 10-12)
- Мигрированные пользователи **должны сменить пароль** при первом входе
- Пароль администратора следует немедленно изменить в продакшене

### Система разрешений

Система использует именование разрешений **ресурс:действие**:

| Разрешение | Описание |
|------------|----------|
| `news:read` | Просмотр новостных статей |
| `news:create` | Создание новых статей |
| `news:update` | Редактирование существующих статей |
| `news:delete` | Удаление статей |
| `news:publish` | Публикация/снятие с публикации статей |
| `users:read` | Просмотр учетных записей пользователей |
| `users:create` | Создание новых пользователей |
| `users:update` | Редактирование учетных записей пользователей |
| `users:delete` | Удаление пользователей |
| `roles:*` | Разрешения на управление ролями |
| `terms:*` | Разрешения на управление таксономией |

### Разрешения ролей по умолчанию

**Роль ADMIN:**
- Все разрешения (полный доступ к системе)

**Роль EDITOR:**
- `news:read`, `news:create`, `news:update`, `news:publish`
- `terms:read`

## История миграций

| Миграция | Назначение | Изменения |
|----------|------------|-----------|
| V1 | Начальная схема | Основные таблицы: users, roles, content, terms |
| V2 | Рабочий процесс публикации | Добавлена колонка `published` в content |
| V3 | Тестовые данные | Пользователь admin по умолчанию и тестовый контент |
| V4 | Унификация пользователей | Консолидация мигрированных авторов |
| V5 | Система разрешений | Добавлены таблицы permissions и role_permissions |
| V6 | Настройка разрешений | Заполнены разрешения по умолчанию и назначения ролей |

## Примеры запросов

```sql
-- Список всех опубликованных статей с авторами
SELECT c.title, c.publication_date, u.username as author
FROM content c
JOIN users u ON c.author_id = u.id
WHERE c.published = TRUE
ORDER BY c.publication_date DESC;

-- Получение разрешений пользователя через роли
SELECT u.username, p.name as permission
FROM users u
JOIN user_roles ur ON u.id = ur.user_id
JOIN roles r ON ur.role_id = r.id
JOIN role_permissions rp ON r.id = rp.role_id
JOIN permissions p ON rp.permission_id = p.id
WHERE u.active = TRUE;

-- Поиск статей по категории
SELECT c.title, t.name as category
FROM content c
JOIN content_terms ct ON c.id = ct.content_id
JOIN terms t ON ct.term_id = t.id
WHERE t.vocabulary = 'category' AND t.name = 'General';
```

## Инструкции по настройке базы данных

### Для новой установки (Чистая база данных)

1. **Запустить MySQL 8.0**:
   ```bash
   docker compose up -d
   ```

2. **Запустить Spring Boot** (автоматически применяет миграции V1-V6):
   ```bash
   cd backend
   ./gradlew bootRun --args='--spring.profiles.active=local'
   ```

3. **Первый вход**:
   - Имя пользователя: `admin`
   - Пароль: `admin`
   - **⚠️ Немедленно смените пароль!**

### Для мигрированной базы данных (Из Drupal 6)

1. **Импортировать данные Drupal 6** (если доступны)
2. **Выполнить скрипты миграции**:
   ```bash
   mysql phoebe_db < db_data/migrate_from_drupal6_universal.sql
   mysql phoebe_db < db_data/update_migrated_users.sql
   ```

3. **Запустить приложение** (применяет оставшиеся миграции)
4. **Варианты первого входа**:
   - Администратор: имя пользователя `admin`, пароль `admin`
   - Мигрированные пользователи: их исходное имя пользователя, пароль `changeme123`

### Контрольный список безопасности

- [ ] Изменить пароль администратора по умолчанию
- [ ] Принудительно сбросить пароли для всех мигрированных пользователей
- [ ] Проверить и настроить разрешения ролей
- [ ] Включить HTTPS в продакшене
- [ ] Настроить правильные учетные данные базы данных
- [ ] Настроить процедуры резервного копирования