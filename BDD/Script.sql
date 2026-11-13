-- Esquema exclusivo del catálogo. No borra ni vacía tablas existentes.
-- La aplicación inicializa la base elegida antes de ejecutar este archivo.

CREATE DATABASE IF NOT EXISTS temuflix;
USE temuflix;

CREATE TABLE IF NOT EXISTS administradores (
	id_administrador INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	nombre VARCHAR(80) NOT NULL,
    apellido VARCHAR(80) NOT NULL,
    username VARCHAR(60) NOT NULL,
    contrasena VARCHAR(512) NOT NULL,
    email VARCHAR(160) NOT NULL,
    nivelAcceso INT NOT NULL
);

CREATE TABLE IF NOT EXISTS usuarios (
	id_usuario INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(80) NOT NULL,
    apellido VARCHAR(80) NOT NULL,
    username VARCHAR(60) NOT NULL,
    contrasena VARCHAR(512) NOT NULL,
    email VARCHAR(160) NOT NULL,
    edad INT NOT NULL
);

CREATE TABLE IF NOT EXISTS directores (
    id_director INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(80) NOT NULL,
    apellido VARCHAR(80) NOT NULL,
    descripcion TEXT
);

CREATE TABLE IF NOT EXISTS series (
	id_serie INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    duracionMinutos INT NOT NULL,
    clasificacion VARCHAR(30) NOT NULL,
    baja BOOLEAN NOT NULL DEFAULT FALSE,
    
    id_director INT NOT NULL,
    genero VARCHAR(30) NOT NULL,
    
    cantidadCapitulos INT NOT NULL,
    cantTemporadas INT NOT NULL,
    
    CONSTRAINT fk_director_series FOREIGN KEY (id_director) REFERENCES directores(id_director)
);

CREATE TABLE IF NOT EXISTS peliculas (
	id_pelicula INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    duracionMinutos INT NOT NULL,
    clasificacion VARCHAR(30) NOT NULL,
    baja BOOLEAN NOT NULL DEFAULT FALSE,
    
    id_director INT NOT NULL,
    genero VARCHAR(30) NOT NULL,
    
    fechaEstreno VARCHAR(10) NOT NULL,
    
    CONSTRAINT fk_director_peliculas FOREIGN KEY (id_director) REFERENCES directores(id_director)
);

CREATE TABLE IF NOT EXISTS videojuegos (
	id_videojuego INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    duracionMinutos INT NOT NULL,
    clasificacion VARCHAR(30) NOT NULL,
    baja BOOLEAN NOT NULL DEFAULT FALSE,

    genero VARCHAR(30) NOT NULL,
    dificultad INT NOT NULL
);

CREATE TABLE IF NOT EXISTS resenas (
	id_resena INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    calificacion INT NOT NULL,
    comentario TEXT,
    fecha VARCHAR(10) NOT NULL
);

CREATE TABLE IF NOT EXISTS favoritos (
	id_favorito INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    tipo VARCHAR(10) NOT NULL,
    -- id_contenido es un campo polimorfico para simplificar
    -- pero en un programa real deberían usarse varias tablas con FK
    id_contenido INT NOT NULL,
    constraint fk_usuario_fav FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario),
    constraint ck_tipo_fav CHECK (tipo IN ('SERIE', 'JUEGO', 'PELICULA'))
);

CREATE TABLE IF NOT EXISTS historial (
	id_historial INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    fecha VARCHAR(10) NOT NULL,
    -- El numero despues de la coma se utiliza como minutos por si se ven en una misma hora
    tiempo DOUBLE NOT NULL,
    tipo VARCHAR(10) NOT NULL,
    -- id_contenido es un campo polimorfico para simplificar
    -- pero en un programa real deberían usarse varias tablas pivot con FK
    id_contenido INT NOT NULL,
    constraint fk_usuario_hist FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario),
    constraint ck_tipo_hist CHECK (tipo IN ('SERIE', 'JUEGO', 'PELICULA'))
);

CREATE TABLE IF NOT EXISTS progresos (
    id_progreso INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    id_videojuego INT NOT NULL,
    estado VARCHAR(12) NOT NULL,
    CONSTRAINT uq_progresos_usuario_juego UNIQUE (id_usuario, id_videojuego),
    CONSTRAINT fk_progresos_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario),
    CONSTRAINT fk_progresos_videojuego FOREIGN KEY (id_videojuego) REFERENCES videojuegos(id_videojuego),
    CONSTRAINT ck_progresos_estado CHECK (estado IN ('PENDIENTE','JUGANDO','COMPLETADO'))
);
