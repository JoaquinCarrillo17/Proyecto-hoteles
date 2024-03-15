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

import gz.hoteles.entities.CategoriaServicio;
import gz.hoteles.entities.JSONMapper;
import gz.hoteles.entities.Servicio;
import gz.hoteles.repositories.ServicioRepository;

@RestController
@RequestMapping("/servicios")
public class ServicioController {
    
    @Autowired
    ServicioRepository servicioRepository;

    @GetMapping()
    public ResponseEntity<?> list() {
        List<Servicio> servicios = servicioRepository.findAll();
        if (servicios.size() > 0) {
            return ResponseEntity.ok(servicios);
        } else throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No se ha encontrado ningún servicio");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable(name = "id") int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser un número entero positivo");
        } 
        Servicio servicio = servicioRepository.findById(id).orElse(null);
        if (servicio == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró ningún servicio con el ID proporcionado");
        } else return ResponseEntity.ok(servicio);
    }

    @GetMapping("/filteredByName")
    public ResponseEntity<?> getServicioByNombre(@RequestParam String nombre, @RequestParam int pages) {
        if (nombre == null || nombre.isEmpty()) {
            throw new IllegalArgumentException("El parámetro 'nombre' no puede estar vacío");
        }
        Pageable pageable = PageRequest.of(0, pages);
        Page<Servicio> page = servicioRepository.getServicioByNombre(nombre, pageable);
        List<Servicio> servicios = page.getContent();
        if (servicios.size() > 0) {
            return ResponseEntity.ok(servicios);
        } else throw new ResponseStatusException(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/filteredByCategory")
    public ResponseEntity<?> getServicioByCategoria(@RequestParam CategoriaServicio categoria, @RequestParam int pages) {
        if (categoria == null) {
            throw new IllegalArgumentException("El parámetro 'categoría' no puede ser nulo");
        }
        Pageable pageable = PageRequest.of(0, pages);
        Page<Servicio> page = servicioRepository.getServicioByCategoria(categoria, pageable);
        List<Servicio> servicios = page.getContent();
        if (servicios.size() > 0) {
            return ResponseEntity.ok(servicios);
        } else throw new ResponseStatusException(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/filteredByDescription")
    public ResponseEntity<?> getServicioByDescripcion(@RequestParam String descripcion, @RequestParam int pages) {
        if (descripcion == null || descripcion.isEmpty()) {
            throw new IllegalArgumentException("El parámetro 'descripción' no puede estar vacío");
        }
        Pageable pageable = PageRequest.of(0, pages);
        Page<Servicio> page = servicioRepository.getServicioByDescripcion(descripcion, pageable);
        List<Servicio> servicios = page.getContent();
        if (servicios.size() > 0) {
            return ResponseEntity.ok(servicios);
        } else throw new ResponseStatusException(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/dynamicSearch")
    public ResponseEntity<?> getHabitacionesFilteredByParam(@RequestBody JSONMapper json) {
        if (json == null || json.getField() == null || json.getPages() <= 0
                || json.getSortBy() == null) {
            throw new IllegalArgumentException("Falta uno o más campos requeridos en el JSON");
        }

        String field = json.getField();
        String value = json.getValue();
        String sortDirection = json.getSortBy().equalsIgnoreCase("asc") ? "ASC" : "DESC";

        Page<Servicio> page = switch (field) {
            case "nombre" -> servicioRepository.findByNombreEquals(value, PageRequest.of(0, json.getPages(), Sort.by(Sort.Direction.fromString(sortDirection), "id")));
            case "descripcion" -> servicioRepository.findByDescripcionEquals(value, PageRequest.of(0, json.getPages(), Sort.by(Sort.Direction.fromString(sortDirection), "id")));
            case "categoria" -> servicioRepository.findByCategoriaEquals(value, PageRequest.of(0, json.getPages(), Sort.by(Sort.Direction.fromString(sortDirection), "id")));
            default -> throw new IllegalArgumentException("El campo proporcionado en el JSON no es válido");
        };

        List<Servicio> servicios = page.getContent();
        if (servicios.size() > 0) {
            return ResponseEntity.ok(servicios);
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron hoteles con " + json.getField() + " = " + json.getValue());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable(name = "id") int id, @RequestBody Servicio input) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser un número entero positivo");
        }
        Servicio find = servicioRepository.findById(id).orElse(null);   
        if(find != null){     
            find.setCategoria(input.getCategoria());
            find.setNombre(input.getNombre());
            find.setDescripcion(input.getDescripcion());
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró ningun servicio por el ID proporcionado");
        Servicio save = servicioRepository.save(find);
           return ResponseEntity.ok(save);
    }

    @PostMapping
    public ResponseEntity<?> post(@RequestBody Servicio input) {
        if (input.getNombre() == null || input.getNombre().isEmpty() || input.getCategoria() == null) {
            throw new IllegalArgumentException("Los campos 'nombre' y 'categoria' son obligatorios");
        }
        Servicio save = servicioRepository.save(input);
        return ResponseEntity.ok(save);
    }

    @DeleteMapping("/{id}")   
    public ResponseEntity<?> delete(@PathVariable(name = "id") int id) {
        Servicio findById = servicioRepository.findById(id).orElse(null);   
        if(findById != null){               
            servicioRepository.delete(findById);  
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró ningun servicio por el ID proporcionado");
        return ResponseEntity.ok().build();
    }
    

}
