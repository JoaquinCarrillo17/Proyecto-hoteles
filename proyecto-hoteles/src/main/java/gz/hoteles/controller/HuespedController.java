package gz.hoteles.controller;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El ID debe ser un número entero positivo");
        } 
        Huesped huesped = huespedRepository.findById(id).orElse(null);
        if (huesped == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró ningún huésped con el ID proporcionado");
        } else return ResponseEntity.ok(huesped);
    }

    @GetMapping("/filteredByName")
    public ResponseEntity<?> getHuespedesByNombre(@RequestParam String nombre, @RequestParam int pages) {
        if (nombre == null || nombre.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El parámetro 'nombre' no puede estar vacío");
        }
        Pageable pageable = PageRequest.of(0, pages);
        Page<Huesped> page = huespedRepository.getHuespedesByNombre(nombre, pageable);
        List<Huesped> huespedes = page.getContent();
        if (huespedes.size() > 0){
            return ResponseEntity.ok(huespedes);
        } else throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No se ha encontrado ningún huésped con nombre '" + nombre + "'");
    }

    @GetMapping("/filteredByDni")
    public ResponseEntity<?> getHuespedesByDni(@RequestParam String dni, @RequestParam int pages) {
        if (dni == null || dni.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El parámetro 'dni' no puede estar vacío");
        }
        Pageable pageable = PageRequest.of(0, pages);
        Page<Huesped> page = huespedRepository.getHuespedesByDni(dni, pageable);
        List<Huesped> huespedes = page.getContent();
        if (huespedes.size() > 0){
            return ResponseEntity.ok(huespedes);
        } else throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No se ha encontrado ningún huésped con dni '" + dni + "'");
    }

    @GetMapping("/filteredByEmail")
    public ResponseEntity<?> getHuespedesByEmail(@RequestParam String email, @RequestParam int pages) {
        if (email == null || email.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El parámetro 'email' no puede estar vacío");
        }
        Pageable pageable = PageRequest.of(0, pages);
        Page<Huesped> page = huespedRepository.getHuespedesByEmail(email, pageable);
        List<Huesped> huespedes = page.getContent();
        if (huespedes.size() > 0){
            return ResponseEntity.ok(huespedes);
        } else throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No se ha encontrado ningún huésped con email '" + email + "'");
    }

    @GetMapping("/filteredByCheckInDate")
    public ResponseEntity<?> getHuespedesByFechaEntrada(@RequestParam Date fecha, @RequestParam int pages) {
        if (fecha == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El parámetro 'fecha' no puede estar vacío");
        }
        Pageable pageable = PageRequest.of(0, pages);
        Page<Huesped> page = huespedRepository.getHuespedesByFechaEntrada(fecha, pageable);
        List<Huesped> huespedes = page.getContent();
        if (huespedes.size() > 0){
            return ResponseEntity.ok(huespedes);
        } else throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No se ha encontrado ningún huésped con fecha de entrada '" + fecha + "'");
    }

    @GetMapping("/filteredByCheckOutDate")
    public ResponseEntity<?> getHuespedesByFechaSalida(@RequestParam Date fecha, @RequestParam int pages) {
        if (fecha == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El parámetro 'fecha' no puede estar vacío");
        }
        Pageable pageable = PageRequest.of(0, pages);
        Page<Huesped> page = huespedRepository.getHuespedesByFechaSalida(fecha, pageable);
        List<Huesped> huespedes = page.getContent();
        if (huespedes.size() > 0){
            return ResponseEntity.ok(huespedes);
        } else throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No se ha encontrado ningún huésped con fecha de salida '" + fecha + "'");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable(name = "id") int id, @RequestBody Huesped input) {
        Huesped find = huespedRepository.findById(id).get();   
        if(find != null){     
            find.setNombreCompleto(input.getNombreCompleto());
            find.setDni(input.getDni());
            find.setEmail(input.getEmail());
            find.setFechaCheckIn(input.getFechaCheckIn());
            find.setFechaCheckOut(input.getFechaCheckOut());
        }
        Huesped save = huespedRepository.save(find);
           return ResponseEntity.ok(save);
    }

    @PostMapping
    public ResponseEntity<?> post(@RequestBody Huesped input) {
        
        if(input.getNombreCompleto().isEmpty() || input.getNombreCompleto() == null || input.getDni() == null || input.getDni().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre y el dni son obligatorios");
        }
        Huesped save = huespedRepository.save(input);
        return ResponseEntity.ok(save);
    }

     @DeleteMapping("/{id}")   
    public ResponseEntity<?> delete(@PathVariable(name = "id") int id) {
        if (id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El ID debe ser un número entero positivo");
        } 
        Optional<Huesped> findById = huespedRepository.findById(id);   
        if(findById.isPresent()){               
            huespedRepository.delete(findById.get());  
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró ningún huésped con el ID proporcionado");
        return ResponseEntity.ok().build();
    }

}
