-- changeset author:add-last-login:2
-- comment: Добавление столбца last_login в таблицу users
ALTER TABLE users ADD COLUMN last_login TIMESTAMP;