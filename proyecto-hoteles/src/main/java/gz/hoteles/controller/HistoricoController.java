package gz.hoteles.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import gz.hoteles.dto.HotelDTO;
import gz.hoteles.entities.Habitacion;
import gz.hoteles.entities.Historico;
import gz.hoteles.repositories.HabitacionRepository;
import gz.hoteles.repositories.HistoricoRepository;
import gz.hoteles.repositories.HotelRepository;
import gz.hoteles.repositories.HuespedRepository;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/historicos")
public class HistoricoController {

    @Autowired
    HistoricoRepository historicoRepository;

    @Autowired
    HotelRepository hotelRepository;
    
    @Autowired
    HotelController hotelController;

    @Autowired
    HabitacionRepository habitacionRepository;

    @Autowired
    HuespedRepository huespedRepository;

    @GetMapping("/admin/{idUsuario}")
    public ResponseEntity<?> list(@PathVariable(name = "idUsuario") int idUsuario) {
        this.crearHistoricoSiNoExiste(idUsuario);
        List<Historico> historicos = historicoRepository.findByIdUsuario(idUsuario);
        if (historicos.size() > 0) {
            return ResponseEntity.ok(historicos);
        } else
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No se ha encontrado ningún historico");
    }

    private ResponseEntity<?> crearHistoricoSiNoExiste(int idUsuario) {
        // Obtener la fecha de hoy sin la parte de la hora
        LocalDate today = LocalDate.now();

        // Verificar si hay un historico para la fecha de hoy
        Optional<Historico> historicoExistente = historicoRepository.findByFechaAndIdUsuario(today, idUsuario);

        if (!historicoExistente.isPresent()) {
            // No hay un historico para hoy, así que crearemos uno nuevo

            // Obtener la cantidad total de hoteles
            long totalHoteles = hotelRepository.count();

            // Calcular las habitaciones totales, disponibles y reservadas sumando las de todos los hoteles
            long habitacionesTotales = 0;
            long habitacionesDisponibles = 0;
            long habitacionesReservadas = 0;
            List<HotelDTO> hoteles = (List<HotelDTO>) hotelController.list().getBody();
            for (HotelDTO hotel : hoteles) {
                habitacionesTotales += hotel.getNumeroHabitaciones();
                habitacionesDisponibles += hotel.getNumeroHabitacionesDisponibles();
                habitacionesReservadas += hotel.getNumeroHabitacionesReservadas();
            }

            // Obtener la cantidad total de huéspedes y servicios
            long huespedesTotales = huespedRepository.count(); // Utiliza el método correspondiente del repositorio para obtener el total de huéspedes

            // Crear un nuevo objeto Historico
            Historico nuevoHistorico = new Historico();
            nuevoHistorico.setFecha(today);
            nuevoHistorico.setHotelesTotales((int) totalHoteles);
            nuevoHistorico.setHabitacionesTotales((int) habitacionesTotales);
            nuevoHistorico.setHabitacionesDisponibles((int) habitacionesDisponibles);
            nuevoHistorico.setHabitacionesReservadas((int) habitacionesReservadas);
            nuevoHistorico.setHuespedesTotales((int) huespedesTotales);
            nuevoHistorico.setIdUsuario(idUsuario); // Añadir el ID del usuario al historico

            // Guardar el nuevo historico en la base de datos
            historicoRepository.save(nuevoHistorico);

            return ResponseEntity.ok(nuevoHistorico);
        } else {
            // Ya existe un historico para hoy
            return ResponseEntity.ok("Ya existe un historico para hoy.");
        }
    }

    @GetMapping("/{idUsuario}")
    public ResponseEntity<?> getHistoricoDeHotel(@PathVariable(name = "idUsuario") int idUsuario) {
        this.crearHistoricoDeHotelSiNoExiste(idUsuario);
        List<Historico> historico = historicoRepository.findByIdUsuario(idUsuario);
        if (historico == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se encontró ningún historico con el ID proporcionado");
        } else
            return ResponseEntity.ok(historico);
    }

    private ResponseEntity<?> crearHistoricoDeHotelSiNoExiste(int idUsuario) {
        // Obtener la fecha de hoy sin la parte de la hora
        LocalDate today = LocalDate.now();

        // Verificar si hay un historico para la fecha de hoy
        Optional<Historico> historicoExistente = historicoRepository.findByFechaAndIdUsuario(today, idUsuario);

        if (!historicoExistente.isPresent()) {
            // No hay un historico para hoy, así que crearemos uno nuevo

            // Calcular las habitaciones totales, disponibles y reservadas sumando las de todos los hoteles
            int habitacionesTotales = 0;
            int habitacionesDisponibles = 0;
            int habitacionesReservadas = 0;
            HotelDTO hotel;
            try {
                hotel = hotelController.getHotelByIdUsuario(idUsuario);
            } catch (Exception e) {
                return ResponseEntity.ok().build();
            }
            habitacionesTotales += hotel.getNumeroHabitaciones();
            habitacionesDisponibles += hotel.getNumeroHabitacionesDisponibles();
            habitacionesReservadas += hotel.getNumeroHabitacionesReservadas();

            // Obtener la cantidad total de huéspedes y servicios
            int huespedesTotales = 0; 
            List<Habitacion> habitaciones = habitacionRepository.findByHotelIdUsuario(idUsuario);
            for (Habitacion habitacion : habitaciones) {
                huespedesTotales += habitacion.getHuespedes().size();
            }

            // Crear un nuevo objeto Historico
            Historico nuevoHistorico = new Historico();
            nuevoHistorico.setFecha(today);
            nuevoHistorico.setHotelesTotales(1);
            nuevoHistorico.setHabitacionesTotales(habitacionesTotales);
            nuevoHistorico.setHabitacionesDisponibles(habitacionesDisponibles);
            nuevoHistorico.setHabitacionesReservadas(habitacionesReservadas);
            nuevoHistorico.setHuespedesTotales(huespedesTotales);
            nuevoHistorico.setIdUsuario(idUsuario);

            // Guardar el nuevo historico en la base de datos
            historicoRepository.save(nuevoHistorico);

            return ResponseEntity.ok(nuevoHistorico);
        } else {
            // Ya existe un historico para hoy
            return ResponseEntity.ok("Ya existe un historico para hoy.");
        }
    }

    @PostMapping
    public ResponseEntity<?> post(@RequestBody Historico input) {
        Historico save = historicoRepository.save(input);
        return ResponseEntity.ok(save);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable(name = "id") int id) {
        if (id <= 0  || Integer.valueOf(id) == null) {
            throw new IllegalArgumentException("El ID debe ser un número entero positivo");
        }
        Historico findById = historicoRepository.findById(id).orElse(null);
        if (findById != null) {
            historicoRepository.delete(findById);
        } else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se encontró ningun historico por el ID proporcionado");
        return ResponseEntity.ok().build();
    }
    

    
}
