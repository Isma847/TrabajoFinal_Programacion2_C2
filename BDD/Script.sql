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

CREATE TABLE IF NOT EXISTS series (
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
);

CREATE TABLE IF NOT EXISTS peliculas (
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
);

CREATE TABLE IF NOT EXISTS videojuegos (
    id_videojuego INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    duracionMinutos INT NOT NULL,
    descripcion VARCHAR(600),
    clasificacion VARCHAR(30) NOT NULL,
    baja BOOLEAN NOT NULL DEFAULT FALSE,

    genero VARCHAR(30) NOT NULL,
    dificultad INT NOT NULL
);

CREATE TABLE IF NOT EXISTS contenidoMultimedia (
    id_contenidoMultimedia INT AUTO_INCREMENT PRIMARY KEY,
    tipo VARCHAR(10) NOT NULL,
    id_pelicula INT NULL UNIQUE,
    id_serie INT NULL UNIQUE,
    id_videojuego INT NULL UNIQUE,
    CONSTRAINT fk_contenido_pelicula FOREIGN KEY (id_pelicula) REFERENCES peliculas(id_pelicula) ON DELETE CASCADE,
    CONSTRAINT fk_contenido_serie FOREIGN KEY (id_serie) REFERENCES series(id_serie) ON DELETE CASCADE,
    CONSTRAINT fk_contenido_videojuego FOREIGN KEY (id_videojuego) REFERENCES videojuegos(id_videojuego) ON DELETE CASCADE,
    -- ck_contenido_arco es para asegurar de que no se le asigne más de un id al contenido
    CONSTRAINT ck_contenido_arco CHECK (
        (tipo = 'PELICULA' AND id_pelicula IS NOT NULL AND id_serie IS NULL AND id_videojuego IS NULL)
        OR (tipo = 'SERIE' AND id_serie IS NOT NULL AND id_pelicula IS NULL AND id_videojuego IS NULL)
        OR (tipo = 'JUEGO' AND id_videojuego IS NOT NULL AND id_pelicula IS NULL AND id_serie IS NULL))
);

CREATE TABLE IF NOT EXISTS resenas (
    id_resena INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    id_contenido INT NOT NULL,
    id_usuario INT NOT NULL,
    calificacion INT NOT NULL,
    comentario TEXT,
    fecha VARCHAR(10) NOT NULL,
    constraint fk_usuario_resena FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    constraint fk_contenido_resena FOREIGN KEY (id_contenido) REFERENCES contenidoMultimedia(id_contenidoMultimedia) ON DELETE CASCADE,
    CONSTRAINT uq_resenas_usuario_contenido UNIQUE (id_usuario, id_contenido)
) ENGINE=InnoDB;

CREATE TABLE favoritos (
    id_favorito INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    id_contenido INT NOT NULL,
    CONSTRAINT uq_favoritos_usuario_contenido UNIQUE (id_usuario, id_contenido),
    constraint fk_usuario_fav FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    constraint fk_contenido_fav FOREIGN KEY (id_contenido) REFERENCES contenidoMultimedia(id_contenidoMultimedia) ON DELETE CASCADE
) ENGINE=InnoDB;
