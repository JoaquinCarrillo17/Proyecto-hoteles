package gz.hoteles.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gz.hoteles.entities.Hotel;
import gz.hoteles.entities.Ubicacion;
import gz.hoteles.repositories.HotelRepository;
import gz.hoteles.repositories.UbicacionRepository;

@Service
public class ServicioUbicacion {

    @Autowired
    private UbicacionRepository ubicacionRepository;
    @Autowired
    private HotelRepository hotelRepository;

    // Crear una nueva ubicación
    public Ubicacion crearUbicacion(Ubicacion ubicacion) {
        return ubicacionRepository.save(ubicacion);
    }

    // Obtener todas las ubicaciones
    public List<Ubicacion> obtenerTodasLasUbicaciones() {
        return ubicacionRepository.findAll();
    }

    // Obtener una ubicación por ID
    public Ubicacion obtenerUbicacionPorId(int id) {
        return ubicacionRepository.findById(id).orElse(null);
    }

    // Actualizar una ubicación
    public Ubicacion actualizarUbicacion(int id, Ubicacion detallesUbicacion) {
        Ubicacion ubicacion = obtenerUbicacionPorId(id);
        if (ubicacion != null) {
            ubicacion.setCiudad(detallesUbicacion.getCiudad());
            ubicacion.setPais(detallesUbicacion.getPais());
            ubicacion.setContinente(detallesUbicacion.getContinente());
            return ubicacionRepository.save(ubicacion);
        }
        return null;
    }

    // Eliminar una ubicación
    public boolean eliminarUbicacion(int id) {
        Ubicacion ubicacion = obtenerUbicacionPorId(id);
        if (ubicacion != null) {
            ubicacionRepository.delete(ubicacion);
            return true;
        }
        return false;
    }

    public Ubicacion anadirHoteles(int id, List<Integer> idsHoteles) {
        Ubicacion ubicacion = obtenerUbicacionPorId(id);
        if (ubicacion!= null) {
            for (Integer idHotel : idsHoteles) {
                Hotel h = hotelRepository.getById(idHotel);
                h.setUbicacion(ubicacion);
                hotelRepository.save(h);
            }
        }
        return ubicacion;
    }

}

