package gz.hoteles.controller;

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

    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable(name = "id") int id, @RequestBody Huesped input) {
        Huesped find = huespedRepository.findById(id).get();   
        if(find != null){     
            find.setNombre(input.getNombre());
            find.setApellido(input.getApellido());
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
