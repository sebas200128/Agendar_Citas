
-- 1. CREAR Y USAR LA BASE DE DATOS
CREATE DATABASE IF NOT EXISTS gestion_citas;
USE clinica_db;

-- --------------------------------------------------------

-- 2. TABLA USUARIOS (Para el Login)
CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario VARCHAR(50) NOT NULL,
    password VARCHAR(50) NOT NULL
);

-- Insertamos el usuario administrador por defecto
INSERT INTO usuarios (usuario, password) VALUES ('admin', '1234');

-- --------------------------------------------------------

-- 3. TABLA PACIENTE (Para Registro y Edición)
CREATE TABLE IF NOT EXISTS paciente (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100),
    dni VARCHAR(20),
    genero VARCHAR(20),
    fecha_nacimiento DATE
);

-- --------------------------------------------------------

-- 4. TABLA CITAS (Con Fecha y Hora)
CREATE TABLE IF NOT EXISTS citas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_paciente INT NOT NULL,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    
    -- Relación con la tabla paciente
    -- ON DELETE CASCADE: Si borras un paciente, se borran sus citas automáticamente
    FOREIGN KEY (id_paciente) REFERENCES paciente(id) ON DELETE CASCADE
);