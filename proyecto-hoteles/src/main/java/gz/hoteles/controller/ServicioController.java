package gz.hoteles.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import gz.hoteles.dto.ServicioDTO;
import gz.hoteles.entities.CategoriaServicio;
import gz.hoteles.entities.Servicio;
import gz.hoteles.repositories.ServicioRepository;
import gz.hoteles.support.ListOrderCriteria;
import gz.hoteles.support.SearchCriteria;
import gz.hoteles.support.SearchRequest;
import io.swagger.v3.oas.annotations.Operation;

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
        } else
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No se ha encontrado ningún servicio");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable(name = "id") int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser un número entero positivo");
        }
        Servicio servicio = servicioRepository.findById(id).orElse(null);
        if (servicio == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se encontró ningún servicio con el ID proporcionado");
        } else
            return ResponseEntity.ok(convertToDtoServicio(servicio));
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
        } else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se encontró ningún hotel por el nombre '" + nombre + "'");
    }

    @GetMapping("/filteredByCategory")
    public ResponseEntity<?> getServicioByCategoria(@RequestParam CategoriaServicio categoria,
            @RequestParam int pages) {
        if (categoria == null) {
            throw new IllegalArgumentException("El parámetro 'categoría' no puede ser nulo");
        }
        Pageable pageable = PageRequest.of(0, pages);
        Page<Servicio> page = servicioRepository.getServicioByCategoria(categoria, pageable);
        List<Servicio> servicios = page.getContent();
        List<ServicioDTO> serviciosDTO = convertToDtoServicioList(servicios);
        if (serviciosDTO.size() > 0) {
            return ResponseEntity.ok(serviciosDTO);
        } else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se encontró ningún servicio por la categoría '" + categoria + "'");
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
        } else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se encontró ningún servicio por la descripción '" + descripcion + "'");
    }

    /*
     * @PostMapping("/dynamicSearch")
     * public ResponseEntity<?> getHabitacionesFilteredByParam(@RequestBody
     * JSONMapper json) {
     * if (json == null || json.getField() == null || json.getPages() <= 0
     * || json.getSortBy() == null) {
     * throw new
     * IllegalArgumentException("Falta uno o más campos requeridos en el JSON");
     * }
     * 
     * String field = json.getField();
     * String value = json.getValue();
     * String sortDirection = json.getSortDirection().equalsIgnoreCase("asc") ?
     * "ASC" : "DESC";
     * String sortByField = json.getSortBy(); // comprobar que recibe un string
     * correcto
     * if (!sortByField.equalsIgnoreCase("nombre") &&
     * !sortByField.equalsIgnoreCase("descripcion")
     * && !sortByField.equalsIgnoreCase("categoria")) {
     * throw new IllegalArgumentException("No se puede ordenar por " + sortByField);
     * }
     * 
     * Page<Servicio> page = switch (field) {
     * case "nombre" -> servicioRepository.findByNombreContainingIgnoreCase(value,
     * PageRequest.of(0, json.getPages(),
     * Sort.by(Sort.Direction.fromString(sortDirection), sortByField)));
     * case "descripcion" -> servicioRepository.findByDescripcionEquals(value,
     * PageRequest.of(0, json.getPages(),
     * Sort.by(Sort.Direction.fromString(sortDirection), sortByField)));
     * case "categoria" -> {
     * switch (value.toUpperCase()) {
     * case "GIMNASIO":
     * yield servicioRepository.findByCategoriaEquals(CategoriaServicio.GIMNASIO,
     * PageRequest.of(0,
     * json.getPages(), Sort.by(Sort.Direction.fromString(sortDirection),
     * sortByField)));
     * case "LAVANDERIA":
     * yield servicioRepository.findByCategoriaEquals(CategoriaServicio.LAVANDERIA,
     * PageRequest.of(0,
     * json.getPages(), Sort.by(Sort.Direction.fromString(sortDirection),
     * sortByField)));
     * case "BAR":
     * yield servicioRepository.findByCategoriaEquals(CategoriaServicio.BAR,
     * PageRequest.of(0,
     * json.getPages(), Sort.by(Sort.Direction.fromString(sortDirection),
     * sortByField)));
     * case "CASINO":
     * yield servicioRepository.findByCategoriaEquals(CategoriaServicio.CASINO,
     * PageRequest.of(0,
     * json.getPages(), Sort.by(Sort.Direction.fromString(sortDirection),
     * sortByField)));
     * case "KARAOKE":
     * yield servicioRepository.findByCategoriaEquals(CategoriaServicio.KARAOKE,
     * PageRequest.of(0,
     * json.getPages(), Sort.by(Sort.Direction.fromString(sortDirection),
     * sortByField)));
     * default:
     * throw new IllegalArgumentException("Valor de categoría no válido");
     * }
     * }
     * default -> throw new IllegalArgumentException("No se puede filtrar por '" +
     * field + "'");
     * };
     * 
     * List<Servicio> servicios = page.getContent();
     * List<ServicioDTO> serviciosDTO = convertToDtoServicioList(servicios);
     * if (serviciosDTO.size() > 0) {
     * return ResponseEntity.ok(serviciosDTO);
     * } else
     * throw new ResponseStatusException(HttpStatus.NOT_FOUND,
     * "No se encontraron servicios con " + json.getField() + " = " +
     * json.getValue());
     * }
     */

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

        Specification<Servicio> spec = Specification.where(null);
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

        Page<Servicio> page = servicioRepository.findAll(spec, PageRequest.of(pageIndex, pageSize, sort));

        List<Servicio> servicios = page.getContent();
        List<ServicioDTO> serviciosDTO = convertToDtoServicioList(servicios);
        if (!serviciosDTO.isEmpty()) {
            return ResponseEntity.ok(serviciosDTO);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se encontraron servicios con los criterios proporcionados");
        }

    }

    @Operation(summary = "Filtrado de servicios por todos sus parámetros a través de un solo String")
    @GetMapping("/magicFilter")
    public ResponseEntity<?> getServicioByMagicFilter(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam("pageNumber") int pagina,
            @RequestParam("itemsPerPage") int itemsPorPagina) {

        // Crear un objeto Pageable para la paginación
        Pageable pageable = PageRequest.of(pagina, itemsPorPagina, Sort.by("id").ascending());

        // Realizar la búsqueda en la base de datos
        Page<Servicio> page;
        long totalItems; // Variable para almacenar el número total de elementos coincidentes

        if (query == null || query.isEmpty()) {
            page = servicioRepository.findAll(pageable);
            totalItems = servicioRepository.count(); // Obtener el número total de servicios en la base de datos
        } else {

            // Convertir el valor de query a CategoriaServicio
            CategoriaServicio categoriaServicio = null;
            switch (query.toUpperCase()) {
                case "GIMNASIO":
                    categoriaServicio = CategoriaServicio.GIMNASIO;
                    break;
                case "LAVANDERIA":
                    categoriaServicio = CategoriaServicio.LAVANDERIA;
                    break;
                case "BAR":
                    categoriaServicio = CategoriaServicio.BAR;
                    break;
                case "CASINO":
                    categoriaServicio = CategoriaServicio.CASINO;
                    break;
                case "KARAOKE":
                    categoriaServicio = CategoriaServicio.KARAOKE;
                    break;
                default:
                    // Si no coincide con ninguna categoría de servicio, se ignora y se busca con la
                    // cadena directamente
            }

            if (categoriaServicio != null) {
                page = servicioRepository.findByNombreContainingIgnoreCaseOrDescripcionContainingIgnoreCaseOrCategoria(
                        query, query, categoriaServicio, pageable);
            } else {
                page = servicioRepository.findByNombreContainingIgnoreCaseOrDescripcionContainingIgnoreCase(
                        query, query, pageable);
            }

            totalItems = page.getTotalElements(); // Obtener el número total de elementos coincidentes
        }

        List<Servicio> servicios = page.getContent();
        List<ServicioDTO> serviciosDTO = convertToDtoServicioList(servicios);

        // Crear un objeto JSON para la respuesta que incluya tanto la lista de
        // serviciosDTO como el número total de elementos
        Map<String, Object> response = new HashMap<>();
        response.put("servicios", serviciosDTO);
        response.put("totalItems", totalItems);

        if (!serviciosDTO.isEmpty()) {
            return ResponseEntity.ok(response);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se encontraron servicios por los parámetros proporcionados");
        }
    }

    @Operation(summary = "Filtrado con GET por todos sus parámetros")
    @GetMapping("/filteredByEverything")
    public ResponseEntity<?> getFilteredByEverything(@RequestParam(required = false) String nombre,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) CategoriaServicio categoria, @RequestParam int pages) {

        Page<Servicio> page = null;

        if (nombre != null && descripcion != null && categoria != null) {
            page = servicioRepository.findByNombreAndDescripcionAndCategoria(nombre, descripcion, categoria,
                    PageRequest.of(0, pages));
        } else if (nombre != null && descripcion != null) {
            page = servicioRepository.findByNombreAndDescripcion(nombre, descripcion, PageRequest.of(0, pages));
        } else if (nombre != null && categoria != null) {
            page = servicioRepository.findByNombreAndCategoria(nombre, categoria, PageRequest.of(0, pages));
        } else if (descripcion != null && categoria != null) {
            page = servicioRepository.findByDescripcionAndCategoria(descripcion, categoria, PageRequest.of(0, pages));
        } else if (nombre != null) {
            page = servicioRepository.findByNombre(nombre, PageRequest.of(0, pages));
        } else if (descripcion != null) {
            page = servicioRepository.findByDescripcion(descripcion, PageRequest.of(0, pages));
        } else if (categoria != null) {
            page = servicioRepository.findByCategoria(categoria, PageRequest.of(0, pages));
        } else {
            page = servicioRepository.findAll(PageRequest.of(0, pages));
        }

        List<Servicio> servicios = page.getContent();
        List<ServicioDTO> serviciosDTO = convertToDtoServicioList(servicios);
        if (serviciosDTO.size() > 0) {
            return ResponseEntity.ok(serviciosDTO);
        } else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se encontraron servicios por los parámetros proporcionados");

    }

    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable(name = "id") int id, @RequestBody Servicio input) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser un número entero positivo");
        }
        Servicio find = servicioRepository.findById(id).orElse(null);
        if (find != null) {
            find.setCategoria(input.getCategoria());
            find.setNombre(input.getNombre());
            find.setDescripcion(input.getDescripcion());
        } else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se encontró ningun servicio por el ID proporcionado");
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
        if (findById != null) {
            servicioRepository.delete(findById);
        } else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se encontró ningun servicio por el ID proporcionado");
        return ResponseEntity.ok().build();
    }

    /* ====== MAPPER ====== */

    public static ServicioDTO convertToDtoServicio(Servicio servicio) {
        return modelMapper.map(servicio, ServicioDTO.class);
    }

    public static List<ServicioDTO> convertToDtoServicioList(List<Servicio> servicios) {
        return servicios.stream()
                .map(servicio -> convertToDtoServicio(servicio))
                .collect(Collectors.toList());
    }

}
