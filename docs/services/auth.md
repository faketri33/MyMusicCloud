# Auth Service

Микросервис аутентификации, авторизации и регистрации пользователей на основе JWT токенов (RS256).

---

## Содержание

- [Описание](#описание)
- [Технологии](#технологии)
- [API](#api)
- [Безопасность](#безопасность)
- [Конфигурация](#конфигурация)
- [Запуск](#запуск)
- [Тесты](#тесты)

---

## Описание

Auth Service отвечает за:
- Регистрацию новых пользователей
- Аутентификацию и выдачу JWT токенов (access + refresh)
- Валидацию токенов для внутренних сервисов
- Предоставление публичных ключей для верификации токенов

Сервис является частью микросервисной архитектуры. Снаружи доступен только через API Gateway. Прямой доступ к сервису из интернета закрыт на сетевом уровне (Docker network).

---

## Технологии

| Технология | Версия | Назначение |
|---|---|---|
| Java | 21 | Язык |
| Spring Boot | 4.0.3 | Фреймворк |
| Spring Security | 7.0.3 | Безопасность |
| nimbus-jose-jwt | 10.7 | JWT (RS256) |
| PostgreSQL | 17 | База данных |
| Hibernate | 7.2.4 | ORM |
| Testcontainers | 2.0.3 | Интеграционные тесты |

---

## API

### Публичные эндпоинты

#### `POST /api/v1/auth/register`

Регистрация нового пользователя.

**Request:**
```json
{
  "login": "username",
  "password": "password123"
}
```

**Response `200 OK`:**
```json
{
  "access": "eyJhbGciOiJSUzI1NiJ9...",
  "refresh": "eyJhbGciOiJSUzI1NiJ9..."
}
```

**Ошибки:**

| Код | Причина |
|---|---|
| `409 Conflict` | Пользователь с таким логином уже существует |
| `400 Bad Request` | Некорректное тело запроса |

---

#### `POST /api/v1/auth/login`

Аутентификация существующего пользователя.

**Request:**
```json
{
  "login": "username",
  "password": "password123"
}
```

**Response `200 OK`:**
```json
{
  "access": "eyJhbGciOiJSUzI1NiJ9...",
  "refresh": "eyJhbGciOiJSUzI1NiJ9..."
}
```

**Ошибки:**

| Код | Причина |
|---|---|
| `401 Unauthorized` | Неверный логин или пароль |
| `400 Bad Request` | Некорректное тело запроса |

---

### Внутренние эндпоинты

Доступны только для сервисов с ролью `ROLE_SERVICE`. Требуют заголовок:

```
Authorization: Bearer <service_jwt>
```

#### `POST /api/internal/validate`

Валидация JWT токена пользователя.

**Request:**
```json
{
  "token": "eyJhbGciOiJSUzI1NiJ9..."
}
```

**Response `200 OK`:**
```json
{
  "valid": true,
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "roles": ["ROLE_USER"]
}
```

---

## Безопасность

### JWT

- Алгоритм подписи: **RS256** (асимметричное шифрование)
- `access` токен: короткоживущий, содержит `userId` (UUID) в `subject` и роли в `claims`
- `refresh` токен: долгоживущий, используется для обновления `access` токена

### Роли

| Роль              | Описание             |
|-------------------|----------------------|
| `ROLE_USER`       | Обычный пользователь |
| `ROLE_SERVICE`    | Внутренний сервис    |
| `ROLE_SUPER_USER` | Администратор        |

Сервисные аккаунты создаются через миграцию БД и используются для межсервисного взаимодействия.

### Сетевая изоляция

```
Internet → API Gateway → [Docker internal network]
                              └── auth-service (порт не проброшен)
```

Эндпоинт `/api/internal/**` дополнительно защищён на уровне Spring Security — требует `ROLE_SERVICE`.

---

## Конфигурация

Сервис конфигурируется через переменные окружения:

| Переменная                 | Описание                         | Пример                           |
|----------------------------|----------------------------------|----------------------------------|
| `AUTH_DATABASE_CONNECT`    | JDBC URL базы данных             | `jdbc:postgresql://db:5432/auth` |
| `AUTH_DATABASE_USER`       | Пользователь БД                  | `auth_user`                      |
| `AUTH_DATABASE_PASSWORD`   | Пароль БД                        | `secret`                         |
| `JWT_KEY_PRIVATE`          | Приватный RSA ключ (PEM)         | `classpath:private.pem`          |
| `JWT_KEY_PUBLIC`           | Публичный RSA ключ (PEM)         | `classpath:public.pem`           |
| `JWT_KEY_PRIVATE_LIFETIME` | Время жизни access токена (сек)  | `3600`                           |
| `JWT_KEY_PUBLIC_LIFETIME`  | Время жизни refresh токена (сек) | `86400`                          |
| `CORS_ALLOWED_ORIGINS`     | Разрешённые origins для CORS     | `https://yourdomain.com`         |


---

## Запуск

### Локально через Docker Compose

```bash
docker-compose up auth-service
```

### Генерация RSA ключей

```bash
# Приватный ключ
openssl genrsa -out private.pem 2048

# Публичный ключ
openssl rsa -in private.pem -pubout -out public.pem
```

---

## Тесты

### Все тесты

```bash
mvn test
```