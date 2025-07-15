--liquibase formatted sql
--changeset springbootapp:2 comment:"Создание таблиц пользователей и ролей"

-- Таблица ролей
CREATE TABLE public.roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Индексы для таблицы ролей
CREATE INDEX idx_roles_name ON public.roles (name);

-- Таблица пользователей
CREATE TABLE public.users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Индексы для таблицы пользователей
CREATE UNIQUE INDEX idx_users_email ON public.users (email);
CREATE UNIQUE INDEX idx_users_username ON public.users (username);

-- Таблица связи пользователей и ролей (many-to-many)
CREATE TABLE public.user_roles (
    user_id BIGINT NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES public.roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- Индексы для таблицы связей
CREATE INDEX idx_user_roles_user_id ON public.user_roles (user_id);
CREATE INDEX idx_user_roles_role_id ON public.user_roles (role_id);

-- Комментарии к таблицам
COMMENT ON TABLE public.roles IS 'Роли пользователей в системе';
COMMENT ON TABLE public.users IS 'Пользователи системы';
COMMENT ON TABLE public.user_roles IS 'Связь между пользователями и их ролями';

-- Комментарии к столбцам
COMMENT ON COLUMN public.roles.name IS 'Уникальное название роли, например ROLE_ADMIN';
COMMENT ON COLUMN public.users.username IS 'Уникальное имя пользователя для входа';
COMMENT ON COLUMN public.users.email IS 'Email пользователя, также уникальный';
COMMENT ON COLUMN public.users.password IS 'Хешированный пароль';
COMMENT ON COLUMN public.users.enabled IS 'Флаг активности пользователя';


--liquibase formatted sql
--changeset springbootapp:3 comment:"Добавление начальных данных для системы"

-- Добавление основных ролей
INSERT INTO public.roles (name, description) VALUES
                                                 ('ROLE_ADMIN', 'Администратор системы'),
                                                 ('ROLE_USER', 'Обычный пользователь'),
                                                 ('ROLE_MANAGER', 'Менеджер проекта');

-- Добавление администратора (пароль: admin123)
-- Хешированный пароль: $2a$10$Xs1skOyZGNlxVEB0VhZyQOA.7.cO0YYqzWTNl2qQhUS9iIwxfwXhq
INSERT INTO public.users (username, email, password, first_name, last_name, enabled)
VALUES ('admin', 'admin@example.com',
        '$2a$10$Xs1skOyZGNlxVEB0VhZyQOA.7.cO0YYqzWTNl2qQhUS9iIwxfwXhq',
        'Администратор', 'Системы', true);

-- Добавление тестового пользователя (пароль: user123)
-- Хешированный пароль: $2a$10$XptfskLsT1l/bRTLRiiCgejHqOpgXFreUnNUa35gJdCr2v2QbVFzu
INSERT INTO public.users (username, email, password, first_name, last_name, enabled)
VALUES ('user', 'user@example.com',
        '$2a$10$XptfskLsT1l/bRTLRiiCgejHqOpgXFreUnNUa35gJdCr2v2QbVFzu',
        'Тестовый', 'Пользователь', true);

-- Назначение ролей пользователям
INSERT INTO public.user_roles (user_id, role_id)
SELECT
    (SELECT id FROM public.users WHERE username = 'admin'),
    (SELECT id FROM public.roles WHERE name = 'ROLE_ADMIN');

INSERT INTO public.user_roles (user_id, role_id)
SELECT
    (SELECT id FROM public.users WHERE username = 'user'),
    (SELECT id FROM public.roles WHERE name = 'ROLE_USER');


--liquibase formatted sql
--changeset springbootapp:4 comment:"Создание таблицы для хранения инсайтов" runOnChange:true dbms:all runAlways:false runInTransaction:true
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'users'

-- Таблица инсайтов
CREATE TABLE public.insights (
                                 id BIGSERIAL PRIMARY KEY,
                                 topic VARCHAR(255) NOT NULL,
                                 content TEXT NOT NULL,
                                 user_id BIGINT NOT NULL,
                                 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                 CONSTRAINT fk_insights_user FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE
);

-- Индексы для таблицы инсайтов
CREATE INDEX idx_insights_topic ON public.insights (topic);
CREATE INDEX idx_insights_user_id ON public.insights (user_id);

-- Комментарии
COMMENT ON TABLE public.insights IS 'Аналитические данные по различным темам';
COMMENT ON COLUMN public.insights.topic IS 'Тема для анализа';
COMMENT ON COLUMN public.insights.content IS 'Текст с аналитикой по теме';
COMMENT ON COLUMN public.insights.user_id IS 'Пользователь, создавший запись';
COMMENT ON COLUMN public.insights.created_at IS 'Дата и время создания';
COMMENT ON COLUMN public.insights.updated_at IS 'Дата и время последнего обновления';

