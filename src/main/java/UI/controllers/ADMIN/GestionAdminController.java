package UI.controllers.ADMIN;

import UI.controllers.ControladorBase;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class GestionAdminController extends ControladorBase {

    @FXML private Button btnVolver;

    @Override
    protected void cargarDatos() {
        comprobarAdministrador();
    }

    @FXML
    private void abrirGestionPeliculas() {
        if (comprobarAdministrador()) {
            cambiarVista(btnVolver, "/vistas/administrar-peliculas.fxml",
                    "Temuflix - Gestionar peliculas");
        }
    }

    @FXML
    private void abrirGestionSeries() {
        if (comprobarAdministrador()) {
            cambiarVista(btnVolver, "/vistas/administrar-series.fxml",
                    "Temuflix - Gestionar series");
        }
    }

    @FXML
    private void abrirGestionVideojuegos() {
        if (comprobarAdministrador()) {
            cambiarVista(btnVolver, "/vistas/administrar-videojuegos.fxml",
                    "Temuflix - Gestionar videojuegos");
        }
    }

    @FXML
    private void volver() {
        cambiarVista(btnVolver, "/vistas/menu-admin.fxml",
                "Temuflix - Administracion");
    }
}

