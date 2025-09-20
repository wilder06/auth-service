-- ==========================================
-- CREACIÓN DE TABLA ROLE
-- ==========================================
CREATE TABLE IF NOT EXISTS role (
    id_role BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);

-- ==========================================
-- CREACIÓN DE TABLA USERS
-- ==========================================
CREATE TABLE IF NOT EXISTS "users" (
    id_user UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    birth_date DATE,
    address VARCHAR(255),
    phone_number VARCHAR(20),
    document_number VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    base_salary NUMERIC(15,2) CHECK (base_salary >= 0),
    id_role BIGINT NOT NULL,
    CONSTRAINT fk_user_role FOREIGN KEY (id_role) REFERENCES role(id_role) ON DELETE RESTRICT
);

-- ==========================================
-- ÍNDICES ADICIONALES
-- ==========================================
CREATE INDEX IF NOT EXISTS idx_user_role ON "users" (id_role);
CREATE INDEX IF NOT EXISTS idx_role_name ON role (name);
CREATE INDEX IF NOT EXISTS idx_user_document_number ON "users" (document_number);

-- ==========================================
-- INSERTS INICIALES
-- ==========================================
INSERT INTO role (name, description) VALUES 
('ADMIN', 'Administrator role with full access'),
('USER', 'Regular user role'),
('ADVISOR', 'Loan advisor role')
ON CONFLICT (name) DO NOTHING;

INSERT INTO "users" (name, last_name, email, document_number, password, id_role) VALUES
                                                                                     ('Admin', 'User', 'admin@email.com', '12345679', '$2a$10$CcUH7Zn5KUTTnc8PV0KUJOWfiHLFtRFxRIZQFzU4ZLqQ6zmEWKm.2', 1),
                                                                                     ('John', 'Doe', 'user@email.com', '12345678', '$2a$10$CcUH7Zn5KUTTnc8PV0KUJOWfiHLFtRFxRIZQFzU4ZLqQ6zmEWKm.2', 2),
                                                                                     ('Jane', 'Smith', 'advisor@email.com', '12345677', '$2a$10$CcUH7Zn5KUTTnc8PV0KUJOWfiHLFtRFxRIZQFzU4ZLqQ6zmEWKm.2', 3)
    ON CONFLICT (email) DO NOTHING;

