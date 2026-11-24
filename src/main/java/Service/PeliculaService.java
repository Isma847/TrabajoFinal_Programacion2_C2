package Service;

import DAO.PeliculaDao;
import Enumerados.GeneroAudiovisual;
import Excepciones.DatoInvalidoException;
import Modelo.Pelicula;

import java.sql.SQLException;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Service de películas. Hereda de {@code Service<Pelicula>} todas las operaciones comunes
 * (agregar, modificar, dar de baja, eliminar y listar) y agrega solo lo propio de la película:
 * sus validaciones, sus filtros y quitarDirector.
 */
public class PeliculaService extends Service<Pelicula> {

    /// Le pasa su DAO a la clase base, que carga la colección desde la base.
    public PeliculaService() throws SQLException {
        super(new PeliculaDao());
    }

    /**
     * Reglas de la película: no puede ser null, el género y la clasificación son obligatorios y
     * la fecha de estreno debe tener formato AAAA-MM-DD y ser una fecha real (se comprueba con
     * LocalDate.parse). Si algo falla lanza DatoInvalidoException.
     */
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

    /**
     * Películas de un género según su estado de baja (false = activas). Se recorre la lista con
     * un for, sin streams.
     */
    public ArrayList<Pelicula> filtrarPorGenero(GeneroAudiovisual genero, boolean baja) {
        ArrayList<Pelicula> resultado = new ArrayList<>();
        for(Pelicula p : getLista(baja)) {
            if(p.getGenero() == genero) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    /// Películas de un director según su estado de baja.
    public ArrayList<Pelicula> filtrarPorDirector(int idDirector, boolean baja) {
        ArrayList<Pelicula> resultado = new ArrayList<>();

        for (Pelicula pelicula : getLista(baja)) {
            if (pelicula.getIdDirector() == idDirector) {
                resultado.add(pelicula);
            }
        }

        return resultado;
    }

    /**
     * Mantiene la memoria al día cuando se elimina un director: la base ya puso su id_director en
     * NULL (ON DELETE SET NULL), pero la colección no se entera, así que acá se le asigna
     * idDirector = 0 (sin director) a sus películas. Lo debe llamar quien elimina el director,
     * después de DirectorService.eliminar.
     */
    public void quitarDirector(int idDirector) {
        for(Pelicula p : coleccion.values()) {
            if(p.getIdDirector() == idDirector) {
                p.setIdDirector(0);
            }
        }
    }
}
