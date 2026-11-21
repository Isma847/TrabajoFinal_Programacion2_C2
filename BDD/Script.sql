-- Temuflix: creación completa desde cero y datos de prueba para MySQL 8.
-- ATENCIÓN: al ejecutarse elimina la base temuflix y todos sus datos anteriores.
-- Cuentas unificadas en usuarios. El rol distingue USUARIO y ADMINISTRADOR.
-- puedeEliminar representa el permiso de borrado físico; todas las cuentas lo tienen en FALSE salvo admin.
-- Los administradores de prueba tienen edad NULL; los usuarios comunes conservan su edad.
-- Credenciales demo: admin / admin1234; valentinaperez / clave1234.
SET NAMES utf8mb4;

DROP DATABASE IF EXISTS temuflix;
CREATE DATABASE temuflix CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE temuflix;

CREATE TABLE usuarios (
                          id_usuario INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                          nombre VARCHAR(80) NOT NULL,
                          apellido VARCHAR(80) NOT NULL,
                          username VARCHAR(60) NOT NULL,
                          contrasena VARCHAR(30) NOT NULL,
                          email VARCHAR(160) NOT NULL,
                          edad INT NULL,
                          rol ENUM('USUARIO', 'ADMINISTRADOR') NOT NULL DEFAULT 'USUARIO',
                          puedeEliminar BOOLEAN NOT NULL DEFAULT FALSE,
                          baja BOOLEAN NOT NULL DEFAULT FALSE,
                          CONSTRAINT uq_usuarios_username UNIQUE (username),
                          CONSTRAINT uq_usuarios_email UNIQUE (email),
                          CONSTRAINT ck_usuarios_edad CHECK (rol = 'ADMINISTRADOR' OR edad IS NOT NULL),
                          CONSTRAINT ck_usuarios_permiso CHECK (rol = 'ADMINISTRADOR' OR puedeEliminar = FALSE)
) ENGINE=InnoDB;

CREATE TABLE directores (
                            id_director INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                            nombre VARCHAR(80) NOT NULL,
                            apellido VARCHAR(80) NOT NULL,
                            descripcion TEXT
) ENGINE=InnoDB;

CREATE TABLE peliculas (
                           id_pelicula INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                           nombre VARCHAR(100) NOT NULL,
                           duracionMinutos INT NOT NULL,
                           descripcion VARCHAR(600),
                           clasificacion VARCHAR(30) NOT NULL,
                           baja BOOLEAN NOT NULL DEFAULT FALSE,

                           id_director INT,
                           genero VARCHAR(30) NOT NULL,

                           fechaEstreno VARCHAR(10) NOT NULL,
                           CONSTRAINT fk_director_peliculas FOREIGN KEY (id_director) REFERENCES directores(id_director) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE series (
                        id_serie INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                        nombre VARCHAR(100) NOT NULL,
                        duracionMinutos INT NOT NULL,
                        descripcion VARCHAR(600),
                        clasificacion VARCHAR(30) NOT NULL,
                        baja BOOLEAN NOT NULL DEFAULT FALSE,

                        id_director INT,
                        genero VARCHAR(30) NOT NULL,

                        cantidadCapitulos INT NOT NULL,
                        cantidadTemporadas INT NOT NULL,
                        CONSTRAINT fk_director_series FOREIGN KEY (id_director) REFERENCES directores(id_director) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE videojuegos (
                             id_videojuego INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                             nombre VARCHAR(100) NOT NULL,
                             duracionMinutos INT NOT NULL,
                             descripcion VARCHAR(600),
                             clasificacion VARCHAR(30) NOT NULL,
                             baja BOOLEAN NOT NULL DEFAULT FALSE,

                             genero VARCHAR(30) NOT NULL,
                             dificultad INT NOT NULL
) ENGINE=InnoDB;

CREATE TABLE resenas (
                         id_resena INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    -- id_contenido es un campo polimorfico para simplificar
    -- pero en un programa real deberían usarse varias tablas con FK
                         id_contenido INT NOT NULL,
                         id_usuario INT NOT NULL,
                         tipo VARCHAR(10) NOT NULL,
                         calificacion INT NOT NULL,
                         comentario TEXT,
                         fecha VARCHAR(10) NOT NULL,
                         constraint fk_usuario_resena FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
                         constraint ck_tipo_resena CHECK (tipo IN ('SERIE', 'JUEGO', 'PELICULA'))
) ENGINE=InnoDB;

CREATE TABLE favoritos (
                           id_favorito INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                           id_usuario INT NOT NULL,
                           tipo VARCHAR(10) NOT NULL,
    -- id_contenido es un campo polimorfico para simplificar
    -- pero en un programa real deberían usarse varias tablas con FK
                           id_contenido INT NOT NULL,
                           CONSTRAINT uq_favoritos_usuario_contenido UNIQUE (id_usuario, tipo, id_contenido),
                           constraint fk_usuario_fav FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
                           constraint ck_tipo_fav CHECK (tipo IN ('SERIE', 'JUEGO', 'PELICULA'))
) ENGINE=InnoDB;

CREATE TABLE historial (
                           id_historial INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                           id_usuario INT NOT NULL,
                           fecha VARCHAR(10) NOT NULL,
    -- El numero despues de la coma se utiliza como minutos por si se ven en una misma hora
                           tiempo DOUBLE NOT NULL,
                           tipo VARCHAR(10) NOT NULL,
    -- id_contenido es un campo polimorfico para simplificar
    -- pero en un programa real deberían usarse varias tablas pivot con FK
                           id_contenido INT NOT NULL,
                           constraint fk_usuario_hist FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
                           constraint ck_tipo_hist CHECK (tipo IN ('SERIE', 'JUEGO', 'PELICULA'))
) ENGINE=InnoDB;

CREATE TABLE progresos (
                           id_progreso INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                           id_usuario INT NOT NULL,
                           id_videojuego INT NOT NULL,
                           estado VARCHAR(12) NOT NULL,
                           CONSTRAINT uq_progresos_usuario_juego UNIQUE (id_usuario, id_videojuego),
                           CONSTRAINT fk_progresos_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
                           CONSTRAINT fk_progresos_videojuego FOREIGN KEY (id_videojuego) REFERENCES videojuegos(id_videojuego) ON DELETE CASCADE,
                           CONSTRAINT ck_progresos_estado CHECK (estado IN ('PENDIENTE','JUGANDO','COMPLETADO'))
) ENGINE=InnoDB;
