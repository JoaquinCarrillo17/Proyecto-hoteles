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

import gz.hoteles.entities.Servicio;
import gz.hoteles.repositories.ServicioRepository;

@RestController
@RequestMapping("/servicios")
public class ServicioController {
    
    @Autowired
    ServicioRepository servicioRepository;

    @GetMapping()
    public List<Servicio> list() {
        return servicioRepository.findAll();
    }

    @GetMapping("/{id}")
    public Servicio get(@PathVariable(name = "id") int id) {
        return servicioRepository.findById(id).get();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable(name = "id") int id, @RequestBody Servicio input) {
        Servicio find = servicioRepository.findById(id).get();   
        if(find != null){     
            find.setCategoria(input.getCategoria());
            find.setNombre(input.getNombre());
            find.setDescripcion(input.getDescripcion());
        }
        Servicio save = servicioRepository.save(find);
           return ResponseEntity.ok(save);
    }

    @PostMapping
    public ResponseEntity<?> post(@RequestBody Servicio input) {
        Servicio save = servicioRepository.save(input);
        return ResponseEntity.ok(save);
    }

     @DeleteMapping("/{id}")   
    public ResponseEntity<?> delete(@PathVariable(name = "id") int id) {
          Optional<Servicio> findById = servicioRepository.findById(id);   
        if(findById.get() != null){               
            servicioRepository.delete(findById.get());  
        }
        return ResponseEntity.ok().build();
    }
    

}
