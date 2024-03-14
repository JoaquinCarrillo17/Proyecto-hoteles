package gz.hoteles.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

import gz.hoteles.entities.Habitacion;
import gz.hoteles.entities.Huesped;
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
    public List<Habitacion> list() {
        return habitacionRepository.findAll();
    }

    @GetMapping("/{id}")
    public Habitacion get(@PathVariable(name = "id") int id) {
        return habitacionRepository.findById(id).get();
    }

    @GetMapping("/filteredByNumber")
    public List<Habitacion> getHabitacionesByNumero(@RequestParam String numero, @RequestParam int pages) {
        Pageable pageable = PageRequest.of(0, pages);
        Page<Habitacion> page = habitacionRepository.getHabitacionesByNumero(numero, pageable);
        return page.getContent();
    }

    @GetMapping("/filteredByTypeOfRoom")
    public List<Habitacion> getHabitacionesByTipoHabitacion(@RequestParam TipoHabitacion tipo, @RequestParam int pages) {
        Pageable pageable = PageRequest.of(0, pages);
        Page<Habitacion> page = habitacionRepository.getHabitacionesByTipoHabitacion(tipo, pageable);
        return page.getContent();
    }

    @GetMapping("/filteredByPricePerNight")
    public List<Habitacion> getHabitacionesByPrecioPorNoche(@RequestParam String precio, @RequestParam int pages) {
        Pageable pageable = PageRequest.of(0, pages);
        Page<Habitacion> page = habitacionRepository.getHabitacionesByPrecioPorNoche(precio, pageable);
        return page.getContent();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable(name = "id") int id, @RequestBody Habitacion input) {
        Habitacion find = habitacionRepository.findById(id).get();   
        if(find != null){     
            find.setNumero(input.getNumero());
            find.setPrecioNoche(input.getPrecioNoche());
            find.setTipoHabitacion(input.getTipoHabitacion());
        }
        Habitacion save = habitacionRepository.save(find);
           return ResponseEntity.ok(save);
    }

    @PostMapping
    public ResponseEntity<?> post(@RequestBody Habitacion input) {
        if (input.getTipoHabitacion() == null) {
            return ResponseEntity.badRequest().body("Debes introducir el tipo de habitación");
        }
        Habitacion save = servicioHoteles.crearHabitacion(input);
        return ResponseEntity.ok(save);
    }

    @PostMapping("/{id}/huespedes")
    public ResponseEntity<?> anadirHuesped(@PathVariable(name = "id") int id, @RequestParam Huesped huesped) {
        Habitacion h = servicioHoteles.anadirHuesped(id, huesped);
        if (h == null) {
            return ResponseEntity.badRequest().build(); //CAMBIAR
        } else return ResponseEntity.ok(h);
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
