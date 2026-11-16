package Service;

import DAO.SerieDao;
import Enumerados.GeneroAudiovisual;
import Modelo.Serie;

import java.sql.SQLException;
import java.util.ArrayList;

public class SerieService extends Service<Serie> {

    public SerieService() throws SQLException {
        super(new SerieDao());
    }

    @Override
    protected void validar(Serie s) {
        if(s.getGenero() == null) {
            throw new IllegalArgumentException("El género es obligatorio");
        }
    }

    public ArrayList<Serie> filtrarPorGenero(GeneroAudiovisual genero, boolean baja) {
        ArrayList<Serie> resultado = new ArrayList<>();
        for(Serie s : getLista(baja)) {
            if(s.getGenero() == genero) {
                resultado.add(s);
            }
        }
        return resultado;
    }
}