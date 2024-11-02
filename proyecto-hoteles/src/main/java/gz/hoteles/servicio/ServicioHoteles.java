package gz.hoteles.servicio;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    private List<String> hotelPhotos = IntStream.rangeClosed(1, 20)
            .mapToObj(i -> "hotel-" + i + ".jpg")
            .collect(Collectors.toList());

    public synchronized String getAvailablePhoto() {
        // Encuentra la primera foto no asignada en la base de datos
        for (String photo : hotelPhotos) {
            if (!hotelRepository.existsByFoto(photo)) { // Método en el repositorio que verifica la existencia
                return photo;
            }
        }
        throw new RuntimeException("No hay más fotos disponibles");
    }


    /* ====== FUNCIONALIDAD HOTEL ====== */

    @Override
    public Hotel crearHotel(Hotel hotel) {
        hotel.setFoto(getAvailablePhoto());
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
        // Generar un número aleatorio entre 1 y 5 para seleccionar una foto
        Random random = new Random();
        int fotoNumero = random.nextInt(5) + 1; // Genera un número entre 1 y 5

        // Asignar la foto aleatoria a la habitación
        habitacion.setFoto("habitacion-" + fotoNumero + ".jpg");
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
