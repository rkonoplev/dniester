## ✅ Дополнялка (по-русски)

Пошаговая инструкция, чтобы ничего не забыть:

1. **Первый запуск**:
    - `cp .env.dev.example .env.dev`
    - проверь, что там `root/root` или свои пароли.
    - запусти `docker compose --env-file .env.dev up -d`.

2. **Проверка**:
    - `docker ps` должно показать `news-app` и `news-mysql`.
    - Зайди на `http://localhost:8080/swagger-ui/index.html`.

3. **Если тесты не запускаются (лезет MySQL)**:
    - проверь, что в `application.yml` **нет** `spring.profiles.active: local`.
    - в `src/test/resources/application-test.yml` отключён Flyway.
    - запускай тесты `./gradlew clean test`.

4. **Сброс БД**:
    - `docker compose down -v` → сотрёт volume.
    - потом `docker compose up` → создаст чистую базу.

5. **CI/CD**:
    - На GitHub Actions запускается профиль `ci` (H2).
    - В продакшене работает `docker-compose.override.yml` c секретами.