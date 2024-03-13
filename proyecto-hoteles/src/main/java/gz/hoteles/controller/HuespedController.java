package gz.hoteles.controller;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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

import gz.hoteles.entities.Huesped;
import gz.hoteles.repositories.HuespedRepository;

@RestController
@RequestMapping("/huespedes")
public class HuespedController {
    
    @Autowired
    HuespedRepository huespedRepository;

    @GetMapping()
    public List<Huesped> list() {
        return huespedRepository.findAll();
    }

    @GetMapping("/{id}")
    public Huesped get(@PathVariable(name = "id") int id) {
        return huespedRepository.findById(id).get();
    }

    @GetMapping("/filteredByName")
    public List<Huesped> getHuespedesByNombre(@RequestParam String nombre) {
        return huespedRepository.getHuespedesByNombre(nombre);
    }

    @GetMapping("/filteredByDni")
    public List<Huesped> getHuespedesByDni(@RequestParam String dni) {
        return huespedRepository.getHuespedesByDni(dni);
    }

    @GetMapping("/filteredByEmail")
    public List<Huesped> getHuespedesByEmail(@RequestParam String email) {
        return huespedRepository.getHuespedesByEmail(email);
    }

    @GetMapping("/filteredByCheckInDate")
    public List<Huesped> getHuespedesByFechaEntrada(@RequestParam Date fecha) {
        return huespedRepository.getHuespedesByFechaEntrada(fecha);
    }

    @GetMapping("/filteredByCheckOutDate")
    public List<Huesped> getHuespedesByFechaSalida(@RequestParam Date fecha) {
        return huespedRepository.getHuespedesByFechaSalida(fecha);
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
            return ResponseEntity.badRequest().body("El nombre y el dni son obligatorios");
        }
        Huesped save = huespedRepository.save(input);
        return ResponseEntity.ok(save);
    }

     @DeleteMapping("/{id}")   
    public ResponseEntity<?> delete(@PathVariable(name = "id") int id) {
          Optional<Huesped> findById = huespedRepository.findById(id);   
        if(findById.get() != null){               
            huespedRepository.delete(findById.get());  
        }
        return ResponseEntity.ok().build();
    }

}
