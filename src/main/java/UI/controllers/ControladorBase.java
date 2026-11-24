package UI.controllers;

import Enumerados.Rol;
import Excepciones.DatoInvalidoException;
import Modelo.Director;
import Modelo.Usuario;
import Service.ContenidoService;
import Service.DirectorService;
import Service.FavoritoService;
import Service.GestionCuenta;
import Service.PeliculaService;
import Service.ResenaService;
import Service.SerieService;
import Service.VideojuegoService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class ControladorBase {

    protected GestionCuenta gestionCuenta;
    protected ContenidoService contenidoService;
    protected DirectorService directorService;
    protected ResenaService resenaService;
    protected FavoritoService favoritoService;
    protected PeliculaService peliculaService;
    protected SerieService serieService;
    protected VideojuegoService videojuegoService;

    // Se llama despues de cargar el FXML. Todas las pantallas reciben los mismos servicios.
    public final void configurarServicios(GestionCuenta gestionCuenta,
                                          ContenidoService contenidoService,
                                          DirectorService directorService,
                                          ResenaService resenaService,
                                          FavoritoService favoritoService) {
        if (gestionCuenta == null || contenidoService == null || directorService == null
                || resenaService == null || favoritoService == null) {
            throw new IllegalArgumentException("Faltan servicios para abrir la pantalla.");
        }

        this.gestionCuenta = gestionCuenta;
        this.contenidoService = contenidoService;
        this.directorService = directorService;
        this.resenaService = resenaService;
        this.favoritoService = favoritoService;
        this.peliculaService = contenidoService.getPeliculas();
        this.serieService = contenidoService.getSeries();
        this.videojuegoService = contenidoService.getVideojuegos();

        cargarDatos();
    }

    // Cada pantalla puede sobrescribir este metodo para consultar sus datos.
    protected void cargarDatos() {
    }

    protected void cambiarVista(Node componente, String ruta, String titulo) {
        try {
            if (!"/vistas/login.fxml".equals(ruta) && !"/vistas/registro.fxml".equals(ruta)) {
                usuarioActual();
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(ruta));
            Parent raiz = loader.load();
            ControladorBase controlador = loader.getController();

            controlador.configurarServicios(
                    gestionCuenta, contenidoService, directorService,
                    resenaService, favoritoService
            );

            Stage ventana = (Stage) componente.getScene().getWindow();
            ventana.setTitle(titulo);
            ventana.setScene(new Scene(raiz));
            ventana.sizeToScene();
            ventana.centerOnScreen();
            ventana.show();

        } catch (IOException | RuntimeException e) {
            mostrarMensaje("Error", "No se pudo abrir la pantalla: " + e.getMessage());
        }
    }

    protected Usuario usuarioActual() {
        if (gestionCuenta == null) {
            throw new DatoInvalidoException("Los servicios de la pantalla no estan configurados.");
        }
        return gestionCuenta.consultarMiCuenta();
    }

    protected boolean comprobarAdministrador() {
        try {
            if (usuarioActual().getRol() == Rol.ADMINISTRADOR) {
                return true;
            }
            mostrarMensaje("Acceso", "Esta opcion requiere una cuenta administradora.");
        } catch (DatoInvalidoException e) {
            mostrarMensaje("Acceso", e.getMessage());
        }
        return false;
    }

    protected void mostrarMensaje(String titulo, String texto) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(texto);
        alerta.showAndWait();
    }

    protected int leerEnteroPositivo(TextField campo, String nombre) {
        try {
            int numero = Integer.parseInt(campo.getText().trim());
            if (numero <= 0) {
                throw new DatoInvalidoException(nombre + " debe ser mayor que cero.");
            }
            return numero;
        } catch (NumberFormatException e) {
            throw new DatoInvalidoException(nombre + " debe ser un numero entero.");
        }
    }

    protected String leerTextoObligatorio(TextField campo, String nombre, int maximo) {
        String texto = campo.getText().trim();
        if (texto.isEmpty() || texto.codePointCount(0, texto.length()) > maximo) {
            throw new DatoInvalidoException(
                    nombre + " debe tener entre 1 y " + maximo + " caracteres."
            );
        }
        return texto;
    }

    protected String leerDescripcion(TextArea campo) {
        String texto = campo.getText().trim();
        if (texto.codePointCount(0, texto.length()) > 600) {
            throw new DatoInvalidoException("La descripcion admite hasta 600 caracteres.");
        }
        return texto.isEmpty() ? null : texto;
    }

    protected String descripcionVisible(String descripcion) {
        return descripcion == null || descripcion.isBlank() ? "Sin descripcion" : descripcion;
    }

    protected String nombreDirector(int idDirector) {
        if (idDirector <= 0) {
            return "Sin director";
        }
        Director director = directorService.buscarPorId(idDirector);
        return director == null ? "Sin director"
                : director.getNombre() + " " + director.getApellido();
    }
}

