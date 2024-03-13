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

import gz.hoteles.entities.Habitacion;
import gz.hoteles.repositories.HabitacionRepository;

@RestController
@RequestMapping("/habitaciones")
public class HabitacionController {
    
    @Autowired
    HabitacionRepository habitacionRepository;

    @GetMapping()
    public List<Habitacion> list() {
        return habitacionRepository.findAll();
    }

    @GetMapping("/{id}")
    public Habitacion get(@PathVariable(name = "id") int id) {
        return habitacionRepository.findById(id).get();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable(name = "id") int id, @RequestBody Habitacion input) {
        Habitacion find = habitacionRepository.findById(id).get();   
        if(find != null){     
            find.setIdHotel(input.getIdHotel()); // TODO cuidado
            find.setNumero(input.getNumero());
            find.setPrecioNoche(input.getPrecioNoche());
            find.setTipoHabitacion(input.getTipoHabitacion());
        }
        Habitacion save = habitacionRepository.save(find);
           return ResponseEntity.ok(save);
    }

    @PostMapping
    public ResponseEntity<?> post(@RequestBody Habitacion input) {
        Habitacion save = habitacionRepository.save(input);
        return ResponseEntity.ok(save);
    }

     @DeleteMapping("/{id}")   
    public ResponseEntity<?> delete(@PathVariable(name = "id") int id) {
          Optional<Habitacion> findById = habitacionRepository.findById(id);   
        if(findById.get() != null){               
            habitacionRepository.delete(findById.get());  
        }
        return ResponseEntity.ok().build();
    }

}
