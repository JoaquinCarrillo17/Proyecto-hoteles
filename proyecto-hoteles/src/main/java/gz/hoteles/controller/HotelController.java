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
import gz.hoteles.entities.Hotel;
import gz.hoteles.entities.JSONMapper;
import gz.hoteles.entities.Servicio;
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
    public ResponseEntity<?> list() {
        List<Hotel> hoteles = hotelRepository.findAll();
        if (hoteles.size() > 0) {
            return ResponseEntity.ok(hoteles);
        } else throw new ResponseStatusException(HttpStatus.NO_CONTENT);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable(name = "id") int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser un número entero positivo");
        } 
        Hotel hotel = hotelRepository.findById(id).orElse(null);
        if (hotel == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró ningún hotel por el ID proporcionado");
        } else return ResponseEntity.ok(hotel);
    }

    @GetMapping("/filteredByName")
    public ResponseEntity<?> getHotelByNombre(@RequestParam String nombre, @RequestParam int pages) {
        if (nombre == null || nombre.isEmpty()) {
            throw new IllegalArgumentException("El parámetro 'nombre' no puede estar vacío");
        }
        Pageable pageable = PageRequest.of(0, pages);
        Page<Hotel> page = hotelRepository.getHotelByNombre(nombre, pageable);
        List<Hotel> hoteles = page.getContent();
        if (hoteles.size() > 0) {
            return ResponseEntity.ok(hoteles);
        } else throw new ResponseStatusException(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/filteredByAddress")
    public ResponseEntity<?> getHotelByDireccion(@RequestParam String direccion, @RequestParam int pages) {
        if (direccion == null || direccion.isEmpty()) {
            throw new IllegalArgumentException("El parámetro 'nombre' no puede estar vacío");
        }
        Pageable pageable = PageRequest.of(0, pages);
        Page<Hotel> page = hotelRepository.getHotelByDireccion(direccion, pageable);
        List<Hotel> hoteles = page.getContent();
        if (hoteles.size() > 0) {
            return ResponseEntity.ok(hoteles);
        } else throw new ResponseStatusException(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/filteredByPhoneNumber")
    public ResponseEntity<?> getHotelByTelefono(@RequestParam String telefono, @RequestParam int pages) {
        if (telefono == null || telefono.isEmpty()) {
            throw new IllegalArgumentException("El parámetro 'nombre' no puede estar vacío");
        }
        Pageable pageable = PageRequest.of(0, pages);
        Page<Hotel> page = hotelRepository.getHotelByTelefono(telefono, pageable);
        List<Hotel> hoteles = page.getContent();
        if (hoteles.size() > 0) {
            return ResponseEntity.ok(hoteles);
        } else throw new ResponseStatusException(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/filteredByEmail")
    public ResponseEntity<?> getHotelByEmail(@RequestParam String email, @RequestParam int pages) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("El parámetro 'nombre' no puede estar vacío");
        }
        Pageable pageable = PageRequest.of(0, pages);
        Page<Hotel> page = hotelRepository.getHotelByEmail(email, pageable);
        List<Hotel> hoteles = page.getContent();
        if (hoteles.size() > 0) {
            return ResponseEntity.ok(hoteles);
        } else throw new ResponseStatusException(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/filteredByWebsite")
    public ResponseEntity<?> getHotelBySitioWeb(@RequestParam String sitioWeb, @RequestParam int pages) {
        if (sitioWeb == null || sitioWeb.isEmpty()) {
            throw new IllegalArgumentException("El parámetro 'nombre' no puede estar vacío");
        }
        Pageable pageable = PageRequest.of(0, pages);
        Page<Hotel> page = hotelRepository.getHotelBySitioWeb(sitioWeb, pageable);
        List<Hotel> hoteles = page.getContent();
        if (hoteles.size() > 0) {
            return ResponseEntity.ok(hoteles);
        } else throw new ResponseStatusException(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/dynamicSearch")
    public ResponseEntity<?> getHotelesFilteredByParam(@RequestBody JSONMapper json) {
        if (json == null || json.getField() == null || json.getPages() <= 0
                || json.getSortBy() == null) {
            throw new IllegalArgumentException("Falta uno o más campos requeridos en el JSON");
        }

        String field = json.getField();
        String value = json.getValue();
        String sortDirection = json.getSortBy().equalsIgnoreCase("asc") ? "ASC" : "DESC";

        Page<Hotel> page = switch (field) {
            case "sitioWeb" -> hotelRepository.findBySitioWebEquals(value, PageRequest.of(0, json.getPages(), Sort.by(Sort.Direction.fromString(sortDirection), "id")));
            case "email" -> hotelRepository.findByEmailEquals(value, PageRequest.of(0, json.getPages(), Sort.by(Sort.Direction.fromString(sortDirection), "id")));
            case "telefono" -> hotelRepository.findByTelefonoEquals(value, PageRequest.of(0, json.getPages(), Sort.by(Sort.Direction.fromString(sortDirection), "id")));
            case "direccion" -> hotelRepository.findByDireccionEquals(value, PageRequest.of(0, json.getPages(), Sort.by(Sort.Direction.fromString(sortDirection), "id")));
            case "nombre" -> hotelRepository.findByNombreEquals(value, PageRequest.of(0, json.getPages(), Sort.by(Sort.Direction.fromString(sortDirection), "id")));
            default -> throw new IllegalArgumentException("El campo proporcionado en el JSON no es válido");
        };

        List<Hotel> hoteles = page.getContent();
        if (hoteles.size() > 0) {
            return ResponseEntity.ok(hoteles);
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron hoteles con " + json.getField() + " = " + json.getValue());
        
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable(name = "id") int id, @RequestBody Hotel input) {
        Hotel find = hotelRepository.findById(id).orElse(null);   
        if(find != null){     
            find.setDireccion(input.getDireccion());
            find.setEmail(input.getEmail());
            find.setNombre(input.getNombre());
            find.setTelefono(input.getTelefono());
            find.setSitioWeb(input.getSitioWeb());
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró ningún hotel con el ID proporcionado");
        Hotel save = hotelRepository.save(find);
           return ResponseEntity.ok(save);
    }
    
    @PostMapping
    public ResponseEntity<?> post(@RequestBody Hotel input) { //Los campos nombre y direccion seran obligatorios
        if (input.getNombre() == null || input.getNombre().isEmpty() ||
                input.getDireccion() == null || input.getDireccion().isEmpty()) {
                    throw new IllegalArgumentException("Los campos 'nombre' y 'direccion' son obligatorios");
        }
        Hotel save = servicioHoteles.crearHotel(input);
        return ResponseEntity.ok(save);
    }

    @PostMapping("/{id}/servicios")
    public ResponseEntity<?> anadirServicio(@PathVariable(name = "id") int id, @RequestBody Servicio input) {
        Hotel h = servicioHoteles.anadirServicio(id, input); //Comprobacion NOT_FOUND en la funcion
        return ResponseEntity.ok(h);
    }

    @PostMapping("/{id}/habitaciones")
    public ResponseEntity<?> anadirHabitacion(@PathVariable(name = "id") int id, @RequestBody Habitacion input) {
        Hotel h = servicioHoteles.anadirHabitacion(id, input); //Comprobacion NOT_FOUND en la funcion
        return ResponseEntity.ok(h);
    }
    
    @DeleteMapping("/{id}")   
    public ResponseEntity<?> delete(@PathVariable(name = "id") int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser un número entero positivo");
        } 
        Hotel findById = hotelRepository.findById(id).orElse(null);   
        if(findById != null){               
            hotelRepository.delete(findById);  
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró ningún hotel por el ID proporcionado");
        return ResponseEntity.ok().build();
    }
    

}
