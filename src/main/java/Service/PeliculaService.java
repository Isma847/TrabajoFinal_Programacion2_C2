package Service;

import DAO.PeliculaDao;
import Enumerados.GeneroAudiovisual;
import Excepciones.DatoInvalidoException;
import Modelo.Pelicula;

import java.sql.SQLException;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class PeliculaService extends Service<Pelicula> {

    public PeliculaService() throws SQLException {
        super(new PeliculaDao());
    }

    @Override
    protected void validar(Pelicula p) {
        if (p == null) {
            throw new DatoInvalidoException("La película es obligatoria");
        }

        if (p.getGenero() == null) {
            throw new DatoInvalidoException("El género es obligatorio");
        }

        if (p.getClasificacionEdad() == null) {
            throw new DatoInvalidoException("La clasificación es obligatoria");
        }

        String fecha = p.getFechaEstreno();

        if (fecha == null || fecha.length() != 10) {
            throw new DatoInvalidoException(
                    "La fecha de estreno debe tener el formato AAAA-MM-DD"
            );
        }

        try {
            LocalDate.parse(fecha);
        } catch (DateTimeParseException e) {
            throw new DatoInvalidoException(
                    "La fecha de estreno no es válida. Usá AAAA-MM-DD"
            );
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
