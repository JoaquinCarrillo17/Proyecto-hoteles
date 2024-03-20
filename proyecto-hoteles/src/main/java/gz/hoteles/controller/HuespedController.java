package gz.hoteles.controller;

import java.util.Date;
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
import gz.hoteles.entities.Huesped;
import gz.hoteles.entities.JSONMapper;
import gz.hoteles.repositories.HuespedRepository;

@RestController
@RequestMapping("/huespedes")
public class HuespedController {
    
    @Autowired
    HuespedRepository huespedRepository;

    @GetMapping()
    public ResponseEntity<?> list() {
        List<Huesped> huespedes = huespedRepository.findAll();
        if (huespedes.size() > 0) {
            return ResponseEntity.ok(huespedes);
        } else {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No se ha encontrado ningún huésped");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable(name = "id") int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser un número entero positivo");
        } 
        Huesped huesped = huespedRepository.findById(id).orElse(null);
        if (huesped == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró ningún huésped con el ID proporcionado");
        } else return ResponseEntity.ok(huesped);
    }

    @GetMapping("/filteredByName")
    public ResponseEntity<?> getHuespedesByNombre(@RequestParam String nombre, @RequestParam int pages) {
        if (nombre == null || nombre.isEmpty()) {
            throw new IllegalArgumentException("El parámetro 'nombre' no puede estar vacío");
        }
        Pageable pageable = PageRequest.of(0, pages);
        Page<Huesped> page = huespedRepository.getHuespedesByNombre(nombre, pageable);
        List<Huesped> huespedes = page.getContent();
        if (huespedes.size() > 0){
            return ResponseEntity.ok(huespedes);
        } else throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No se encontró ningún huésped con nombre '" + nombre + "'");
    }

    @GetMapping("/filteredByDni")
    public ResponseEntity<?> getHuespedesByDni(@RequestParam String dni, @RequestParam int pages) {
        if (dni == null || dni.isEmpty()) {
            throw new IllegalArgumentException("El parámetro 'dni' no puede estar vacío");
        }
        Pageable pageable = PageRequest.of(0, pages);
        Page<Huesped> page = huespedRepository.getHuespedesByDni(dni, pageable);
        List<Huesped> huespedes = page.getContent();
        if (huespedes.size() > 0){
            return ResponseEntity.ok(huespedes);
        } else throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No se encontró ningún huésped con dni '" + dni + "'");
    }

    @GetMapping("/filteredByEmail")
    public ResponseEntity<?> getHuespedesByEmail(@RequestParam String email, @RequestParam int pages) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("El parámetro 'email' no puede estar vacío");
        }
        Pageable pageable = PageRequest.of(0, pages);
        Page<Huesped> page = huespedRepository.getHuespedesByEmail(email, pageable);
        List<Huesped> huespedes = page.getContent();
        if (huespedes.size() > 0){
            return ResponseEntity.ok(huespedes);
        } else throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No se encontró ningún huésped con email '" + email + "'");
    }

    @GetMapping("/filteredByCheckInDate")
    public ResponseEntity<?> getHuespedesByFechaEntrada(@RequestParam Date fecha, @RequestParam int pages) {
        if (fecha == null) {
            throw new IllegalArgumentException("El parámetro 'fecha' no puede estar vacío");
        }
        Pageable pageable = PageRequest.of(0, pages);
        Page<Huesped> page = huespedRepository.getHuespedesByFechaEntrada(fecha, pageable);
        List<Huesped> huespedes = page.getContent();
        if (huespedes.size() > 0){
            return ResponseEntity.ok(huespedes);
        } else throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No se encontró ningún huésped con fecha de entrada '" + fecha + "'");
    }

    @GetMapping("/filteredByCheckOutDate")
    public ResponseEntity<?> getHuespedesByFechaSalida(@RequestParam Date fecha, @RequestParam int pages) {
        if (fecha == null) {
            throw new IllegalArgumentException("El parámetro 'fecha' no puede estar vacío");
        }
        Pageable pageable = PageRequest.of(0, pages);
        Page<Huesped> page = huespedRepository.getHuespedesByFechaSalida(fecha, pageable);
        List<Huesped> huespedes = page.getContent();
        if (huespedes.size() > 0){
            return ResponseEntity.ok(huespedes);
        } else throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No se encontró ningún huésped con fecha de salida '" + fecha + "'");
    }

    @PostMapping("/dynamicSearch")
    public ResponseEntity<?> getHuespedesFilteredByParam(@RequestBody JSONMapper json) {
        if (json == null || json.getField() == null || json.getPages() <= 0
                || json.getSortBy() == null) {
            throw new IllegalArgumentException("Falta uno o más campos requeridos en el JSON");
        }

        String field = json.getField();
        String value = json.getValue();
        String sortDirection = json.getSortBy().equalsIgnoreCase("asc") ? "ASC" : "DESC";

        Page<Huesped> page = switch (field) {
            case "nombre" -> huespedRepository.findByNombreCompletoEquals(value, PageRequest.of(0, json.getPages(), Sort.by(Sort.Direction.fromString(sortDirection), "id")));
            case "dni" -> huespedRepository.findByDniEquals(value, PageRequest.of(0, json.getPages(), Sort.by(Sort.Direction.fromString(sortDirection), "id")));
            case "email" -> huespedRepository.findByEmailEquals(value, PageRequest.of(0, json.getPages(), Sort.by(Sort.Direction.fromString(sortDirection), "id")));
            case "fecha entrada" -> huespedRepository.findByFechaCheckInEquals(value, PageRequest.of(0, json.getPages(), Sort.by(Sort.Direction.fromString(sortDirection), "id")));
            case "fecha salida" -> huespedRepository.findByFechaCheckOutEquals(value, PageRequest.of(0, json.getPages(), Sort.by(Sort.Direction.fromString(sortDirection), "id")));
            default -> throw new IllegalArgumentException("El campo proporcionado en el JSON no es válido");
        };

        List<Huesped> huespedes = page.getContent();
        if (huespedes.size() > 0) {
            return ResponseEntity.ok(huespedes);
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron hoteles con " + json.getField() + " = " + json.getValue());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable(name = "id") int id, @RequestBody Huesped input) {
        Huesped find = huespedRepository.findById(id).orElse(null);   
        if(find != null){     
            find.setNombreCompleto(input.getNombreCompleto());
            find.setDni(input.getDni());
            find.setEmail(input.getEmail());
            find.setFechaCheckIn(input.getFechaCheckIn());
            find.setFechaCheckOut(input.getFechaCheckOut());
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró ningún huésped con el ID proporcionado");
        Huesped save = huespedRepository.save(find);
           return ResponseEntity.ok(save);
    }

    @PostMapping
    public ResponseEntity<?> post(@RequestBody Huesped input) {
        
        if(input.getNombreCompleto().isEmpty() || input.getNombreCompleto() == null || input.getDni() == null || input.getDni().isEmpty()) {
            throw new IllegalArgumentException("El nombre y el dni son obligatorios");
        }
        Huesped save = huespedRepository.save(input);
        return ResponseEntity.ok(save);
    }

    @DeleteMapping("/{id}")   
    public ResponseEntity<?> delete(@PathVariable(name = "id") int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser un número entero positivo");
        } 
        Huesped findById = huespedRepository.findById(id).orElse(null);   
        if(findById != null){               
            huespedRepository.delete(findById);  
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró ningún huésped con el ID proporcionado");
        return ResponseEntity.ok().build();
    }

}
