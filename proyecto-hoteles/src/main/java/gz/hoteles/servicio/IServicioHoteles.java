package gz.hoteles.servicio;

import gz.hoteles.entities.Habitacion;
import gz.hoteles.entities.Hotel;
import gz.hoteles.entities.Huesped;

public interface IServicioHoteles {
    
    Hotel crearHotel(Hotel hotel);

    Hotel anadirHabitacion(int id, Habitacion habitacion);

    Habitacion anadirHuesped(int id, Huesped huesped);

    Habitacion crearHabitacion(Habitacion habitacion);

}
