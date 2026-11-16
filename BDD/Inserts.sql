-- Datos de prueba de Temuflix. Ejecutar después de BDD/Script.sql.
-- El catálogo y los usuarios conservan los IDs que referencia el seed.
USE temuflix;

START TRANSACTION;

INSERT INTO directores (id_director, nombre, apellido, descripcion)
SELECT datos.id_director, datos.nombre, datos.apellido, datos.descripcion
FROM (
         SELECT 1 AS id_director, 'Christopher' AS nombre, 'Nolan' AS apellido, 'Director y guionista británico-estadounidense. Conocido por Inception e Interstellar.' AS descripcion
         UNION ALL SELECT 2, 'Quentin', 'Tarantino', 'Director y guionista estadounidense. Conocido por Pulp Fiction y Kill Bill.'
         UNION ALL SELECT 3, 'Greta', 'Gerwig', 'Directora y guionista estadounidense. Conocida por Lady Bird, Mujercitas y Barbie.'
         UNION ALL SELECT 4, 'Denis', 'Villeneuve', 'Director canadiense. Conocido por La llegada y Dune.'
         UNION ALL SELECT 5, 'Martin', 'Scorsese', 'Director estadounidense. Conocido por Taxi Driver, Buenos muchachos y Los infiltrados.'
         UNION ALL SELECT 6, 'Steven', 'Spielberg', 'Director estadounidense. Conocido por Tiburón, E.T. y Jurassic Park.'
         UNION ALL SELECT 7, 'Hayao', 'Miyazaki', 'Director japonés, cofundador del Studio Ghibli.'
         UNION ALL SELECT 8, 'Joon-ho', 'Bong', 'Director surcoreano. Conocido por Memorias de un asesino y Parásitos.'
         UNION ALL SELECT 9, 'John', 'Lasseter', 'Director estadounidense de animación. Dirigió Toy Story.'
         UNION ALL SELECT 10, 'Peter', 'Jackson', 'Director neozelandés. Dirigió la trilogía de El Señor de los Anillos.'
         UNION ALL SELECT 11, 'James', 'Cameron', 'Director canadiense. Conocido por Terminator, Titanic y Avatar.'
         UNION ALL SELECT 12, 'Ridley', 'Scott', 'Director británico. Conocido por Alien, Blade Runner y Gladiador.'
         UNION ALL SELECT 13, 'Juan José', 'Campanella', 'Director argentino. Conocido por El hijo de la novia y El secreto de sus ojos.'
         UNION ALL SELECT 14, 'Damián', 'Szifron', 'Director y guionista argentino. Conocido por Relatos salvajes.'
         UNION ALL SELECT 15, 'Vince', 'Gilligan', 'Creador de Breaking Bad y Better Call Saul.'
         UNION ALL SELECT 16, 'David', 'Benioff', 'Guionista estadounidense, cocreador de Game of Thrones.'
         UNION ALL SELECT 17, 'Baran', 'bo Odar', 'Director alemán, cocreador de la serie Dark.'