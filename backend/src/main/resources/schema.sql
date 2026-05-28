-- =============================================
-- Alby: Database Schema MVP
-- Создание всех таблиц для первого этапа
-- =============================================

-- 1. Пользователи
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    login VARCHAR(50) UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,          -- хранить ТОЛЬКО хеш (BCrypt)
    name VARCHAR(100),
    created_at TIMESTAMPTZ DEFAULT now()
);

-- 2. Справочник интересов (готовый список)
CREATE TABLE IF NOT EXISTS interests (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL
);

-- 3. Интересы пользователя с весом (0–10)
CREATE TABLE IF NOT EXISTS user_interests (
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    interest_id INT REFERENCES interests(id) ON DELETE CASCADE,
    weight INT CHECK (weight BETWEEN 0 AND 10) NOT NULL,
    PRIMARY KEY (user_id, interest_id)
);

-- 4. Чаты (комнаты)
CREATE TABLE IF NOT EXISTS chats (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMPTZ DEFAULT now()
);

-- 5. Участники чатов (many-to-many)
CREATE TABLE IF NOT EXISTS chat_participants (
    chat_id UUID REFERENCES chats(id) ON DELETE CASCADE,
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    joined_at TIMESTAMPTZ DEFAULT now(),
    PRIMARY KEY (chat_id, user_id)
);

-- 6. Сообщения в чатах
CREATE TABLE IF NOT EXISTS messages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    chat_id UUID REFERENCES chats(id) ON DELETE CASCADE,
    sender_id UUID REFERENCES users(id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    sent_at TIMESTAMPTZ DEFAULT now()
);

-- 7. (Опционально) Мэтчи – сохранённые пары
CREATE TABLE IF NOT EXISTS matches (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user1_id UUID REFERENCES users(id) ON DELETE CASCADE,
    user2_id UUID REFERENCES users(id) ON DELETE CASCADE,
    score DECIMAL(5,2),                  -- процент совместимости
    matched_at TIMESTAMPTZ DEFAULT now(),
    UNIQUE (user1_id, user2_id)
);

-- Индексы для ускорения самых частых запросов
CREATE INDEX IF NOT EXISTS idx_user_interests_user ON user_interests(user_id);
CREATE INDEX IF NOT EXISTS idx_user_interests_interest ON user_interests(interest_id);
CREATE INDEX IF NOT EXISTS idx_messages_chat ON messages(chat_id, sent_at);
CREATE INDEX IF NOT EXISTS idx_chat_participants_user ON chat_participants(user_id);