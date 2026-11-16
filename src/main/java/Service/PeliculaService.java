package Service;

import DAO.PeliculaDao;
import Enumerados.GeneroAudiovisual;
import Excepciones.DatoInvalidoException;
import Modelo.Pelicula;

import java.sql.SQLException;
import java.util.ArrayList;

public class PeliculaService extends Service<Pelicula> {

    public PeliculaService() throws SQLException {
        super(new PeliculaDao());
    }

    @Override
    protected void validar(Pelicula p) {
        if(p.getGenero() == null) {
            throw new DatoInvalidoException("El género es obligatorio");
        }
        if(p.getClasificacionEdad() == null) {
            throw new DatoInvalidoException("El clasificacion es obligatorio");
        }
    }

    public ArrayList<Pelicula> filtrarPorGenero(GeneroAudiovisual genero, boolean baja) {
        ArrayList<Pelicula> resultado = new ArrayList<>();
        for(Pelicula p : getLista(baja)) {
            if(p.getGenero() == genero) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    // Se llama despues de eliminar un director desde la clase de sistema
    public void quitarDirector(int idDirector) {
        for(Pelicula p : coleccion.values()) {
            if(p.getIdDirector() == idDirector) {
                p.setIdDirector(0);
            }
        }
    }
}
