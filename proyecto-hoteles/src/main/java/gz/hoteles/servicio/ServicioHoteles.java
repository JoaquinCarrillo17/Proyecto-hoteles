package gz.hoteles.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import gz.hoteles.entities.Habitacion;
import gz.hoteles.entities.Hotel;
import gz.hoteles.entities.Huesped;
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


    /* ====== FUNCIONALIDAD HOTEL ====== */

    @Override
    public Hotel crearHotel(Hotel hotel) {
        hotel.updateHabitaciones(hotel.getHabitaciones());
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
    public Hotel anadirHabitacion(int id, Habitacion habitacion) {
        habitacionRepository.save(habitacion);
        Hotel hotel = hotelRepository.findById(id).orElse(null);
        if (hotel != null) {
            hotel.addHabitacion(habitacion);
            hotelRepository.save(hotel);
            habitacion.setHotel(hotel);
            habitacionRepository.save(habitacion);
            for (Huesped huesped : habitacion.getHuespedes()){
                huesped.setHabitacion(habitacion);
                huespedRepository.save(huesped);
            }          
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró ningún hotel por el ID proporcionado");
        return hotel;
    }

    /* ====== FUNCIONALIDAD HABITACIÓN ====== */

    @Override
    public Habitacion crearHabitacion(Habitacion habitacion) {
        Habitacion h = habitacionRepository.save(habitacion);
        for (Huesped huesped : habitacion.getHuespedes()) {
            huesped.setHabitacion(h);
            huespedRepository.save(huesped);
        }
        return h;
    }

    @Override
    public Habitacion anadirHuesped(int id, Huesped huesped) {
        huespedRepository.save(huesped);
        Habitacion habitacion = habitacionRepository.findById(id).orElse(null);
        if (habitacion != null) {
            habitacion.addHuesped(huesped);
            habitacionRepository.save(habitacion);
            huesped.setHabitacion(habitacion);
            huespedRepository.save(huesped);
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró ninguna habitacion por el ID proporcionado");
        return habitacion;
    } 

}
