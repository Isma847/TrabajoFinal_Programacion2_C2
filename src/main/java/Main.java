import DAO.FavoritoDao;
import Service.FavoritoService;
import Service.DirectorService;
import Service.GestionCuenta;
import Service.PeliculaService;
import Service.ResenaService;
import Service.SerieService;
import Service.UsuarioService;
import Service.VideojuegoService;
import UI.MenuFavoritos;
import UI.MenuPeliculas;
import UI.MenuResenas;
import UI.MenuSeries;
import UI.MenuVideojuegos;
import UI.Sistema;

import java.sql.SQLException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        try (Scanner entrada = new Scanner(System.in)) {
            UsuarioService usuarios = new UsuarioService();
            GestionCuenta gestionCuenta = new GestionCuenta(usuarios);

            DirectorService directores = new DirectorService();
            PeliculaService peliculas = new PeliculaService();
            SerieService series = new SerieService();
            VideojuegoService videojuegos = new VideojuegoService();
            ResenaService resenas = new ResenaService();
            FavoritoService favoritos = new FavoritoService(new FavoritoDao());

            MenuPeliculas menuPeliculas = new MenuPeliculas(peliculas, directores, entrada);
            MenuSeries menuSeries = new MenuSeries(series, directores, entrada);
            MenuVideojuegos menuVideojuegos = new MenuVideojuegos(videojuegos, entrada);

            MenuResenas menuResenas = new MenuResenas(resenas, peliculas, series, videojuegos, entrada);
            MenuFavoritos menuFavoritos = new MenuFavoritos(favoritos, peliculas, series, videojuegos,
                    usuarios, entrada);
            Sistema sistema = new Sistema(gestionCuenta, entrada, menuPeliculas, menuSeries,
                    menuVideojuegos, menuResenas, menuFavoritos);
            sistema.mostrarMenuInicio();
        } catch (SQLException e) {
            System.err.println("No se pudo iniciar Temuflix. " + e.getMessage());
        }
    }
}
