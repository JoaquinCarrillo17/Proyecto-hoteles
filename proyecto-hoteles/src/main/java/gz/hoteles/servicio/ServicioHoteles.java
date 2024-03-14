package gz.hoteles.servicio;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gz.hoteles.entities.Habitacion;
import gz.hoteles.entities.Hotel;
import gz.hoteles.entities.Huesped;
import gz.hoteles.entities.Servicio;
import gz.hoteles.repositories.HabitacionRepository;
import gz.hoteles.repositories.HotelRepository;
import gz.hoteles.repositories.HuespedRepository;

@Service
public class ServicioHoteles implements IServicioHoteles {

    @Autowired
    HabitacionRepository habitacionRepository;
    @Autowired
    HuespedRepository huespedRepository;
    @Autowired
    HotelRepository hotelRepository;

    @Override
    public Hotel crearHotel(Hotel hotel) {
       Hotel h = hotelRepository.save(hotel);

       for (Habitacion habitacion : h.getHabitaciones()) {
            habitacion.setHotel(h);
            habitacionRepository.save(habitacion);
            for (Huesped huesped : habitacion.getHuespedes()) {
                huesped.setHabitacion(habitacion);
                huespedRepository.save(huesped);
            }
       }

       return h;
    }

    @Override
    public Hotel anadirServicio(int id, Servicio servicio) {
        Optional<Hotel> optionalHotel = hotelRepository.findById(id);
        if (optionalHotel.isPresent()) {
            Hotel h = optionalHotel.get();
            // Hacer algo con el hotel encontrado
        } else {
            // Manejar el caso en el que no se encuentra ningún hotel con el ID especificado
        }

    }


    
}
