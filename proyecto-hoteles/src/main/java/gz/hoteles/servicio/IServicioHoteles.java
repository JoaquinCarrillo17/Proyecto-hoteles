package gz.hoteles.servicio;

import gz.hoteles.entities.Hotel;
import gz.hoteles.entities.Servicio;

public interface IServicioHoteles {
    
    Hotel crearHotel(Hotel hotel);

    Hotel anadirServicio(int id, Servicio servicio);

}
