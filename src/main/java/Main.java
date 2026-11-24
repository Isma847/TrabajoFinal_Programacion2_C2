import DAO.ContenidoDao;
import DAO.FavoritoDao;
import Service.ContenidoService;
import Service.DirectorService;
import Service.FavoritoService;
import Service.GestionCuenta;
import Service.PeliculaService;
import Service.ResenaService;
import Service.SerieService;
import Service.UsuarioService;
import Service.VideojuegoService;
import UI.MenuCuentas;
import UI.MenuDirectores;
import UI.MenuFavoritos;
import UI.MenuPeliculas;
import UI.MenuResenas;
import UI.MenuSeries;
import UI.MenuVideojuegos;
import UI.Sistema;
import UI.MenuMiCuenta;

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

            ContenidoService contenidos = new ContenidoService(
                    peliculas,
                    series,
                    videojuegos,
                    new ContenidoDao()
            );

            ResenaService resenas = new ResenaService();
            FavoritoService favoritos = new FavoritoService(
                    new FavoritoDao()
            );

            MenuPeliculas menuPeliculas = new MenuPeliculas(
                    peliculas, directores, entrada
            );

            MenuSeries menuSeries = new MenuSeries(
                    series, directores, entrada
            );

            MenuVideojuegos menuVideojuegos = new MenuVideojuegos(
                    videojuegos, entrada
            );

            MenuResenas menuResenas = new MenuResenas(
                    resenas, contenidos, entrada
            );

            MenuFavoritos menuFavoritos = new MenuFavoritos(
                    favoritos, contenidos, usuarios, entrada
            );

            MenuDirectores menuDirectores = new MenuDirectores(
                    directores, peliculas, series, entrada
            );

            MenuCuentas menuCuentas = new MenuCuentas(
                    usuarios, entrada
            );
            MenuMiCuenta menuMiCuenta = new MenuMiCuenta(
                    gestionCuenta, resenas, favoritos, contenidos, entrada
            );

            Sistema sistema = new Sistema(
                    gestionCuenta,
                    entrada,
                    menuPeliculas,
                    menuSeries,
                    menuVideojuegos,
                    menuResenas,
                    menuFavoritos,
                    menuDirectores,
                    menuCuentas,
                    menuMiCuenta
            );

            sistema.mostrarMenuInicio();

        } catch (SQLException e) {
            System.err.println(
                    "No se pudo iniciar Temuflix. " + e.getMessage()
            );
        }
    }
}