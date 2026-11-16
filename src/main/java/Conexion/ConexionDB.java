package Conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {
    private static final String URL = "jdbc:mysql://localhost:3306/temuflix";
    private static final String USUARIO = "root";
    private static final String PASSWORD = "1234";
    /*
    private static final String URL = "jdbc:mysql://172.16.1.64:3306/temuflix";
    private static final String USUARIO = "bdd1";
    private static final String PASSWORD = "bdd1";
     */

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection (URL, USUARIO, PASSWORD);
    }
}