# Руководство по стратегии тестирования

Этот документ объясняет комплексную стратегию тестирования, используемую в Phoebe CMS, включая гибридный подход для интеграционного тестирования.

---

## Обзор архитектуры тестирования

### Типы тестов и среды

| Тип теста | Среда | База данных | Базовый класс | Профиль |
|-----------|-------|-------------|---------------|---------|
| **Unit тесты** | Любая | Только моки | N/A | `test` |
| **Локальная интеграция** | Локальная разработка | Testcontainers MySQL | `LocalIntegrationTest` | `integration-test` |
| **CI интеграция** | GitHub Actions | Docker Compose MySQL | `AbstractIntegrationTest` | `ci-integration` |

---

## Стратегия интеграционного тестирования

### Локальная разработка
```java
// Для локальной разработки - автоматические Testcontainers
@ActiveProfiles("integration-test")
@Testcontainers
class NewsServiceTest extends LocalIntegrationTest {
    // MySQL контейнер автоматически запускается/останавливается
    // Не требуется настройка Docker Compose
}
```

### CI среда
```java
// Для CI - использует внешний Docker Compose MySQL
@ActiveProfiles("ci-integration") 
class NewsServiceTest extends AbstractIntegrationTest {
    // Использует общий сервис MySQL из Docker Compose
    // Более быстрое выполнение, нет накладных расходов на запуск контейнера
}
```

---

## Конфигурация среды

### Конфигурация задач Gradle
```bash
# Локальная разработка
./gradlew integrationTest
# Использует LocalIntegrationTest с Testcontainers

# CI среда  
./gradlew integrationTest -Pci
# Использует AbstractIntegrationTest с Docker Compose MySQL
```

### Логика выбора профиля
- **По умолчанию**: профиль `integration-test` → Testcontainers
- **Параметр CI**: `-Pci` → профиль `ci-integration` → Docker Compose
- **Автоматически**: Тесты наследуют соответствующий базовый класс в зависимости от среды

---

## Преимущества гибридного подхода

### Преимущества локальной разработки
- **Нулевая настройка**: Docker Compose не требуется
- **Изоляция**: Каждый запуск теста получает свежую базу данных
- **Отладка**: Легко отлаживать с логами контейнера
- **Гибкость**: Разные версии MySQL для каждого теста при необходимости

### Преимущества CI среды  
- **Скорость**: Переиспользует общий сервис MySQL
- **Эффективность ресурсов**: Нет накладных расходов на запуск контейнера
- **Надежность**: Консистентность с продакшн развертыванием
- **Простота**: Единый сервис MySQL для всех тестов

---

## Руководство по миграции

### Со старого подхода
```java
// СТАРЫЙ: Единый AbstractIntegrationTest со сложной логикой
@Testcontainers
class MyTest extends AbstractIntegrationTest {
    // Сложная условная логика Testcontainers
}
```

### К новому подходу
```java
// НОВЫЙ: Выберите подходящий базовый класс
class MyTest extends LocalIntegrationTest {     // Для локальной разработки
class MyTest extends AbstractIntegrationTest { // Для CI среды
```

---

## Лучшие практики

1. **Используйте LocalIntegrationTest** для локальной разработки и отладки
2. **Используйте AbstractIntegrationTest** для CI-совместимых тестов
3. **Держите unit тесты быстрыми** только с моками
4. **Тестируйте миграции базы данных** в интеграционных тестах
5. **Проверяйте соответствие продакшену** с реальным MySQL во всех интеграционных тестах

---

## Устранение неполадок

### Частые проблемы
- **Testcontainers не запускается**: Проверьте, что Docker daemon запущен локально
- **CI тесты падают**: Убедитесь, что Docker Compose MySQL здоров перед тестами
- **Конфликты профилей**: Проверьте правильную комбинацию базового класса и профиля

### Команды для отладки
```bash
# Проверить статус CI MySQL
docker compose ps phoebe-mysql

# Запустить конкретный тест локально
./gradlew integrationTest --tests "NewsServiceTest"

# Запустить с CI профилем локально
./gradlew integrationTest -Pci --tests "NewsServiceTest"
```