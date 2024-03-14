package gz.hoteles.servicio;

import gz.hoteles.entities.Habitacion;
import gz.hoteles.entities.Hotel;
import gz.hoteles.entities.Huesped;
import gz.hoteles.entities.Servicio;

public interface IServicioHoteles {
    
    Hotel crearHotel(Hotel hotel);

    Hotel anadirServicio(int id, Servicio servicio);

    Hotel anadirHabitacion(int id, Habitacion habitacion);

    Habitacion anadirHuesped(int id, Huesped huesped);

    Habitacion crearHabitacion(Habitacion habitacion);

}
