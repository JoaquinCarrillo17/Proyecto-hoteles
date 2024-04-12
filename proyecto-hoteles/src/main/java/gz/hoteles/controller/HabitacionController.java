package gz.hoteles.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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

import gz.hoteles.dto.HabitacionDTO;
import gz.hoteles.entities.Habitacion;
import gz.hoteles.entities.Huesped;
import gz.hoteles.entities.TipoHabitacion;
import gz.hoteles.repositories.HabitacionRepository;
import gz.hoteles.servicio.IServicioHoteles;
import gz.hoteles.support.ListOrderCriteria;
import gz.hoteles.support.SearchCriteria;
import gz.hoteles.support.SearchRequest;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/habitaciones")
public class HabitacionController {

    @Autowired
    HabitacionRepository habitacionRepository;
    @Autowired
    IServicioHoteles servicioHoteles;

    private static final ModelMapper modelMapper = new ModelMapper();

    @GetMapping()
    public ResponseEntity<?> list() {
        List<Habitacion> habitaciones = habitacionRepository.findAll();
        List<HabitacionDTO> habitacionDTO = convertToDtoHabitacionList(habitaciones);
        if (habitacionDTO.size() > 0) {
            return ResponseEntity.ok(habitacionDTO);
        } else
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No se ha encontrado ninguna habitación");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable(name = "id") int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser un número entero positivo");
        }
        Habitacion habitacion = habitacionRepository.findById(id).orElse(null);
        if (habitacion == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se encontró ninguna habitación con el ID proporcionado");
        } else
            return ResponseEntity.ok(convertToDtoHabitacion(habitacion));
    }

    @GetMapping("/{id}/full")
    public ResponseEntity<?> getHabitacionFull(@PathVariable(name = "id") int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser un número entero positivo");
        }
        Habitacion habitacion = habitacionRepository.findById(id).orElse(null);
        if (habitacion == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se encontró ninguna habitación con el ID proporcionado");
        } else
            return ResponseEntity.ok(habitacion);
    }

    @GetMapping("/filteredByNumber")
    public ResponseEntity<?> getHabitacionesByNumero(@RequestParam String numero, @RequestParam int pages) {
        if (numero == null || numero.isEmpty()) {
            throw new IllegalArgumentException("El parámetro 'número' no puede estar vacío");
        }
        Pageable pageable = PageRequest.of(0, pages);
        Page<Habitacion> page = habitacionRepository.getHabitacionesByNumero(numero, pageable);
        List<Habitacion> habitaciones = page.getContent();
        List<HabitacionDTO> habitacionDTO = convertToDtoHabitacionList(habitaciones);
        if (habitacionDTO.size() > 0) {
            return ResponseEntity.ok(habitacionDTO);
        } else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se encontró ninguna habitación con el número '" + numero + "'");
    }

    @GetMapping("/filteredByTypeOfRoom")
    public ResponseEntity<?> getHabitacionesByTipoHabitacion(@RequestParam TipoHabitacion tipo,
            @RequestParam int pages) {
        if (tipo == null) {
            throw new IllegalArgumentException("El parámetro 'tipo' no puede ser nulo");
        }
        Pageable pageable = PageRequest.of(0, pages);
        Page<Habitacion> page = habitacionRepository.getHabitacionesByTipoHabitacion(tipo, pageable);
        List<Habitacion> habitaciones = page.getContent();
        List<HabitacionDTO> habitacionDTO = convertToDtoHabitacionList(habitaciones);
        if (habitacionDTO.size() > 0) {
            return ResponseEntity.ok(habitacionDTO);
        } else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se encontró ninguna habitación del tipo '" + tipo + "'");
    }

    @GetMapping("/filteredByPricePerNight")
    public ResponseEntity<?> getHabitacionesByPrecioPorNoche(@RequestParam String precio, @RequestParam int pages) {
        if (precio == null || precio.isEmpty()) {
            throw new IllegalArgumentException("El parámetro 'precio' no puede ser nulo.");
        }
        Pageable pageable = PageRequest.of(0, pages);
        Page<Habitacion> page = habitacionRepository.getHabitacionesByPrecioPorNoche(precio, pageable);
        List<Habitacion> habitaciones = page.getContent();
        List<HabitacionDTO> habitacionDTO = convertToDtoHabitacionList(habitaciones);
        if (habitacionDTO.size() > 0) {
            return ResponseEntity.ok(habitacionDTO);
        } else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se encontró ninguna habitación con precio '" + precio + "€'");
    }

    @PostMapping("/dynamicSearch")
    public ResponseEntity<?> getFilteredByDynamicSearch(@RequestBody SearchRequest searchRequest) {
        if (searchRequest == null || searchRequest.getListSearchCriteria() == null
                || searchRequest.getListSearchCriteria().isEmpty()
                || searchRequest.getPage() == null || searchRequest.getPage().getPageSize() <= 0
                || searchRequest.getPage().getPageIndex() < 0) {
            throw new IllegalArgumentException("Falta uno o más campos requeridos en el JSON");
        }

        List<SearchCriteria> searchCriteriaList = searchRequest.getListSearchCriteria();
        ListOrderCriteria orderCriteriaList = searchRequest.getListOrderCriteria();
        int pageSize = searchRequest.getPage().getPageSize();
        int pageIndex = searchRequest.getPage().getPageIndex();

        Specification<Habitacion> spec = Specification.where(null);
        for (SearchCriteria criteria : searchCriteriaList) {
            switch (criteria.getOperation()) {
                case "equals":
                    spec = spec.and((root, query, cb) -> cb.equal(root.get(criteria.getKey()), criteria.getValue()));
                    break;
                case "not equals":
                    spec = spec.and((root, query, cb) -> cb.notEqual(root.get(criteria.getKey()), criteria.getValue()));
                    break;
                case "contains":
                    spec = spec.and(
                            (root, query, cb) -> cb.like(root.get(criteria.getKey()), "%" + criteria.getValue() + "%"));
                    break;
                case "greater than":
                    spec = spec
                            .and((root, query, cb) -> cb.greaterThan(root.get(criteria.getKey()), criteria.getValue()));
                    break;
                case "less than":
                    spec = spec.and((root, query, cb) -> cb.lessThan(root.get(criteria.getKey()), criteria.getValue()));
                    break;
                case "greater or equals than":
                    spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get(criteria.getKey()),
                            criteria.getValue()));
                    break;
                case "less or equals than":
                    spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get(criteria.getKey()),
                            criteria.getValue()));
                    break;
                default:
                    throw new IllegalArgumentException("Operador de búsqueda no válido: " + criteria.getOperation());
            }
        }

        String sortByField = orderCriteriaList.getSortBy();
        String sortDirection = orderCriteriaList.getValueSortOrder().equalsIgnoreCase("asc") ? "ASC" : "DESC";
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortByField);

        Page<Habitacion> page = habitacionRepository.findAll(spec, PageRequest.of(pageIndex, pageSize, sort));

        List<Habitacion> habitaciones = page.getContent();
        List<HabitacionDTO> habitacionDTO = convertToDtoHabitacionList(habitaciones);
        if (habitacionDTO.size() > 0) {
            return ResponseEntity.ok(habitacionDTO);
        } else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se encontraron habitaciones por los parametros proporcionados");

    }

    @Operation(summary = "Filtrado de habitaciones por todos sus parámetros a través de un solo String")
    @GetMapping("/magicFilter")
    public ResponseEntity<?> getHabitacionesByMagicFilter(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam("pageNumber") int pagina,
            @RequestParam("itemsPerPage") int itemsPorPagina) {

        Pageable pageable = PageRequest.of(pagina, itemsPorPagina);

        Page<Habitacion> page;
        if (query == null || query.isEmpty()) {
            page = habitacionRepository.findAll(pageable);
        } else {

            // Intenta convertir el valor de query a TipoHabitacion
            TipoHabitacion tipoHabitacion = null;
            switch (query.toUpperCase()) {
                case "INDIVIDUAL":
                    tipoHabitacion = TipoHabitacion.INDIVIDUAL;
                    break;
                case "DOBLE":
                    tipoHabitacion = TipoHabitacion.DOBLE;
                    break;
                case "CUADRUPLE":
                    tipoHabitacion = TipoHabitacion.CUADRUPLE;
                    break;
                case "SUITE":
                    tipoHabitacion = TipoHabitacion.SUITE;
                    break;
                default:
                    // Si no coincide con ningún tipo de habitación, se ignora y se busca con la
                    // cadena directamente
            }

            if (tipoHabitacion != null) {
                page = habitacionRepository
                        .findByNumeroContainingIgnoreCaseOrTipoHabitacion(
                                query, tipoHabitacion, pageable);
            } else {
                page = habitacionRepository
                        .findByNumeroContainingIgnoreCaseOrPrecioNoche(
                                query, Float.parseFloat(query), pageable);
            }
        }

        List<Habitacion> habitaciones = page.getContent();
        List<HabitacionDTO> habitacionesDTO = convertToDtoHabitacionList(habitaciones);
        if (habitacionesDTO.size() > 0) {
            return ResponseEntity.ok(habitacionesDTO);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se encontraron habitaciones por los parámetros proporcionados");
        }
    }

    @Operation(summary = "Filtrado con GET por todos sus parámetros")
    @GetMapping("/filteredByEverything")
    public ResponseEntity<?> getFilteredByEverything(@RequestParam(required = false) String numero,
            @RequestParam(required = false) TipoHabitacion tipoHabitacion,
            @RequestParam(required = false) Float precioNoche, @RequestParam int pages) {

        Page<Habitacion> page = null;

        if (numero != null && tipoHabitacion != null && precioNoche != null) {
            page = habitacionRepository.findByNumeroAndTipoHabitacionAndPrecioNoche(numero, tipoHabitacion, precioNoche,
                    PageRequest.of(0, pages));
        } else if (numero != null && tipoHabitacion != null) {
            page = habitacionRepository.findByNumeroAndTipoHabitacion(numero, tipoHabitacion, PageRequest.of(0, pages));
        } else if (numero != null && precioNoche != null) {
            page = habitacionRepository.findByNumeroAndPrecioNoche(numero, precioNoche, PageRequest.of(0, pages));
        } else if (tipoHabitacion != null && precioNoche != null) {
            page = habitacionRepository.findByTipoHabitacionAndPrecioNoche(tipoHabitacion, precioNoche,
                    PageRequest.of(0, pages));
        } else if (numero != null) {
            page = habitacionRepository.findByNumero(numero, PageRequest.of(0, pages));
        } else if (tipoHabitacion != null) {
            page = habitacionRepository.findByTipoHabitacion(tipoHabitacion, PageRequest.of(0, pages));
        } else if (precioNoche != null) {
            page = habitacionRepository.findByPrecioNoche(precioNoche, PageRequest.of(0, pages));
        } else {
            page = habitacionRepository.findAll(PageRequest.of(0, pages));
        }

        List<Habitacion> habitaciones = page.getContent();
        List<HabitacionDTO> habitacionDTO = convertToDtoHabitacionList(habitaciones);
        if (habitacionDTO.size() > 0) {
            return ResponseEntity.ok(habitacionDTO);
        } else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se encontraron habitaciones por los parametros proporcionados");

    }

    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable(name = "id") int id, @RequestBody Habitacion input) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser un número entero positivo");
        }
        Habitacion find = habitacionRepository.findById(id).orElse(null);
        if (find != null) {
            find.setNumero(input.getNumero());
            find.setPrecioNoche(input.getPrecioNoche());
            find.setTipoHabitacion(input.getTipoHabitacion());
        } else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se encontró ninguna habitación por el ID proporcionado");
        Habitacion save = habitacionRepository.save(find);
        return ResponseEntity.ok(convertToDtoHabitacion(save));
    }

    @PostMapping
    public ResponseEntity<?> post(@RequestBody Habitacion input) {
        if (input.getTipoHabitacion() == null) {
            throw new IllegalArgumentException("Debes introducir el tipo de habitación");
        }
        Habitacion save = servicioHoteles.crearHabitacion(input);
        return ResponseEntity.ok(convertToDtoHabitacion(save));
    }

    @Operation(summary = "Añadir huésped a la habitación")
    @PostMapping("/{id}/huespedes")
    public ResponseEntity<?> anadirHuesped(@PathVariable(name = "id") int id, @RequestBody Huesped huesped) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser un número entero positivo");
        }
        Habitacion h = servicioHoteles.anadirHuesped(id, huesped); // Contemplo el NOT_FOUND en el servicioHoteles
        return ResponseEntity.ok(convertToDtoHabitacion(h));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable(name = "id") int id) {
        Habitacion findById = habitacionRepository.findById(id).orElse(null);
        if (findById != null) {
            habitacionRepository.delete(findById);
        } else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se encontró ninguna habitacion por el ID proporcionado");
        return ResponseEntity.ok().build();
    }

    /* ====== MAPPER ====== */

    public static HabitacionDTO convertToDtoHabitacion(Habitacion habitacion) {
        return modelMapper.map(habitacion, HabitacionDTO.class);
    }

    public static List<HabitacionDTO> convertToDtoHabitacionList(List<Habitacion> habitaciones) {
        return habitaciones.stream()
                .map(habitacion -> convertToDtoHabitacion(habitacion))
                .collect(Collectors.toList());
    }

}
