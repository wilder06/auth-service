CREATE TABLE "usuarios"
(
    id_user      UUID PRIMARY KEY DEFAULT gen_random_uuid(), -- Identificador único con UUID
    name         VARCHAR(100)        NOT NULL,               -- Nombre del usuario
    last_name    VARCHAR(100)        NOT NULL,               -- Apellido del usuario
    document_number VARCHAR(20)      UNIQUE NOT NULL,         -- Numero Documento
    birth_date   DATE                NOT NULL,               -- Fecha de nacimiento
    address      VARCHAR(255),                               -- Dirección
    phone_number VARCHAR(20),                                -- Número de teléfono (string por códigos internacionales)
    email        VARCHAR(150) UNIQUE NOT NULL,               -- Email único
    base_salary  NUMERIC(15, 2) CHECK (base_salary >= 0)    -- Salario base, no negativo
);
-- ==========================================
-- ==========================================
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ==========================================
-- CREACIÓN DE TABLA ROLE
-- ==========================================
CREATE TABLE role
(
    id          BIGSERIAL PRIMARY KEY,        -- Identificador único autoincremental
    name        VARCHAR(100) NOT NULL UNIQUE, -- Nombre único del rol
    description TEXT                          -- Descripción opcional del rol
);

-- ==========================================
-- CREACIÓN DE TABLA USER
-- ==========================================
CREATE TABLE "users"
(
    id_user      UUID PRIMARY KEY DEFAULT gen_random_uuid(), -- Identificador único con UUID
    name         VARCHAR(100)        NOT NULL,               -- Nombre del usuario
    last_name    VARCHAR(100)        NOT NULL,               -- Apellido del usuario
    birth_date   DATE                NOT NULL,               -- Fecha de nacimiento
    address      VARCHAR(255),                               -- Dirección
    phone_number VARCHAR(20),                                -- Número de teléfono (string por códigos internacionales)
    email        VARCHAR(150) UNIQUE NOT NULL,               -- Email único
    base_salary  NUMERIC(15, 2) CHECK (base_salary >= 0),    -- Salario base, no negativo
    id_role      BIGINT              NOT NULL,               -- FK hacia role

    CONSTRAINT fk_user_role
        FOREIGN KEY (id_role) REFERENCES role (id)
            ON DELETE RESTRICT
);

-- ==========================================
-- ÍNDICES ADICIONALES
-- ==========================================
-- Para búsquedas rápidas por email
CREATE INDEX idx_user_email ON "users" (email);

-- Para búsquedas rápidas por rol
CREATE INDEX idx_user_role ON "users" (id_role);

-- Para búsquedas rápidas por nombre de rol
CREATE INDEX idx_role_name ON role (name);


--drop database user_db; elimina la bd
-- truncate table users; elimina la tabla
--SELECT datname FROM pg_database; lista todas las bd