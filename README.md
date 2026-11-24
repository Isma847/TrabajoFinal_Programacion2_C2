<h1>Temuflix: Catálogo de contenidos</h1>
<h2>Introducción</h2>
<h3>Integrantes del grupo</h3>
<p>Los encargados del desarrollo de este programa fueron; Ismael Salvador Castillo, Marco Uriel 
Villaroel Buet, Dylan Nicolás Barboza y Joaquin Nemet.</p>
<p> Temuflix es un catálogo de contenidos audiovisuales, como lo son
series, peliculas y videojuegos que permite a sus usuarios crear reseñas
y agregar a favoritos los contenidos que desee.</p>
<p> El programa cuenta con dos tipos de cuenta, por un lado, la cuenta corriente, el usuario,
pueda consultar el catálogo de series, peliculas y videojuegos, crear una reseña con calificación
y comentario para los mismos, así como agregarlos a una lista de favoritos. Por otro lado,
el administrador puede agregar, modificar y dar de baja dichos contenidos mediante un menú aparte,
además de las funciones de un usuario corriente.</p>
<p>El programa posee dos interfaces de ejecución, por un lado, la interfaz por consola que posee algunas
todas las funcionalidades planteadas del programa, y, por otro lado, la interfaz gráfica creada con JavaFX
que ofrece una experiencia más accesible para el usuario. La interfaz gráfica no posee todas las funciones
planteadas para el administrador.</p>

<h2>Ejecutar el programa</h2>
<p> Para ejecutar el programa se deben realizar ciertos preparativos previos:</p>
<ol>
    <li>
        <p>Descargar el proyecto e iniciar una conexión en Workbench.</p>
    </li>
    <li>
        <p>Acceder a los archivos del proyecto y modificar el archivo <i>ConexionDB</i> en la ruta:</p>
        <code>ProyectoFinal_C2/src/main/java/Conexion/ConexionDB</code>
        <p>Dentro de ese archivo se debe modificar las variables 'USUARIO' y 'PASSWORD' para que
            coincidan con su usuario y contraseña de su conexión de Workbench.
        </p>
    </li>
    <li>
    <p>En workbench, se deben abrir y ejecutar los archivos 'Script.sql' e 'Inserts.sql' en dicho orden
        para crear la base de datos e insertar los datos de prueba. Estos archivos se encuentran en:
        <code>ProyectoFinal_C2/BDD</code>
    </p>
    </li>
    <li>
        <p>Por último, dentro de IntelliJ, se debe abrir el proyecto y en el caso de querer entrar al
            programa por consola se debe abrir el archivo main.java y presionar ejecutar arriba a la derecha.
        </p>
        <p>En el caso de querer ejecutar la interfaz gráfica se debe dirigir a la barra lateral
            a la derecha de la pantalla presionar en el apartado de Maven (Simbolizado con una 'm'),
            y luego presionar navegar en ProyectoFinal -> Plugins -> javafx -> javafx:run y ahí
            comenzará a cargar la interfaz.
        </p>
    </li>
</ol>
<p>Para ingresar al sistema, puede utilizar estas credenciales de prueba:</p>
<ul>
    <i>Credenciales para administrador</i>
    <li><b>EMAIL: </b> admin@temuflix.com</li>
    <li><b>CONTRASEÑA: </b> admin1234</li>
    <br>
    <i>Credenciales para administrador</i>
    <li><b>EMAIL: </b> usuario@mail.com</li>
    <li><b>CONTRASEÑA: </b> clave1234</li>
</ul>