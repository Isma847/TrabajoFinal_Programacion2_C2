package Conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/// Para correr el archivo correctamente se debe modificar el USUARIO y PASSWORD según
/// los que fueron definidos para la conexión de Workbench.

public class ConexionDB {
    private static final String URL = "jdbc:mysql://localhost:3306/temuflix";
    private static final String USUARIO = "root";
    private static final String PASSWORD = "1234";

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection (URL, USUARIO, PASSWORD);
    }
}