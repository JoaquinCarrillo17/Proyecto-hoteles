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

import gz.hoteles.entities.Hotel;
import gz.hoteles.repositories.HotelRepository;

@RestController
@RequestMapping("/hoteles")
public class HotelController {
    
    @Autowired
    HotelRepository hotelRepository;

    @GetMapping()
    public List<Hotel> list() {
        return hotelRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public Hotel get(@PathVariable(name = "id") int id) {
        return hotelRepository.findById(id).get();
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable(name = "id") int id, @RequestBody Hotel input) {
        Hotel find = hotelRepository.findById(id).get();   
        if(find != null){     
            find.setDireccion(input.getDireccion());
            find.setEmail(input.getEmail());
            find.setNombre(input.getNombre());
            find.setTelefono(input.getTelefono());
            find.setSitioWeb(input.getSitioWeb());
        }
        Hotel save = hotelRepository.save(find);
           return ResponseEntity.ok(save);
    }
    
    @PostMapping
    public ResponseEntity<?> post(@RequestBody Hotel input) {
        Hotel save = hotelRepository.save(input);
        return ResponseEntity.ok(save);
    }
    
    @DeleteMapping("/{id}")   
    public ResponseEntity<?> delete(@PathVariable(name = "id") int id) {
          Optional<Hotel> findById = hotelRepository.findById(id);   
        if(findById.get() != null){               
            hotelRepository.delete(findById.get());  
        }
        return ResponseEntity.ok().build();
    }
    

}
