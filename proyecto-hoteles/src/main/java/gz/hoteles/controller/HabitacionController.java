package gz.hoteles.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import gz.hoteles.entities.Habitacion;
import gz.hoteles.entities.Huesped;
import gz.hoteles.entities.JSONMapper;
import gz.hoteles.entities.TipoHabitacion;
import gz.hoteles.repositories.HabitacionRepository;
import gz.hoteles.servicio.IServicioHoteles;

@RestController
@RequestMapping("/habitaciones")
public class HabitacionController {
    
    @Autowired
    HabitacionRepository habitacionRepository;
    @Autowired
    IServicioHoteles servicioHoteles;

    @GetMapping()
    public ResponseEntity<?> list() {
        List<Habitacion> habitaciones = habitacionRepository.findAll();
        if (habitaciones.size() > 0) {
            return ResponseEntity.ok(habitaciones);
        } else throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No se ha encontrado ninguna habitación"); 
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable(name = "id") int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser un número entero positivo");
        } 
        Habitacion habitacion = habitacionRepository.findById(id).orElse(null);
        if (habitacion == null) {
           throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró ninguna habitación con el ID proporcionado");
        } else return ResponseEntity.ok(habitacion);
    }

    @GetMapping("/filteredByNumber")
    public ResponseEntity<?> getHabitacionesByNumero(@RequestParam String numero, @RequestParam int pages) {
        if (numero == null || numero.isEmpty()) {
            throw new IllegalArgumentException("El parámetro 'número' no puede estar vacío");
        }
        Pageable pageable = PageRequest.of(0, pages);
        Page<Habitacion> page = habitacionRepository.getHabitacionesByNumero(numero, pageable);
        List<Habitacion> habitaciones = page.getContent();
        if (habitaciones.size() > 0) {
            return ResponseEntity.ok(habitaciones);
        } else throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No se encontró ninguna habitación con el número '" + numero + "'");
    }

    @GetMapping("/filteredByTypeOfRoom")
    public ResponseEntity<?> getHabitacionesByTipoHabitacion(@RequestParam TipoHabitacion tipo, @RequestParam int pages) {
        if (tipo == null) {
            throw new IllegalArgumentException("El parámetro 'tipo' no puede ser nulo");
        }
        Pageable pageable = PageRequest.of(0, pages);
        Page<Habitacion> page = habitacionRepository.getHabitacionesByTipoHabitacion(tipo, pageable);
        List<Habitacion> habitaciones = page.getContent();
        if (habitaciones.size() > 0) {
            return ResponseEntity.ok(habitaciones);
        } else throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No se encontró ninguna habitación del tipo '" + tipo + "'");
    }

    @GetMapping("/filteredByPricePerNight")
    public ResponseEntity<?> getHabitacionesByPrecioPorNoche(@RequestParam String precio, @RequestParam int pages) {
        if (precio == null || precio.isEmpty()) {
            throw new IllegalArgumentException("El parámetro 'precio' no puede ser nulo.");
        }
        Pageable pageable = PageRequest.of(0, pages);
        Page<Habitacion> page = habitacionRepository.getHabitacionesByPrecioPorNoche(precio, pageable);
        List<Habitacion> habitaciones = page.getContent();
        if (habitaciones.size() > 0) {
            return ResponseEntity.ok(habitaciones);
        } else throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No se encontró ninguna habitación con precio '" + precio + "€'");
    }

    @PostMapping("/dynamicSearch")
    public ResponseEntity<?> getHabitacionesFilteredByParam(@RequestBody JSONMapper json) {
        if (json == null || json.getField() == null || json.getPages() <= 0
                || json.getSortBy() == null) {
            throw new IllegalArgumentException("Falta uno o más campos requeridos en el JSON");
        }

        String field = json.getField();
        String value = json.getValue();
        String sortDirection = json.getSortBy().equalsIgnoreCase("asc") ? "ASC" : "DESC";

        Page<Habitacion> page = switch (field) {
            case "numero" -> habitacionRepository.findByNumeroEquals(value, PageRequest.of(0, json.getPages(), Sort.by(Sort.Direction.fromString(sortDirection), "id")));
            case "tipoHabitacion" -> habitacionRepository.findByTipoHabitacionEquals(value, PageRequest.of(0, json.getPages(), Sort.by(Sort.Direction.fromString(sortDirection), "id")));
            case "precioNoche" -> habitacionRepository.findByPrecioNocheEquals(value, PageRequest.of(0, json.getPages(), Sort.by(Sort.Direction.fromString(sortDirection), "id")));
            default -> throw new IllegalArgumentException("El campo proporcionado en el JSON no es válido");
        };

        List<Habitacion> habitaciones = page.getContent();
        if (habitaciones.size() > 0) {
            return ResponseEntity.ok(habitaciones);
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron hoteles con " + json.getField() + " = " + json.getValue());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable(name = "id") int id, @RequestBody Habitacion input) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser un número entero positivo");
        }
        Habitacion find = habitacionRepository.findById(id).orElse(null);   
        if(find != null){     
            find.setNumero(input.getNumero());
            find.setPrecioNoche(input.getPrecioNoche());
            find.setTipoHabitacion(input.getTipoHabitacion());
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró ninguna habitación por el ID proporcionado");
        Habitacion save = habitacionRepository.save(find);
        return ResponseEntity.ok(save);
    }

    @PostMapping
    public ResponseEntity<?> post(@RequestBody Habitacion input) {
        if (input.getTipoHabitacion() == null) {
            throw new IllegalArgumentException("Debes introducir el tipo de habitación");
        }
        Habitacion save = servicioHoteles.crearHabitacion(input);
        return ResponseEntity.ok(save);
    }

    @PostMapping("/{id}/huespedes")
    public ResponseEntity<?> anadirHuesped(@PathVariable(name = "id") int id, @RequestBody Huesped huesped) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser un número entero positivo");
        }
        Habitacion h = servicioHoteles.anadirHuesped(id, huesped); // Contemplo el NOT_FOUND en el servicioHoteles
        return ResponseEntity.ok(h);
    }

    @DeleteMapping("/{id}")   
    public ResponseEntity<?> delete(@PathVariable(name = "id") int id) {
        Habitacion findById = habitacionRepository.findById(id).orElse(null);   
        if(findById != null){               
            habitacionRepository.delete(findById);  
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró ninguna habitacion por el ID proporcionado");
        return ResponseEntity.ok().build();
    }

}
