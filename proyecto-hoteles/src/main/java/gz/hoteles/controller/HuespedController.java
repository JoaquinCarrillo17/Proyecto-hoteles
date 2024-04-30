package gz.hoteles.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
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
import gz.hoteles.dto.HuespedDTO;
import gz.hoteles.entities.Habitacion;
import gz.hoteles.entities.Huesped;
import gz.hoteles.entities.JSONMapper;
import gz.hoteles.repositories.HuespedRepository;
import gz.hoteles.servicio.IServicioHoteles;
import gz.hoteles.support.ListOrderCriteria;
import gz.hoteles.support.SearchCriteria;
import gz.hoteles.support.SearchRequest;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/huespedes")
public class HuespedController {

    Logger log = LoggerFactory.getLogger(HuespedController.class);

    @Autowired
    HuespedRepository huespedRepository;

    @Autowired
    IServicioHoteles servicioHoteles;

    private static final ModelMapper modelMapper = new ModelMapper();

    @GetMapping()
    public ResponseEntity<?> list() {
        List<Huesped> huespedes = huespedRepository.findAll();
        List<HuespedDTO> huespedesDTO = convertToDtoHuespedList(huespedes);
        if (huespedesDTO.size() > 0) {
            return ResponseEntity.ok(huespedesDTO);
        } else {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No se ha encontrado ningún huésped");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable(name = "id") int id) {
        if (id <= 0 || Integer.valueOf(id) == null) {
            throw new IllegalArgumentException("El ID debe ser un número entero positivo");
        }
        Huesped huesped = huespedRepository.findById(id).orElse(null);
        if (huesped == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se encontró ningún huésped con el ID proporcionado");
        } else
            return ResponseEntity.ok(convertToDtoHuesped(huesped));
    }

    @GetMapping("/filteredByName")
    public ResponseEntity<?> getHuespedesByNombre(@RequestParam String nombre, @RequestParam int pages) {
        if (nombre == null || nombre.isEmpty()) {
            throw new IllegalArgumentException("El parámetro 'nombre' no puede estar vacío");
        }
        Pageable pageable = PageRequest.of(0, pages);
        Page<Huesped> page = huespedRepository.getHuespedesByNombre(nombre, pageable);
        List<Huesped> huespedes = page.getContent();
        List<HuespedDTO> huespedesDTO = convertToDtoHuespedList(huespedes);
        if (huespedesDTO.size() > 0) {
            return ResponseEntity.ok(huespedesDTO);
        } else
            throw new ResponseStatusException(HttpStatus.NO_CONTENT,
                    "No se encontró ningún huésped con nombre '" + nombre + "'");
    }

    @GetMapping("/filteredByDni")
    public ResponseEntity<?> getHuespedesByDni(@RequestParam String dni, @RequestParam int pages) {
        if (dni == null || dni.isEmpty()) {
            throw new IllegalArgumentException("El parámetro 'dni' no puede estar vacío");
        }
        Pageable pageable = PageRequest.of(0, pages);
        Page<Huesped> page = huespedRepository.getHuespedesByDni(dni, pageable);
        List<Huesped> huespedes = page.getContent();
        List<HuespedDTO> huespedesDTO = convertToDtoHuespedList(huespedes);
        if (huespedesDTO.size() > 0) {
            return ResponseEntity.ok(huespedesDTO);
        } else
            throw new ResponseStatusException(HttpStatus.NO_CONTENT,
                    "No se encontró ningún huésped con dni '" + dni + "'");
    }

    @GetMapping("/filteredByEmail")
    public ResponseEntity<?> getHuespedesByEmail(@RequestParam String email, @RequestParam int pages) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("El parámetro 'email' no puede estar vacío");
        }
        Pageable pageable = PageRequest.of(0, pages);
        Page<Huesped> page = huespedRepository.getHuespedesByEmail(email, pageable);
        List<Huesped> huespedes = page.getContent();
        List<HuespedDTO> huespedesDTO = convertToDtoHuespedList(huespedes);
        if (huespedesDTO.size() > 0) {
            return ResponseEntity.ok(huespedesDTO);
        } else
            throw new ResponseStatusException(HttpStatus.NO_CONTENT,
                    "No se encontró ningún huésped con email '" + email + "'");
    }

    @GetMapping("/filteredByCheckInDate")
    public ResponseEntity<?> getHuespedesByFechaEntrada(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date fecha, @RequestParam int pages) {
        if (fecha == null) {
            throw new IllegalArgumentException("El parámetro 'fecha' no puede estar vacío");
        }

        System.out.println(fecha);

        Pageable pageable = PageRequest.of(0, pages);
        Page<Huesped> page = huespedRepository.getHuespedesByFechaEntrada(fecha, pageable);
        List<Huesped> huespedes = page.getContent();
        List<HuespedDTO> huespedesDTO = convertToDtoHuespedList(huespedes);
        if (huespedesDTO.size() > 0) {
            return ResponseEntity.ok(huespedesDTO);
        } else {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT,
                    "No se encontró ningún huésped con fecha de entrada '" + fecha + "'");
        }
    }

    @GetMapping("/filteredByCheckOutDate")
    public ResponseEntity<?> getHuespedesByFechaSalida(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date fecha, @RequestParam int pages) {
        if (fecha == null) {
            throw new IllegalArgumentException("El parámetro 'fecha' no puede estar vacío");
        }
        Pageable pageable = PageRequest.of(0, pages);
        Page<Huesped> page = huespedRepository.getHuespedesByFechaSalida(fecha, pageable);
        List<Huesped> huespedes = page.getContent();
        List<HuespedDTO> huespedesDTO = convertToDtoHuespedList(huespedes);
        if (huespedesDTO.size() > 0) {
            return ResponseEntity.ok(huespedesDTO);
        } else
            throw new ResponseStatusException(HttpStatus.NO_CONTENT,
                    "No se encontró ningún huésped con fecha de salida '" + fecha + "'");
    }

    // @PostMapping("/dynamicSearch")
    public ResponseEntity<?> getHuespedesFilteredByParam(@RequestBody JSONMapper json) {
        if (json == null || json.getField() == null || json.getPages() <= 0
                || json.getSortBy() == null) {
            throw new IllegalArgumentException("Falta uno o más campos requeridos en el JSON");
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

        String field = json.getField();
        String value = json.getValue();
        String sortDirection = json.getSortDirection().equalsIgnoreCase("asc") ? "ASC" : "DESC";
        String sortByField = json.getSortBy(); // comprobar que recibe un string correcto
        if (!sortByField.equalsIgnoreCase("nombreCompleto") && !sortByField.equalsIgnoreCase("dni")
                && !sortByField.equalsIgnoreCase("email") && !sortByField.equalsIgnoreCase("fecha entrada")
                && !sortByField.equalsIgnoreCase("fecha salida")) {
            throw new IllegalArgumentException("No se puede ordenar por " + sortByField);
        }

        Date date = null;
        if (field.equals("fecha entrada") || field.equals("fecha salida")) {
            try {
                date = dateFormat.parse(value);
                System.out.println(date);
            } catch (ParseException e) {
                throw new IllegalArgumentException("Error al parsear la fecha: " + e.getMessage());
            }
        }

        Page<Huesped> page;
        switch (field) {
            case "nombre":
                page = huespedRepository.findByNombreCompletoContainingIgnoreCase(value,
                        PageRequest.of(0, json.getPages(),
                                Sort.by(Sort.Direction.fromString(sortDirection), sortByField)));
                break;
            case "dni":
                page = huespedRepository.findByDniEquals(value,
                        PageRequest.of(0, json.getPages(),
                                Sort.by(Sort.Direction.fromString(sortDirection), sortByField)));
                break;
            case "email":
                page = huespedRepository.findByEmailEquals(value,
                        PageRequest.of(0, json.getPages(),
                                Sort.by(Sort.Direction.fromString(sortDirection), sortByField)));
                break;
            case "fecha entrada":
                page = huespedRepository.findByFechaCheckInEquals(date,
                        PageRequest.of(0, json.getPages(),
                                Sort.by(Sort.Direction.fromString(sortDirection), sortByField)));
                break;
            case "fecha salida":
                page = huespedRepository.findByFechaCheckOutEquals(date,
                        PageRequest.of(0, json.getPages(),
                                Sort.by(Sort.Direction.fromString(sortDirection), sortByField)));
                break;
            default:
                throw new IllegalArgumentException("No se puede filtrar por '" + field + "'");
        }

        List<Huesped> huespedes = page.getContent();
        List<HuespedDTO> huespedesDTO = convertToDtoHuespedList(huespedes);
        if (huespedesDTO.size() > 0) {
            return ResponseEntity.ok(huespedesDTO);
        } else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se encontraron hoteles con " + json.getField() + " = " + json.getValue());
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

        Specification<Huesped> spec = Specification.where(null);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        for (SearchCriteria criteria : searchCriteriaList) {
            Date date;
            if (criteria.getKey().equals("fechaCheckIn") || criteria.getKey().equals("fechaCheckOut")) {
                try {
                    date = dateFormat.parse(criteria.getValue());
                } catch (ParseException e) {
                    throw new IllegalArgumentException("Error al parsear la fecha: " + e.getMessage());
                }

                switch (criteria.getOperation()) {
                    case "equals":
                        spec = spec.and((root, query, cb) -> cb.equal(root.get(criteria.getKey()),
                                date));
                        break;
                    case "not equals":
                        spec = spec.and((root, query, cb) -> cb.notEqual(root.get(criteria.getKey()),
                                date));
                        break;
                    case "contains":
                        spec = spec.and((root, query, cb) -> cb.like(root.get(criteria.getKey()),
                                "%" + date + "%"));
                        break;
                    case "greater than":
                        spec = spec.and(
                                (root, query, cb) -> cb.greaterThan(root.get(criteria.getKey()), date));
                        break;
                    case "less than":
                        spec = spec.and(
                                (root, query, cb) -> cb.lessThan(root.get(criteria.getKey()), date));
                        break;
                    case "greater or equals than":
                        spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get(criteria.getKey()),
                                date));
                        break;
                    case "less or equals than":
                        spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get(criteria.getKey()),
                                date));
                        break;
                    default:
                        throw new IllegalArgumentException(
                                "Operador de búsqueda no válido: " + criteria.getOperation());
                }
            } else {
                switch (criteria.getOperation()) {
                    case "equals":
                        spec = spec
                                .and((root, query, cb) -> cb.equal(root.get(criteria.getKey()), criteria.getValue()));
                        break;
                    case "not equals":
                        spec = spec.and(
                                (root, query, cb) -> cb.notEqual(root.get(criteria.getKey()), criteria.getValue()));
                        break;
                    case "contains":
                        spec = spec.and((root, query, cb) -> cb.like(root.get(criteria.getKey()),
                                "%" + criteria.getValue() + "%"));
                        break;
                    case "greater than":
                        spec = spec.and(
                                (root, query, cb) -> cb.greaterThan(root.get(criteria.getKey()), criteria.getValue()));
                        break;
                    case "less than":
                        spec = spec.and(
                                (root, query, cb) -> cb.lessThan(root.get(criteria.getKey()), criteria.getValue()));
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
                        throw new IllegalArgumentException(
                                "Operador de búsqueda no válido: " + criteria.getOperation());
                }
            }
        }

        String sortByField = orderCriteriaList.getSortBy();
        String sortDirection = orderCriteriaList.getValueSortOrder().equalsIgnoreCase("asc") ? "ASC" : "DESC";
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortByField);

        Page<Huesped> page = huespedRepository.findAll(spec, PageRequest.of(pageIndex, pageSize, sort));

        List<Huesped> huespedes = page.getContent();
        List<HuespedDTO> huespedesDTO = convertToDtoHuespedList(huespedes);
        if (huespedesDTO.size() > 0) {
            return ResponseEntity.ok(huespedesDTO);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se encontraron huéspedes con los parámetros proporcionados");
        }

    }

    // TODO: SI LE PASO UNA FECHA DE TIPO YYYY-MM-DD NO ME ENCUENTRA A LOS HUESPEDES
    @GetMapping("/magicFilter")
    public ResponseEntity<?> getHuespedesByMagicFilter(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam("pageNumber") int pagina,
            @RequestParam("itemsPerPage") int itemsPorPagina,
            @RequestParam(value = "valueSortOrder") String valueSortOrder,
            @RequestParam(value = "sortBy") String sortBy) {

        // Definir la paginación y clasificación
        Sort.Direction direction = Sort.Direction.fromString(valueSortOrder.toUpperCase());
        Pageable pageable = PageRequest.of(pagina, itemsPorPagina, Sort.by(direction, sortBy));

        // Realizar la búsqueda en la base de datos
        Page<Huesped> page;
        long totalItems; // Variable para almacenar el número total de elementos coincidentes

        if (query == null || query.isEmpty()) {
            page = huespedRepository.findAll(pageable);
            totalItems = huespedRepository.count(); // Obtener el número total de huéspedes en la base de datos
        } else {

            // Intentar parsear el parámetro 'query' como fecha
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
            Date fechaQuery = null;
            try {
                // Intenta parsear la fecha completa
                fechaQuery = dateFormat.parse(query);
            } catch (ParseException e) {
                try {
                    // Si falla, intenta parsear solo la fecha sin la hora
                    dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    fechaQuery = dateFormat.parse(query);
                } catch (ParseException ex) {
                    // Si tampoco se puede parsear como fecha, se ignora y se busca sin tener en
                    // cuenta las fechas
                }
            }

            if (fechaQuery != null) {
                // Si se pudo parsear como fecha, se realiza la búsqueda teniendo en cuenta la
                // fecha
                page = huespedRepository.findByQueryAndDate(query, fechaQuery, pageable);
            } else {
                // Si no se pudo parsear como fecha, se realiza la búsqueda sin tener en cuenta
                // las fechas
                page = huespedRepository
                        .findByNombreCompletoContainingIgnoreCaseOrDniContainingIgnoreCaseOrEmailContainingIgnoreCase(
                                query, query, query, pageable);
            }

            totalItems = page.getTotalElements(); // Obtener el número total de elementos coincidentes
        }

        List<Huesped> huespedes = page.getContent();
        List<HuespedDTO> huespedesDTO = convertToDtoHuespedList(huespedes);

        // Crear un objeto JSON para la respuesta que incluya tanto la lista de
        // huespedesDTO como el número total de elementos
        Map<String, Object> response = new HashMap<>();
        response.put("huespedes", huespedesDTO);
        response.put("totalItems", totalItems);

        if (!huespedesDTO.isEmpty()) {
            return ResponseEntity.ok(response);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se encontraron huéspedes por los parámetros proporcionados");
        }
    }

    @PostMapping("/dynamicSearchWithDateRange")
    public ResponseEntity<?> getHuespedesFilteredByParamWithDateRange(@RequestBody JSONMapper json,
            @RequestParam(name = "fechaEntrada", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaEntrada,
            @RequestParam(name = "fechaSalida", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaSalida) {
        if (json == null || json.getField() == null || json.getPages() <= 0
                || json.getSortBy() == null) {
            throw new IllegalArgumentException("Falta uno o más campos requeridos en el JSON");
        }

        String field = json.getField();
        String value = json.getValue();
        String sortDirection = json.getSortDirection().equalsIgnoreCase("asc") ? "ASC" : "DESC";
        String sortByField = json.getSortBy(); // comprobar que recibe un string correcto
        if (!sortByField.equalsIgnoreCase("nombreCompleto") && !sortByField.equalsIgnoreCase("dni")
                && !sortByField.equalsIgnoreCase("email")) {
            throw new IllegalArgumentException("No se puede ordenar por " + sortByField);
        }

        Page<Huesped> page = null;

        if (fechaEntrada == null && fechaSalida == null) {
            return getHuespedesFilteredByParam(json);
        } else {
            if (fechaEntrada == null) {
                switch (field) {
                    case "nombre":
                        page = huespedRepository.findByNombreCompletoContainingIgnoreCaseAndFechaCheckOutBefore(
                                value, fechaSalida, PageRequest.of(0, json.getPages(),
                                        Sort.by(Sort.Direction.fromString(sortDirection), sortByField)));
                        break;
                    case "dni":
                        page = huespedRepository.findByDniEqualsAndFechaCheckOutBefore(value, fechaSalida,
                                PageRequest.of(0,
                                        json.getPages(),
                                        Sort.by(Sort.Direction.fromString(sortDirection), sortByField)));
                        break;
                    case "email":
                        page = huespedRepository.findByEmailEqualsAndFechaCheckOutBefore(value, fechaSalida,
                                PageRequest.of(0,
                                        json.getPages(),
                                        Sort.by(Sort.Direction.fromString(sortDirection), sortByField)));
                        break;
                    default:
                        throw new IllegalArgumentException("No se puede filtrar por '" + field + "'");
                }
            } else if (fechaSalida == null) {
                switch (field) {
                    case "nombre":
                        page = huespedRepository.findByNombreCompletoContainingIgnoreCaseAndFechaCheckInAfter(
                                value, fechaEntrada, PageRequest.of(0, json.getPages(),
                                        Sort.by(Sort.Direction.fromString(sortDirection), sortByField)));
                        break;
                    case "dni":
                        page = huespedRepository.findByDniEqualsAndFechaCheckInAfter(value, fechaEntrada, PageRequest
                                .of(0, json.getPages(),
                                        Sort.by(Sort.Direction.fromString(sortDirection), sortByField)));
                        break;
                    case "email":
                        page = huespedRepository.findByEmailEqualsAndFechaCheckInAfter(value, fechaEntrada,
                                PageRequest.of(0,
                                        json.getPages(),
                                        Sort.by(Sort.Direction.fromString(sortDirection), sortByField)));
                        break;
                    default:
                        throw new IllegalArgumentException("No se puede filtrar por '" + field + "'");
                }
            } else {
                switch (field) {
                    case "nombre":
                        page = huespedRepository
                                .findByNombreCompletoContainingIgnoreCaseAndFechaCheckInAfterAndFechaCheckOutBefore(
                                        value,
                                        fechaEntrada, fechaSalida, PageRequest.of(0, json.getPages(),
                                                Sort.by(Sort.Direction.fromString(sortDirection), sortByField)));
                        break;
                    case "dni":
                        page = huespedRepository.findByDniEqualsAndFechaCheckInAfterAndFechaCheckOutBefore(value,
                                fechaEntrada, fechaSalida, PageRequest.of(0, json.getPages(),
                                        Sort.by(Sort.Direction.fromString(sortDirection), sortByField)));
                        break;
                    case "email":
                        page = huespedRepository.findByEmailEqualsAndFechaCheckInAfterAndFechaCheckOutBefore(value,
                                fechaEntrada, fechaSalida, PageRequest.of(0, json.getPages(),
                                        Sort.by(Sort.Direction.fromString(sortDirection), sortByField)));
                        break;
                    default:
                        throw new IllegalArgumentException("No se puede filtrar por '" + field + "'");
                }
            }
        }

        List<Huesped> huespedes = page.getContent();
        List<HuespedDTO> huespedesDTO = convertToDtoHuespedList(huespedes);
        if (huespedesDTO.size() > 0) {
            return ResponseEntity.ok(huespedesDTO);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se encontraron huéspedes con " + json.getField() + " = " + json.getValue());
        }
    }

    @Operation(summary = "Filtrado con GET por todos sus parámetros")
    @GetMapping("/filteredByEverythingWithDateRange")
    public ResponseEntity<?> getFilteredByEverythingWithDateRange(@RequestParam(required = false) String nombre,
            @RequestParam(required = false) String email, @RequestParam(required = false) String dni,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date fechaEntrada,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date fechaSalida,
            @RequestParam int pages) {

        Page<Huesped> page = null;

        if (fechaEntrada != null && fechaSalida != null) {
            // Caso: Se proporcionan ambas fechas
            page = findByNombreCompletoAndEmailAndDniAndFechaCheckInAndFechaCheckOut(nombre, email, dni, fechaEntrada,
                    fechaSalida, pages);
        } else if (fechaEntrada != null) {
            // Caso: Solo se proporciona fecha de entrada
            page = findByNombreCompletoAndEmailAndFechaCheckIn(nombre, email, dni, fechaEntrada, pages);
        } else if (fechaSalida != null) {
            // Caso: Solo se proporciona fecha de salida
            page = findByNombreCompletoAndEmailAndDniAndFechaCheckOut(nombre, email, dni, fechaSalida, pages);
        } else {
            // Caso: No se proporciona ninguna fecha
            page = findByNombreAndEmailAndDni(nombre, email, dni, pages);
        }

        List<Huesped> huespedes = page.getContent();
        List<HuespedDTO> huespedesDTO = convertToDtoHuespedList(huespedes);
        if (huespedesDTO.size() > 0) {
            return ResponseEntity.ok(huespedesDTO);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se encontraron huéspedes con los parámetros proporcionados");
        }

    }

    private Page<Huesped> findByNombreCompletoAndEmailAndDniAndFechaCheckInAndFechaCheckOut(String nombreCompleto,
            String email, String dni, Date fechaCheckIn, Date fechaCheckOut, int pages) {
        if (nombreCompleto != null && email != null && dni != null) {
            return huespedRepository.findByNombreCompletoAndEmailAndDniAndFechaCheckInAfterAndFechaCheckOutBefore(
                    nombreCompleto, email, dni, fechaCheckIn, fechaCheckOut, PageRequest.of(0, pages));
        } else if (nombreCompleto != null && email != null) {
            return huespedRepository.findByNombreCompletoAndEmailAndFechaCheckInAfterAndFechaCheckOutBefore(
                    nombreCompleto, email, fechaCheckIn, fechaCheckOut, PageRequest.of(0, pages));
        } else if (nombreCompleto != null) {
            return huespedRepository.findByNombreCompletoAndFechaCheckInAfterAndFechaCheckOutBefore(nombreCompleto,
                    fechaCheckIn, fechaCheckOut, PageRequest.of(0, pages));
        } else if (email != null && dni != null) {
            return huespedRepository.findByEmailAndDniAndFechaCheckInAfterAndFechaCheckOutBefore(email, dni,
                    fechaCheckIn, fechaCheckOut, PageRequest.of(0, pages));
        } else if (email != null) {
            return huespedRepository.findByEmailAndFechaCheckInAfterAndFechaCheckOutBefore(email, fechaCheckIn,
                    fechaCheckOut, PageRequest.of(0, pages));
        } else if (dni != null) {
            return huespedRepository.findByDniAndFechaCheckInAfterAndFechaCheckOutBefore(dni, fechaCheckIn,
                    fechaCheckOut, PageRequest.of(0, pages));
        } else {
            return huespedRepository.findByFechaCheckInAfterAndFechaCheckOutBefore(fechaCheckIn, fechaCheckOut,
                    PageRequest.of(0, pages));
        }
    }

    private Page<Huesped> findByNombreCompletoAndEmailAndFechaCheckIn(String nombreCompleto, String email, String dni,
            Date fechaCheckIn, int pages) {
        if (nombreCompleto != null && email != null && dni != null) {
            return huespedRepository.findByNombreCompletoAndEmailAndDniAndFechaCheckInAfter(nombreCompleto, email, dni,
                    fechaCheckIn, PageRequest.of(0, pages));
        } else if (nombreCompleto != null && email != null) {
            return huespedRepository.findByNombreCompletoAndEmailAndFechaCheckInAfter(nombreCompleto, email,
                    fechaCheckIn, PageRequest.of(0, pages));
        } else if (nombreCompleto != null) {
            return huespedRepository.findByNombreCompletoAndFechaCheckInAfter(nombreCompleto, fechaCheckIn,
                    PageRequest.of(0, pages));
        } else if (email != null && dni != null) {
            return huespedRepository.findByEmailAndDniAndFechaCheckInAfter(email, dni, fechaCheckIn,
                    PageRequest.of(0, pages));
        } else if (email != null) {
            return huespedRepository.findByEmailAndFechaCheckInAfter(email, fechaCheckIn, PageRequest.of(0, pages));
        } else if (dni != null) {
            return huespedRepository.findByDniAndFechaCheckInAfter(dni, fechaCheckIn, PageRequest.of(0, pages));
        } else {
            return huespedRepository.findByFechaCheckInAfter(fechaCheckIn, PageRequest.of(0, pages));
        }
    }

    private Page<Huesped> findByNombreCompletoAndEmailAndDniAndFechaCheckOut(String nombreCompleto, String email,
            String dni, Date fechaCheckOut, int pages) {
        if (nombreCompleto != null && email != null && dni != null) {
            return huespedRepository.findByNombreCompletoAndEmailAndDniAndFechaCheckOutBefore(nombreCompleto, email,
                    dni, fechaCheckOut, PageRequest.of(0, pages));
        } else if (nombreCompleto != null && email != null) {
            return huespedRepository.findByNombreCompletoAndEmailAndFechaCheckOutBefore(nombreCompleto, email,
                    fechaCheckOut, PageRequest.of(0, pages));
        } else if (nombreCompleto != null) {
            return huespedRepository.findByNombreCompletoAndFechaCheckOutBefore(nombreCompleto, fechaCheckOut,
                    PageRequest.of(0, pages));
        } else if (email != null && dni != null) {
            return huespedRepository.findByEmailAndDniAndFechaCheckOutBefore(email, dni, fechaCheckOut,
                    PageRequest.of(0, pages));
        } else if (email != null) {
            return huespedRepository.findByEmailAndFechaCheckOutBefore(email, fechaCheckOut, PageRequest.of(0, pages));
        } else if (dni != null) {
            return huespedRepository.findByDniAndFechaCheckOutBefore(dni, fechaCheckOut, PageRequest.of(0, pages));
        } else {
            return huespedRepository.findByFechaCheckOutBefore(fechaCheckOut, PageRequest.of(0, pages));
        }
    }

    private Page<Huesped> findByNombreAndEmailAndDni(String nombre, String email, String dni, int pages) {
        if (nombre != null && email != null && dni != null) {
            return huespedRepository.findByNombreCompletoAndEmailAndDni(nombre, email, dni, PageRequest.of(0, pages));
        } else if (nombre != null && email != null) {
            return huespedRepository.findByNombreCompletoAndEmail(nombre, email, PageRequest.of(0, pages));
        } else if (nombre != null) {
            return huespedRepository.findByNombreCompleto(nombre, PageRequest.of(0, pages));
        } else if (email != null && dni != null) {
            return huespedRepository.findByEmailAndDni(email, dni, PageRequest.of(0, pages));
        } else if (email != null) {
            return huespedRepository.findByEmail(email, PageRequest.of(0, pages));
        } else if (dni != null) {
            return huespedRepository.findByDni(dni, PageRequest.of(0, pages));
        } else {
            return huespedRepository.findAll(PageRequest.of(0, pages));
        }
    }

    @Operation(summary = "Asocia un huésped NO EXISTENTE a una habitación")
    @PostMapping("/{id}")
    public ResponseEntity<?> linkearHuespedNoExistenteHabitacion(@PathVariable(name = "id") int id,
            @RequestBody Huesped huesped) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser un número entero positivo");
        }
        Habitacion h = servicioHoteles.anadirHuesped(id, huesped);
        return ResponseEntity.ok(convertToDtoHabitacion(h));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable(name = "id") int id, @RequestBody Huesped input) {
        if (id <= 0 || Integer.valueOf(id) == null) {
            throw new IllegalArgumentException("El ID debe ser un número entero positivo");
        }
        Huesped find = huespedRepository.findById(id).orElse(null);
        if (find != null) {
            find.setNombreCompleto(input.getNombreCompleto());
            find.setDni(input.getDni());
            find.setEmail(input.getEmail());
            find.setFechaCheckIn(input.getFechaCheckIn());
            find.setFechaCheckOut(input.getFechaCheckOut());
        } else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se encontró ningún huésped con el ID proporcionado");
        Huesped save = huespedRepository.save(find);
        return ResponseEntity.ok(convertToDtoHuesped(save));
    }

    @PostMapping
    public ResponseEntity<?> post(@RequestBody Huesped input) {

        if (input.getNombreCompleto().isEmpty() || input.getNombreCompleto() == null || input.getDni() == null
                || input.getDni().isEmpty()) {
            throw new IllegalArgumentException("El nombre y el dni son obligatorios");
        }
        Huesped save = huespedRepository.save(input);
        return ResponseEntity.ok(convertToDtoHuesped(save));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable(name = "id") int id) {
        if (id <= 0 || Integer.valueOf(id) == null) {
            throw new IllegalArgumentException("El ID debe ser un número entero positivo");
        }
        Huesped findById = huespedRepository.findById(id).orElse(null);
        if (findById != null) {
            huespedRepository.delete(findById);
        } else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se encontró ningún huésped con el ID proporcionado");
        return ResponseEntity.ok().build();
    }

    /* ====== MAPPER ====== */

    public static HuespedDTO convertToDtoHuesped(Huesped huesped) {
        return modelMapper.map(huesped, HuespedDTO.class);
    }

    public static List<HuespedDTO> convertToDtoHuespedList(List<Huesped> huespedes) {
        return huespedes.stream()
                .map(huesped -> convertToDtoHuesped(huesped))
                .collect(Collectors.toList());
    }

    public static HabitacionDTO convertToDtoHabitacion(Habitacion habitacion) {
        return modelMapper.map(habitacion, HabitacionDTO.class);
    }

}
