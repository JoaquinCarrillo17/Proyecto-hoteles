package gz.hoteles.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
import gz.hoteles.dto.HotelDTO;
import gz.hoteles.entities.Habitacion;
import gz.hoteles.entities.Hotel;
import gz.hoteles.entities.Huesped;
import gz.hoteles.entities.ServiciosHabitacionEnum;
import gz.hoteles.entities.ServiciosHotelEnum;
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
        if (id <= 0  || Integer.valueOf(id) == null) {
            throw new IllegalArgumentException("El ID debe ser un número entero positivo");
        }
        Habitacion habitacion = habitacionRepository.findById(id).orElse(null);
        if (habitacion == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se encontró ninguna habitación con el ID proporcionado");
        } else
            return ResponseEntity.ok(convertToDtoHabitacion(habitacion));
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

    @PostMapping("/dynamicFilterOr")
    public ResponseEntity<?> getFilteredByDynamicSearchOr(@RequestBody SearchRequest searchRequest) {

        List<SearchCriteria> searchCriteriaList = searchRequest.getListSearchCriteria();
        ListOrderCriteria orderCriteriaList = searchRequest.getListOrderCriteria();
        int pageSize = searchRequest.getPage().getPageSize();
        int pageIndex = searchRequest.getPage().getPageIndex();

        Specification<Habitacion> spec = Specification.where(null);

        for (SearchCriteria criteria : searchCriteriaList) {
            switch (criteria.getOperation()) {
                case "equals":
                    if (criteria.getKey().equals("hotel.idUsuario")) {
                        // Filtrar por hotel.idUsuario
                        spec = spec.and(
                                (root, query, cb) -> cb.equal(root.get("hotel").get("idUsuario"), criteria.getValue()));
                    } else if (criteria.getKey().equals("servicios")) {
                        // Filtrar por servicios de la habitación
                        spec = spec.or((root, query, cb) -> cb
                                .isMember(ServiciosHabitacionEnum.valueOf(criteria.getValue()), root.get("servicios")));
                    } else {
                        spec = spec.or((root, query, cb) -> cb.equal(root.get(criteria.getKey()), criteria.getValue()));
                    }
                    break;
                case "contains":
                    if (criteria.getKey().equals("hotel.nombre")) {
                        // Filtrar por hotel.nombre
                        spec = spec.or(
                                (root, query, cb) -> cb.like(root.get("hotel").get("nombre"), "%" + criteria.getValue() + "%"));
                    } else
                        spec = spec.or(
                                (root, query, cb) -> cb.like(root.get(criteria.getKey()),
                                        "%" + criteria.getValue() + "%"));
                    break;
                // Otras operaciones...
                default:
                    throw new IllegalArgumentException("Operador de búsqueda no válido: " + criteria.getOperation());
            }
        }

        String sortByField = orderCriteriaList.getSortBy();
        String sortDirection = orderCriteriaList.getValueSortOrder().equalsIgnoreCase("asc") ? "ASC" : "DESC";
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortByField);

        Page<Habitacion> page = habitacionRepository.findAll(spec, PageRequest.of(pageIndex, pageSize, sort));

        List<HabitacionDTO> habitacionDTOList = page.getContent().stream()
                .map(HabitacionController::convertToDtoHabitacion)
                .collect(Collectors.toList());

        Page<HabitacionDTO> habitacionDTOPage = new PageImpl<>(habitacionDTOList, PageRequest.of(pageIndex, pageSize, sort),
                page.getTotalElements());

        return ResponseEntity.ok(habitacionDTOPage);
    }

    @GetMapping("/{id}/full")
    public ResponseEntity<?> getHabitacionFull(@PathVariable(name = "id") int id) {
        if (id <= 0  || Integer.valueOf(id) == null) {
            throw new IllegalArgumentException("El ID debe ser un número entero positivo");
        }
        Habitacion habitacion = habitacionRepository.findById(id).orElse(null);
        if (habitacion == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se encontró ninguna habitación con el ID proporcionado");
        } else
            return ResponseEntity.ok(habitacion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable(name = "id") int id, @RequestBody Habitacion input) {
        if (id <= 0  || Integer.valueOf(id) == null) {
            throw new IllegalArgumentException("El ID debe ser un número entero positivo");
        }
        Habitacion find = habitacionRepository.findById(id).orElse(null);
        if (find != null) {
            find.setNumero(input.getNumero());
            find.setPrecioNoche(input.getPrecioNoche());
            find.setTipoHabitacion(input.getTipoHabitacion());
            find.setServicios(input.getServicios());
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
        if (id <= 0  || Integer.valueOf(id) == null) {
            throw new IllegalArgumentException("El ID debe ser un número entero positivo");
        }
        Habitacion h = servicioHoteles.anadirHuesped(id, huesped); // Contemplo el NOT_FOUND en el servicioHoteles
        return ResponseEntity.ok(convertToDtoHabitacion(h));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable(name = "id") int id) {
        if (id <= 0  || Integer.valueOf(id) == null) {
            throw new IllegalArgumentException("El ID debe ser un número entero positivo");
        }
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
        ModelMapper modelMapper = new ModelMapper();
        
        // Configurar mapeo personalizado para incluir el nombre del hotel en el DTO
        modelMapper.typeMap(Habitacion.class, HabitacionDTO.class).addMappings(mapper -> {
            mapper.map(src -> src.getHotel().getNombre(), HabitacionDTO::setNombreHotel);
            mapper.map(src -> src.getHotel().getIdUsuario(), HabitacionDTO::setIdUsuario);
        });
    
        return modelMapper.map(habitacion, HabitacionDTO.class);
    }
    

    public static List<HabitacionDTO> convertToDtoHabitacionList(List<Habitacion> habitaciones) {
        return habitaciones.stream()
                .map(habitacion -> convertToDtoHabitacion(habitacion))
                .collect(Collectors.toList());
    }

}
