> [Вернуться к содержанию документации](./README.md)

# Шпаргалка по командам MySQL

Этот документ содержит полезные команды для работы с MySQL, как в Docker-контейнере, так и при локальной установке.

---

## Работа с MySQL в Docker-контейнере

Предполагается, что ваш MySQL-контейнер запущен и имеет имя `phoebe-mysql`.

### Подключение в интерактивный режим:

```bash
docker exec -it phoebe-mysql mysql -uroot -proot
```

### Внутри MySQL (после подключения, появляется `mysql>`):

**Посмотреть все базы данных:**
```sql
SHOW DATABASES;
```

**Переключиться в базу данных:**
```sql
USE phoebe_db;
```

**Посмотреть таблицы в текущей базе:**
```sql
SHOW TABLES;
```

**Пример: посчитать записи в таблице `content`:**
```sql
SELECT COUNT(*) FROM content;
```

**Выйти из MySQL-клиента:**
```sql
EXIT;
```

### Экспорт данных (создание дампа БД):

**Дамп всей базы `phoebe_db`:**
```bash
docker exec -i phoebe-mysql mysqldump -uroot -proot phoebe_db > db_data/exported_dump.sql
```

**Дамп конкретной таблицы (например, `users`):**
```bash
docker exec -i phoebe-mysql mysqldump -uroot -proot phoebe_db users > db_data/users_dump.sql
```

### Импорт данных (загрузка дампа в БД):

```bash
docker exec -i phoebe-mysql mysql -uroot -proot phoebe_db < db_data/exported_dump.sql
```

### Примечания:

-   В командах экспорта/импорта указывайте имя базы данных (например, `phoebe_db`).
-   Перед импортом база данных должна существовать.
-   Дамп — это обычный `.sql` файл, его можно хранить в папке `db_data` для удобства.

---

## Работа с локальной установкой MySQL

Если MySQL установлен непосредственно на вашей машине, команды будут похожи, но без префикса `docker exec`.
Предполагается, что MySQL-сервер запущен, и вы знаете имя пользователя (`-u`) и пароль (`-p`).

### Подключение в интерактивный режим:

```bash
mysql -u <username> -p
# Например: mysql -u root -p
```
После ввода команды система запросит пароль.

### Внутри MySQL (после подключения, появляется `mysql>`):

Команды внутри MySQL-клиента идентичны тем, что используются в Docker-контейнере:

**Посмотреть все базы данных:**
```sql
SHOW DATABASES;
```

**Переключиться в базу данных:**
```sql
USE phoebe_db;
```

**Посмотреть таблицы в текущей базе:**
```sql
SHOW TABLES;
```

**Пример: посчитать записи в таблице `content`:**
```sql
SELECT COUNT(*) FROM content;
```

**Выйти из MySQL-клиента:**
```sql
EXIT;
```

### Экспорт данных (создание дампа БД):

**Дамп всей базы `phoebe_db`:**
```bash
mysqldump -u <username> -p phoebe_db > db_data/exported_dump.sql
# Например: mysqldump -u root -p phoebe_db > db_data/exported_dump.sql
```

**Дамп конкретной таблицы (например, `users`):**
```bash
mysqldump -u <username> -p phoebe_db users > db_data/users_dump.sql
# Например: mysqldump -u root -p phoebe_db users > db_data/users_dump.sql
```

### Импорт данных (загрузка дампа в БД):

```bash
mysql -u <username> -p phoebe_db < db_data/exported_dump.sql
# Например: mysql -u root -p phoebe_db < db_data/exported_dump.sql
```

---