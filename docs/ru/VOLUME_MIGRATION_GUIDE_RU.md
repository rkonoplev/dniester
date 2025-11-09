# Руководство по миграции Docker Volume

Это руководство объясняет, как безопасно мигрировать Docker volumes при переименовании контейнеров 
с `news-mysql` на `phoebe-mysql`.

## Текущее состояние
- Контейнер: `news-mysql`
- Volume: `mysql_data` (или `phoebe_mysql_data`)
- База данных: `dniester` или `phoebe_db`

## Шаги миграции

### Шаг 1: Создание резервной копии базы данных
```bash
# Остановить текущие сервисы
docker compose down

# Запустить только MySQL для создания бэкапа
docker compose up -d news-mysql

# Дождаться готовности MySQL
docker exec news-mysql mysqladmin ping -h localhost --silent

# Создать резервную копию
docker exec news-mysql mysqldump -uroot -proot --all-databases > backup_before_migration.sql
```

### Шаг 2: Обновление docker-compose.yml
```bash
# Обновить имя контейнера в docker-compose.yml
sed -i 's/news-mysql:/phoebe-mysql:/g' docker-compose.yml
sed -i 's/container_name: news-mysql/container_name: phoebe-mysql/g' docker-compose.yml
sed -i 's/news-mysql:/phoebe-mysql:/g' docker-compose.yml
```

### Шаг 3: Миграция данных Volume
```bash
# Остановить старый контейнер
docker stop news-mysql
docker rm news-mysql

# Запустить новый контейнер с тем же volume
docker compose up -d phoebe-mysql

# Дождаться готовности MySQL
docker exec phoebe-mysql mysqladmin ping -h localhost --silent

# Проверить целостность данных
docker exec phoebe-mysql mysql -uroot -proot -e "SHOW DATABASES;"
```

### Шаг 4: Обновление переменных окружения (при необходимости)
```bash
# Обновить имя базы данных при миграции с dniester на phoebe_db
docker exec -it phoebe-mysql mysql -uroot -proot -e "CREATE DATABASE IF NOT EXISTS phoebe_db;"
docker exec -it phoebe-mysql mysql -uroot -proot -e "RENAME TABLE dniester.* TO phoebe_db.*;" # MySQL 8.0+
# ИЛИ использовать подход с mysqldump:
docker exec phoebe-mysql mysqldump -uroot -proot dniester > temp_db.sql
docker exec -i phoebe-mysql mysql -uroot -proot phoebe_db < temp_db.sql
```

### Шаг 5: Обновление конфигурации приложения
Обновить URL базы данных в `docker-compose.yml`:
```yaml
SPRING_DATASOURCE_URL: jdbc:mysql://phoebe-mysql:3306/${MYSQL_DATABASE:-phoebe_db}
```

### Шаг 6: Тестирование миграции
```bash
# Запустить все сервисы
docker compose up -d

# Проверить подключение приложения
curl http://localhost:8080/health

# Проверить содержимое базы данных
docker exec phoebe-mysql mysql -uroot -proot -e "SELECT COUNT(*) FROM phoebe_db.content;"
```

## План отката
Если миграция не удалась:
```bash
# Остановить новые сервисы
docker compose down

# Восстановить старое имя контейнера в docker-compose.yml
git checkout docker-compose.yml

# Восстановить из резервной копии
docker compose up -d news-mysql
docker exec -i news-mysql mysql -uroot -proot < backup_before_migration.sql
```

## Очистка
После успешной миграции:
```bash
# Удалить файл резервной копии
rm backup_before_migration.sql

# Обновить ссылки в документации
# (Уже выполнено в предыдущих коммитах)
```