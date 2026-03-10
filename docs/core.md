## Core service

# 1️⃣ API Gateway

- **Маршрутизатор** всех запросов от клиента.
- Forward запроса на нужный сервис (Auth / Metadata / Streaming)
- Выдаёт публичный ключ для валидации JWT внутри других сервисов


---

# 2️⃣ Auth Service

- **Генерация JWT**
- Хранение пользователей (userId, email, хэш пароля, настройки и т.д.)
- Работает с **реляционной БД**.
- Проверка прав доступа для API

---

# 3️⃣ Metadata Service

- **Хранит сведения о треке**:
    - trackId, title, artist, duration
    - userId (владелец)
    - storageKey / MiniO URL (куда фактически хранится mp3)
- Работает с **реляционной БД**.
- **Как получать обновления о новых треках**:
    - через Kafka / RabbitMQ / очередь сообщений:
        - Streaming Service после загрузки трека кладёт сообщение “Новый трек загружен: userId + storageKey + metadata”
        - Metadata Service получает сообщение и сохраняет в БД

---

# 4️⃣ Streaming Service

- **Отвечает за**:
    1. Воспроизведение треков (streaming с поддержкой Range)
    2. Загрузку треков в Object Storage (MiniO)
    3. Удаление треков из Object Storage
- **Отправка сообщений в Metadata Service через Kafka** после загрузки или удаления трека
- **Streaming Service** не хранит метаданные пользователей — только **файлы и потоковое воспроизведение**.

---

# 5️⃣ MiniO (Object Storage)

- Хранит mp3, wav и другие файлы
- Metadata Service хранит только **ссылки / storageKey**, а файлы реально лежат в MiniO
- MiniO поддерживает API S3 → Streaming Service работает с ним по API

---

# 6️⃣ Поток при загрузке трека

1. Клиент → API Gateway → Streaming Service (POST /upload)
2. Streaming Service → MiniO (сохраняет файл)
3. Streaming Service → Kafka → Metadata Service
4. Metadata Service → сохраняет запись в БД

---

# 7️⃣ Поток при воспроизведении трека

1. Клиент → API Gateway → Streaming Service (GET /track/:id/stream)
2. Streaming Service → MiniO (Range read)
3. Поток идёт клиенту
