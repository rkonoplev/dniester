# Руководство по миграции Docker Volume

Это руководство объясняет, как безопасно мигрировать Docker volumes при переименовании контейнеров 
с `phoebe-mysql` на `phoebe-mysql`.

## Текущее состояние
- Контейнер: `phoebe-mysql`
- Volume: `mysql_data` (или `phoebe_mysql_data`)
- База данных: `phoebe_db` (ранее `dniester`)

## Шаги миграции

### Шаг 1: Создание резервной копии базы данных
```bash
# Остановить текущие сервисы
docker compose down

# Запустить только MySQL для создания бэкапа
docker compose up -d phoebe-mysql

# Дождаться готовности MySQL
docker exec phoebe-mysql mysqladmin ping -h localhost --silent

# Создать резервную копию
docker exec phoebe-mysql mysqldump -uroot -proot --all-databases > backup_before_migration.sql
```

### Шаг 2: Обновление docker-compose.yml
```bash
# Обновить имя контейнера в docker-compose.yml
sed -i 's/phoebe-mysql:/phoebe-mysql:/g' docker-compose.yml
sed -i 's/container_name: phoebe-mysql/container_name: phoebe-mysql/g' docker-compose.yml
sed -i 's/phoebe-mysql:/phoebe-mysql:/g' docker-compose.yml
```

### Шаг 3: Миграция данных Volume
```bash
# Остановить старый контейнер
docker stop phoebe-mysql
docker rm phoebe-mysql

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
docker compose up -d phoebe-mysql
docker exec -i phoebe-mysql mysql -uroot -proot < backup_before_migration.sql
```

## Автоматизированные скрипты миграции (выполнено)

**Примечание**: Скрипты миграции перемещены в папку `legacy/` после успешного завершения.

Миграция была выполнена с помощью этих скриптов:

```bash
# Скрипт миграции (теперь в legacy/migrate_volumes.sh)
./migrate_volumes.sh

# Скрипт отката (теперь в legacy/rollback_migration.sh)
./rollback_migration.sh
```

Подробности об этих скриптах см. в [legacy/README.md](../../legacy/README.md).

## Полный процесс миграции (выполнено)

### Что было сделано:

1. **Исправлена ошибка MapStruct** в ChannelSettingsMapper:
   ```java
   @Mapping(target = "id", ignore = true)
   void updateEntity(@MappingTarget ChannelSettings entity, ChannelSettingsUpdateDto dto);
   ```

2. **Исправлен docker-compose.yml** - изменен build context:
   ```yaml
   phoebe-app:
     build:
       context: ./backend  # было: context: .
       dockerfile: Dockerfile.dev
   ```

3. **Выполнена миграция volumes**:
   - Создан бэкап в `db_dumps/backup_before_migration_*.sql`
   - Переименованы все контейнеры: `news-*` → `phoebe-*`
   - База данных: `dniester` → `phoebe_db`
   - Все данные сохранены

### Команды для запуска после миграции:

```bash
# Собрать приложение
docker compose build phoebe-app

# Запустить все сервисы
docker compose up -d

# Или только нужные (без Next.js)
docker compose up -d phoebe-mysql phoebe-app

# Проверить статус
docker ps

# Проверить API
curl http://localhost:8080/actuator/health
```

### Результат миграции:
- ✅ Контейнеры переименованы в `phoebe-*`
- ✅ База данных работает с новыми именами
- ✅ Приложение собирается без предупреждений
- ✅ Создан бэкап для безопасности
- ✅ Все конфигурации обновлены

## Очистка
После успешной миграции:
```bash
# Удалить файлы резервных копий (опционально)
rm db_dumps/backup_before_migration_*.sql

# Удалить бэкап docker-compose
rm docker-compose.yml.bak
```