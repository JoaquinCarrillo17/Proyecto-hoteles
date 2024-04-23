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
import gz.hoteles.entities.Historico;
import gz.hoteles.repositories.HabitacionRepository;
import gz.hoteles.repositories.HistoricoRepository;
import gz.hoteles.repositories.HotelRepository;
import gz.hoteles.repositories.HuespedRepository;
import gz.hoteles.repositories.ServicioRepository;

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

    @Autowired
    ServicioRepository servicioRepository;

    @GetMapping()
    public ResponseEntity<?> list() {
        this.crearHistoricoSiNoExiste();
        List<Historico> historicos = historicoRepository.findAll();
        if (historicos.size() > 0) {
            return ResponseEntity.ok(historicos);
        } else
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No se ha encontrado ningún historico");
    }

    public ResponseEntity<?> crearHistoricoSiNoExiste() {
        // Obtener la fecha de hoy sin la parte de la hora
        LocalDate today = LocalDate.now();

        // Verificar si hay un historico para la fecha de hoy
        Optional<Historico> historicoExistente = historicoRepository.findByFecha(today);

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
            long serviciosTotales = servicioRepository.count(); // Utiliza el método correspondiente del repositorio para obtener el total de servicios

            // Crear un nuevo objeto Historico
            Historico nuevoHistorico = new Historico();
            nuevoHistorico.setFecha(today);
            nuevoHistorico.setHotelesTotales((int) totalHoteles);
            nuevoHistorico.setHabitacionesTotales((int) habitacionesTotales);
            nuevoHistorico.setHabitacionesDisponibles((int) habitacionesDisponibles);
            nuevoHistorico.setHabitacionesReservadas((int) habitacionesReservadas);
            nuevoHistorico.setHuespedesTotales((int) huespedesTotales);
            nuevoHistorico.setServiciosTotales((int) serviciosTotales);

            // Guardar el nuevo historico en la base de datos
            historicoRepository.save(nuevoHistorico);

            return ResponseEntity.ok(nuevoHistorico);
        } else {
            // Ya existe un historico para hoy
            return ResponseEntity.ok("Ya existe un historico para hoy.");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable(name = "id") int id) {
        if (id <= 0  || Integer.valueOf(id) == null) {
            throw new IllegalArgumentException("El ID debe ser un número entero positivo");
        }
        Historico historico = historicoRepository.findById(id).orElse(null);
        if (historico == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se encontró ningún historico con el ID proporcionado");
        } else
            return ResponseEntity.ok(historico);
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
