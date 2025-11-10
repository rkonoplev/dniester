# Справочник команд Git и Bash

Этот документ содержит практические команды Git и Bash, используемые при разработке и устранении проблем в проекте Phoebe CMS.

## Содержание
- [Команды Git](#команды-git)
- [Команды Bash](#команды-bash)
- [Команды Gradle](#команды-gradle)
- [Команды Docker](#команды-docker)
- [Операции с файлами](#операции-с-файлами)
- [Устранение проблем](#устранение-проблем)

---

## Команды Git

### Основные операции Git
```bash
# Проверить статус репозитория
git status

# Добавить файлы в индекс
git add .
git add specific-file.txt

# Зафиксировать изменения
git commit -m "Ваше сообщение коммита"

# Отправить в удаленный репозиторий
git push origin main
git push -u origin main  # Установить upstream и отправить

# Получить последние изменения
git pull origin main
```

### Обработка конфликтов слияния
```bash
# Получить изменения со стратегией merge
git pull origin main --no-rebase

# Разрешить конфликты, оставив локальные изменения
git checkout --ours .
git add .
git commit -m "Resolve merge conflicts by keeping local changes"

# Разрешить конфликты, оставив удаленные изменения
git checkout --theirs .
git add .
git commit -m "Resolve merge conflicts by keeping remote changes"
```

### Принудительная отправка (использовать осторожно)
```bash
# Принудительная отправка (перезаписывает удаленный репозиторий)
git push --force origin main

# Более безопасная принудительная отправка (проверяет, не отправил ли кто-то еще)
git push --force-with-lease origin main
```

### Конфигурация Git
```bash
# Увеличить буферы Git для больших репозиториев
git config http.postBuffer 1048576000  # 1GB
git config http.maxRequestBuffer 1048576000
git config pack.windowMemory 256m
git config core.compression 1

# Установить поведение pull
git config pull.rebase false  # Использовать merge
git config pull.rebase true   # Использовать rebase

# Автоматическая настройка отслеживания удаленных веток
git config --global push.autoSetupRemote true
```

### Информация о репозитории
```bash
# Просмотр истории коммитов
git log --oneline -10

# Проверить удаленные репозитории
git remote -v

# Проверить отслеживание веток
git branch -vv

# Подсчет объектов и размер репозитория
git count-objects -vH
du -sh .git
```

---

## Команды Bash

### Операции с файлами и директориями
```bash
# Список содержимого директории с подробностями
ls -la

# Проверить время модификации файлов
stat -f "%Sm %N" -t "%Y-%m-%d %H:%M:%S" filename

# Найти файлы по размеру
find . -size +50M -not -path "./.git/*" -not -path "./node_modules/*"

# Проверить использование диска
du -sh folder_name
du -sh node_modules

# Подсчитать файлы в директории
ls directory_name | wc -l
```

### Обработка текста
```bash
# Просмотр определенных строк из файла
sed -n '10,15p' filename.txt
sed -n '12p' filename.txt  # Просмотр строки 12

# Поиск по шаблонам
grep -r "pattern" /path/to/search/
grep -l "pattern" *.java  # Список файлов, содержащих шаблон

# Поиск с исключениями
find /path -name "*.java" -exec grep -l "pattern" {} \;
```

### Информация о процессах и системе
```bash
# Проверить запущенные процессы
ps aux | grep java

# Проверить системные ресурсы
top
htop

# Проверить сетевые соединения
netstat -an | grep :8080
lsof -i :8080
```

---

## Команды Gradle

### Основные операции Gradle
```bash
# Сделать gradlew исполняемым
chmod +x gradlew

# Очистка и сборка
./gradlew clean build

# Запуск тестов
./gradlew test
./gradlew integrationTest

# Запуск с определенным профилем
SPRING_PROFILES_ACTIVE=ci ./gradlew test

# Запуск с отладкой
./gradlew test --debug --stacktrace
```

### Проверки качества кода
```bash
# Запуск Checkstyle
./gradlew checkstyleMain checkstyleTest checkstyleIntegrationTest

# Запуск PMD
./gradlew pmdMain pmdTest

# Генерация отчета о покрытии тестами
./gradlew jacocoTestReport
```

---

## Команды Docker

### Управление контейнерами
```bash
# Запуск сервисов
docker compose up -d
docker compose up --build

# Остановка сервисов
docker compose down

# Проверка запущенных контейнеров
docker compose ps
docker ps

# Просмотр логов контейнера
docker logs container_name
docker compose logs service_name

# Выполнение команд в контейнере
docker exec -it container_name bash
docker exec -it phoebe-mysql mysql -uroot -proot
```

### Операции с базой данных
```bash
# Подключение к MySQL в Docker
docker exec -it phoebe-mysql mysql -uroot -proot

# Ожидание готовности MySQL
timeout 60s bash -c 'until docker exec phoebe-mysql mysqladmin ping -h localhost --silent; do sleep 2; done'

# Проверка статуса MySQL
docker exec phoebe-mysql mysqladmin ping -h localhost --silent
```

---

## Операции с файлами

### Поиск и замена
```bash
# Поиск файлов, содержащих текст
grep -r "search_text" /path/to/directory/

# Поиск файлов по шаблону имени
find . -name "*.yml" -type f

# Поиск в определенных типах файлов
find . -name "*.java" -exec grep -l "pattern" {} \;
```

### Права доступа к файлам
```bash
# Сделать файл исполняемым
chmod +x filename

# Изменить права доступа к файлу
chmod 755 filename
chmod 644 filename
```

### Операции с архивами
```bash
# Создать tar архив
tar -czf archive.tar.gz directory/

# Извлечь tar архив
tar -xzf archive.tar.gz

# Просмотр содержимого архива
tar -tzf archive.tar.gz
```

---

## Устранение проблем

### Распространенные проблемы и решения

#### Ошибки Git Push
```bash
# Проблема: HTTP 400 ошибка при push
# Решение: Увеличить буферы Git
git config http.postBuffer 1048576000

# Проблема: Нарушения правил репозитория (запрет merge коммитов)
# Решение: Использовать rebase или отключить правило в настройках GitHub
git rebase -i HEAD~3  # Интерактивный rebase для объединения коммитов
```

#### Проблемы с большими репозиториями
```bash
# Проверить размер репозитория
git count-objects -vH

# Найти большие файлы в истории
git rev-list --objects --all | git cat-file --batch-check='%(objecttype) %(objectname) %(objectsize) %(rest)' | awk '/^blob/ {print substr($0,6)}' | sort --numeric-sort --key=2 | tail -10

# Очистка репозитория (использовать осторожно)
git gc --aggressive --prune=now
```

#### Проблемы Node.js/npm
```bash
# Проверить размер node_modules
du -sh node_modules

# Очистить кэш npm
npm cache clean --force

# Переустановить зависимости
rm -rf node_modules package-lock.json
npm install
```

#### Падающие тесты
```bash
# Запуск тестов с подробным выводом
./gradlew test --info

# Запуск определенного тестового класса
./gradlew test --tests "ClassName"

# Запуск тестов с определенным профилем
SPRING_PROFILES_ACTIVE=test ./gradlew test
```

### Отладка окружения
```bash
# Проверить переменные окружения
env | grep SPRING
echo $SPRING_PROFILES_ACTIVE

# Проверить версию Java
java -version
./gradlew -version

# Проверить статус Docker
docker --version
docker compose version
```

---

## Лучшие практики

### Рабочий процесс Git
1. Всегда проверяйте статус перед коммитом: `git status`
2. Используйте описательные сообщения коммитов
3. Получайте изменения перед отправкой: `git pull origin main`
4. Используйте `--force-with-lease` вместо `--force` при необходимости
5. Делайте коммиты атомарными и сфокусированными

### Управление файлами
1. Используйте `.gitignore` для генерируемых файлов (`node_modules`, `build/`, `.env`)
2. Проверяйте размеры файлов перед коммитом больших файлов
3. Используйте относительные пути в документации
4. Поддерживайте синхронизацию конфигурационных файлов между языками

### Рабочий процесс разработки
1. Тестируйте локально перед отправкой: `./gradlew test`
2. Проверяйте качество кода: `./gradlew checkstyleMain pmdMain`
3. Используйте подходящие профили Spring для разных окружений
4. Мониторьте использование ресурсов во время разработки

Этот справочник поможет разработчикам быстро находить и использовать правильные команды для обычных задач разработки.