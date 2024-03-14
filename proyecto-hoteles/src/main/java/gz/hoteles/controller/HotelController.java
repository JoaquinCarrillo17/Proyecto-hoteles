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

import gz.hoteles.entities.Hotel;
import gz.hoteles.repositories.HotelRepository;
import gz.hoteles.servicio.IServicioHoteles;

@RestController
@RequestMapping("/hoteles")
public class HotelController {
    
    @Autowired
    HotelRepository hotelRepository;
    @Autowired
    IServicioHoteles servicioHoteles;

    @GetMapping()
    public List<Hotel> list() {
        return hotelRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public Hotel get(@PathVariable(name = "id") int id) {
        return hotelRepository.findById(id).get();
    }

    @GetMapping("/filteredByName")
    public List<Hotel> getHotelByNombre(@RequestParam String nombre, @RequestParam int pages) {
        Pageable pageable = PageRequest.of(0, pages);
        Page<Hotel> page = hotelRepository.getHotelByNombre(nombre, pageable);
        return page.getContent();
    }

    @GetMapping("/filteredByAddress")
    public List<Hotel> getHotelByDirecion(@RequestParam String direccion, @RequestParam int pages) {
        Pageable pageable = PageRequest.of(0, pages);
        Page<Hotel> page = hotelRepository.getHotelByDireccion(direccion, pageable);
        return page.getContent();
    }

    @GetMapping("/filteredByPhoneNumber")
    public List<Hotel> getHotelByTelefono(@RequestParam String telefono, @RequestParam int pages) {
        Pageable pageable = PageRequest.of(0, pages);
        Page<Hotel> page = hotelRepository.getHotelByTelefono(telefono, pageable);
        return page.getContent();
    }

    @GetMapping("/filteredByEmail")
    public List<Hotel> getHotelByEmail(@RequestParam String email, @RequestParam int pages) {
        Pageable pageable = PageRequest.of(0, pages);
        Page<Hotel> page = hotelRepository.getHotelByEmail(email, pageable);
        return page.getContent();
    }

    @GetMapping("/filteredByWebsite")
    public List<Hotel> getHotelBySitioWeb(@RequestParam String sitioWeb, @RequestParam int pages) {
        Pageable pageable = PageRequest.of(0, pages);
        Page<Hotel> page = hotelRepository.getHotelBySitioWeb(sitioWeb, pageable);
        return page.getContent();
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
    public ResponseEntity<?> post(@RequestBody Hotel input) { //Los campos nombre y direccion seran obligatorios
        if (input.getNombre() == null || input.getNombre().isEmpty() ||
                input.getDireccion() == null || input.getDireccion().isEmpty()) {
            return ResponseEntity.badRequest().body("Los campos 'nombre' y 'direccion' son obligatorios");
        }
        Hotel save = servicioHoteles.crearHotel(input);
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
