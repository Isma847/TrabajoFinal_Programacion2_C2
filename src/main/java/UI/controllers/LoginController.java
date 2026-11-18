package UI.controllers;

import Excepciones.SesionInvalidaException;
import Modelo.Administrador;
import Modelo.Cuenta;
import Modelo.Usuario;
import Service.GestionCuenta;
import Service.PeliculaService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import UI.controllers.MenuAdminController;

import java.io.IOException;




public class LoginController {

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtContrasena;

    @FXML
    private Button btnIniciarSesion;


    private GestionCuenta gestionCuenta;

    private PeliculaService peliculaService;

    public void setPeliculaService(PeliculaService peliculaService) {
        this.peliculaService = peliculaService;
    }


    public void setGestionCuenta(GestionCuenta gestionCuenta) {
        this.gestionCuenta = gestionCuenta;
    }

    //Metodo que conecta el boton con el controlador

    @FXML
    private void iniciarSesion() {

        String usuario = txtUsuario.getText();
        String contrasena = txtContrasena.getText();

        try {
            // Validamos las credenciales
            gestionCuenta.iniciarSesion(usuario, contrasena);

            // Obtenemos la cuenta que inició sesión
            Cuenta cuenta = gestionCuenta.getUsuarioActual();

            // Decidimos qué ventana abrir
            if (cuenta instanceof Administrador) {
                cargarVentana("/vistas/menu-admin.fxml");
            } else if (cuenta instanceof Usuario) {
                cargarVentana("/vistas/menu-usuario.fxml");
            }

        } catch (SesionInvalidaException e) {
            System.out.println(e.getMessage());

        } catch (IOException e) {
            System.out.println("Error al cargar la ventana: " + e.getMessage());
        }
    }

    //este metodo cargar ventana se va a encargar de reemplazar la pantalla del
    //login por la proxima

    private void cargarVentana(String ruta) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource(ruta));
        Parent root = loader.load();

        if (ruta.equals("/vistas/menu-admin.fxml")) {
            MenuAdminController controller = loader.getController();
            controller.setGestionCuenta(gestionCuenta);
        }

        if (ruta.equals("/vistas/menu-usuario.fxml")) {
            MenuUsuarioController controller = loader.getController();

            controller.setGestionCuenta(gestionCuenta);
            controller.setPeliculaService(peliculaService);
        }

        Stage stage = (Stage) btnIniciarSesion.getScene().getWindow();

        Scene escena = new Scene(root);
        stage.setScene(escena);
        stage.show();
    }



}

