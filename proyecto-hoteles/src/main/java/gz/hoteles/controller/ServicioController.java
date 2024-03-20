package gz.hoteles.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
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

import gz.hoteles.dto.ServicioDTO;
import gz.hoteles.entities.CategoriaServicio;
import gz.hoteles.entities.JSONMapper;
import gz.hoteles.entities.Servicio;
import gz.hoteles.repositories.ServicioRepository;

@RestController
@RequestMapping("/servicios")
public class ServicioController {
    
    @Autowired
    ServicioRepository servicioRepository;

    private static final ModelMapper modelMapper = new ModelMapper();

    @GetMapping()
    public ResponseEntity<?> list() {
        List<Servicio> servicios = servicioRepository.findAll();
        List<ServicioDTO> serviciosDTO = convertToDtoServicioList(servicios);
        if (serviciosDTO.size() > 0) {
            return ResponseEntity.ok(serviciosDTO);
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
        } else return ResponseEntity.ok(convertToDtoServicio(servicio));
    }

    @GetMapping("/filteredByName")
    public ResponseEntity<?> getServicioByNombre(@RequestParam String nombre, @RequestParam int pages) {
        if (nombre == null || nombre.isEmpty()) {
            throw new IllegalArgumentException("El parámetro 'nombre' no puede estar vacío");
        }
        Pageable pageable = PageRequest.of(0, pages);
        Page<Servicio> page = servicioRepository.getServicioByNombre(nombre, pageable);
        List<Servicio> servicios = page.getContent();
        List<ServicioDTO> serviciosDTO = convertToDtoServicioList(servicios);
        if (serviciosDTO.size() > 0) {
            return ResponseEntity.ok(serviciosDTO);
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró ningún hotel por el nombre '" + nombre + "'");
    }

    @GetMapping("/filteredByCategory")
    public ResponseEntity<?> getServicioByCategoria(@RequestParam CategoriaServicio categoria, @RequestParam int pages) {
        if (categoria == null) {
            throw new IllegalArgumentException("El parámetro 'categoría' no puede ser nulo");
        }
        Pageable pageable = PageRequest.of(0, pages);
        Page<Servicio> page = servicioRepository.getServicioByCategoria(categoria, pageable);
        List<Servicio> servicios = page.getContent();
        List<ServicioDTO> serviciosDTO = convertToDtoServicioList(servicios);
        if (serviciosDTO.size() > 0) {
            return ResponseEntity.ok(serviciosDTO);
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró ningún servicio por la categoría '" + categoria + "'");
    }

    @GetMapping("/filteredByDescription")
    public ResponseEntity<?> getServicioByDescripcion(@RequestParam String descripcion, @RequestParam int pages) {
        if (descripcion == null || descripcion.isEmpty()) {
            throw new IllegalArgumentException("El parámetro 'descripción' no puede estar vacío");
        }
        Pageable pageable = PageRequest.of(0, pages);
        Page<Servicio> page = servicioRepository.getServicioByDescripcion(descripcion, pageable);
        List<Servicio> servicios = page.getContent();
        List<ServicioDTO> serviciosDTO = convertToDtoServicioList(servicios);
        if (serviciosDTO.size() > 0) {
            return ResponseEntity.ok(serviciosDTO);
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró ningún servicio por la descripción '" + descripcion + "'");
    }

    @PostMapping("/dynamicSearch")
    public ResponseEntity<?> getHabitacionesFilteredByParam(@RequestBody JSONMapper json) {
        if (json == null || json.getField() == null || json.getPages() <= 0
                || json.getSortBy() == null) {
            throw new IllegalArgumentException("Falta uno o más campos requeridos en el JSON");
        }

        String field = json.getField();
        String value = json.getValue();
        String sortDirection = json.getSortDirection().equalsIgnoreCase("asc") ? "ASC" : "DESC";
        String sortByField = json.getSortBy(); // comprobar que recibe un string correcto
        if (!sortByField.equalsIgnoreCase("nombre") && !sortByField.equalsIgnoreCase("descripcion") && !sortByField.equalsIgnoreCase("categoria")) {
            throw new IllegalArgumentException("No se puede ordenar por " + sortByField);
        }


        Page<Servicio> page = switch (field) {
            case "nombre" -> servicioRepository.findByNombreContainingIgnoreCase(value, PageRequest.of(0, json.getPages(), Sort.by(Sort.Direction.fromString(sortDirection), sortByField)));
            case "descripcion" -> servicioRepository.findByDescripcionEquals(value, PageRequest.of(0, json.getPages(), Sort.by(Sort.Direction.fromString(sortDirection), sortByField)));
            case "categoria" -> {
                switch (value.toUpperCase()) {
                    case "GIMNASIO":
                        yield servicioRepository.findByCategoriaEquals(CategoriaServicio.GIMNASIO, PageRequest.of(0, json.getPages(), Sort.by(Sort.Direction.fromString(sortDirection), sortByField)));
                    case "LAVANDERIA":
                        yield servicioRepository.findByCategoriaEquals(CategoriaServicio.LAVANDERIA, PageRequest.of(0, json.getPages(), Sort.by(Sort.Direction.fromString(sortDirection), sortByField)));
                    case "BAR":
                        yield servicioRepository.findByCategoriaEquals(CategoriaServicio.BAR, PageRequest.of(0, json.getPages(), Sort.by(Sort.Direction.fromString(sortDirection), sortByField)));
                    case "CASINO":
                        yield servicioRepository.findByCategoriaEquals(CategoriaServicio.CASINO, PageRequest.of(0, json.getPages(), Sort.by(Sort.Direction.fromString(sortDirection), sortByField)));
                    case "KARAOKE":
                        yield servicioRepository.findByCategoriaEquals(CategoriaServicio.KARAOKE, PageRequest.of(0, json.getPages(), Sort.by(Sort.Direction.fromString(sortDirection), sortByField)));
                    default:
                        throw new IllegalArgumentException("Valor de categoría no válido");
                }
            }
            default -> throw new IllegalArgumentException("No se puede filtrar por '" + field + "'");
        };

        List<Servicio> servicios = page.getContent();
        List<ServicioDTO> serviciosDTO = convertToDtoServicioList(servicios);
        if (serviciosDTO.size() > 0) {
            return ResponseEntity.ok(serviciosDTO);
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
           return ResponseEntity.ok(convertToDtoServicio(save));
    }

    @PostMapping
    public ResponseEntity<?> post(@RequestBody Servicio input) {
        if (input.getNombre() == null || input.getNombre().isEmpty() || input.getCategoria() == null) {
            throw new IllegalArgumentException("Los campos 'nombre' y 'categoria' son obligatorios");
        }
        Servicio save = servicioRepository.save(input);
        return ResponseEntity.ok(convertToDtoServicio(save));
    }

    @DeleteMapping("/{id}")   
    public ResponseEntity<?> delete(@PathVariable(name = "id") int id) {
        Servicio findById = servicioRepository.findById(id).orElse(null);   
        if(findById != null){               
            servicioRepository.delete(findById);  
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró ningun servicio por el ID proporcionado");
        return ResponseEntity.ok().build();
    }

    /*====== MAPPER ====== */

    public static ServicioDTO convertToDtoServicio(Servicio servicio) {
        return modelMapper.map(servicio, ServicioDTO.class);
    }

    public static List<ServicioDTO> convertToDtoServicioList(List<Servicio> servicios) {
        return servicios.stream()
                        .map(servicio -> convertToDtoServicio(servicio))
                        .collect(Collectors.toList());
    }
    

}
