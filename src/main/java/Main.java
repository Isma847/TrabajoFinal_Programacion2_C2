import Service.AdministradorService;
import Service.DirectorService;
import Service.GestionCuenta;
import Service.PeliculaService;
import Service.UsuarioService;
import UI.MenuPeliculas;
import UI.Sistema;

import java.sql.SQLException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        try (Scanner entrada = new Scanner(System.in)) {
            UsuarioService usuarios = new UsuarioService();
            AdministradorService administradores = new AdministradorService();
            GestionCuenta gestionCuenta = new GestionCuenta(usuarios, administradores);

            PeliculaService peliculas = new PeliculaService();
            DirectorService directores = new DirectorService();
            MenuPeliculas menuPeliculas = new MenuPeliculas(peliculas, directores, entrada);

            Sistema sistema = new Sistema(gestionCuenta, entrada, menuPeliculas);
            sistema.mostrarMenuInicio();
        } catch (SQLException e) {
            System.err.println("No se pudo iniciar Temuflix. " + e.getMessage());
        }
    }
}