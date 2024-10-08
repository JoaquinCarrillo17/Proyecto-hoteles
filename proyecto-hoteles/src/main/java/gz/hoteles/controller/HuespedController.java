package gz.hoteles.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.criteria.Join;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
import gz.hoteles.dto.HotelDTO;
import gz.hoteles.dto.HuespedDTO;
import gz.hoteles.entities.Habitacion;
import gz.hoteles.entities.Hotel;
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

    @PostMapping("/dynamicFilterAnd")
    public ResponseEntity<?> getFilteredByDynamicSearchAnd(@RequestBody SearchRequest searchRequest) {

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
                    case "contains":
                        spec = spec.and((root, query, cb) -> cb.like(root.get(criteria.getKey()),
                                "%" + date + "%"));
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
                    case "contains":
                        spec = spec.and((root, query, cb) -> cb.like(root.get(criteria.getKey()),
                                "%" + criteria.getValue() + "%"));
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

        List<HuespedDTO> huespedDTOList = page.getContent().stream()
                .map(HuespedController::convertToDtoHuesped)
                .collect(Collectors.toList());

        Page<HuespedDTO> huespedDTOPage = new PageImpl<>(huespedDTOList, PageRequest.of(pageIndex, pageSize, sort),
                page.getTotalElements());

        return ResponseEntity.ok(huespedDTOPage);
    }

    @PostMapping("/dynamicFilterOr")
    public ResponseEntity<?> getFilteredByDynamicSearchOr(@RequestBody SearchRequest searchRequest) {

        List<SearchCriteria> searchCriteriaList = searchRequest.getListSearchCriteria();
        ListOrderCriteria orderCriteriaList = searchRequest.getListOrderCriteria();
        int pageSize = searchRequest.getPage().getPageSize();
        int pageIndex = searchRequest.getPage().getPageIndex();

        Specification<Huesped> spec = Specification.where(null);
        // Usamos este formato para recibir la fecha en formato dd-MM-yyyy
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        // Este formato es para convertir la fecha al formato yyyy-MM-dd
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        if (searchCriteriaList != null && !searchCriteriaList.isEmpty()) {

            String value = searchCriteriaList.get(0).getValue(); // Obtenemos el valor del primer criterio de búsqueda
            Date parsedDate = null;
    
            // Intentamos parsear el valor que viene en formato dd-MM-yyyy a una fecha válida
            try {
                parsedDate = inputDateFormat.parse(value);
            } catch (ParseException e) {
                parsedDate = null;  // Si no es una fecha válida, continuamos el flujo normal
            }
    
            // Si el valor es una fecha válida, realizamos la consulta nativa
            if (parsedDate != null) {
                // Convertimos la fecha al formato yyyy-MM-dd
                String formattedDate = outputDateFormat.format(parsedDate);
                Pageable pageable = PageRequest.of(pageIndex, pageSize);
                Page<Huesped> huespedesPage = huespedRepository.findHuespedesByDateWithPagination(formattedDate, pageable);
    
                // Convertimos los resultados a DTO
                List<HuespedDTO> huespedDTOList = huespedesPage.getContent().stream()
                        .map(HuespedController::convertToDtoHuesped)
                        .collect(Collectors.toList());
                
                Page<HuespedDTO> huespedDTOPage = new PageImpl<>(huespedDTOList, PageRequest.of(pageIndex, pageSize), huespedesPage.getTotalElements());
                
                return ResponseEntity.ok(huespedDTOPage);
            }
        }

        for (SearchCriteria criteria : searchCriteriaList) {            
            if (criteria.getKey().startsWith("habitacion.")) {
                switch (criteria.getKey()) {
                    case "habitacion.numero":
                        spec = spec.or((root, query, cb) -> cb.like(root.get("habitacion").get("numero"),
                                "%" + criteria.getValue() + "%"));
                        break;
                    case "habitacion.hotel.nombre":
                        spec = spec.or((root, query, cb) -> cb.like(root.get("habitacion").get("hotel").get("nombre"),
                                "%" + criteria.getValue() + "%"));
                        break;
                    case "habitacion.hotel.idUsuario":
                        spec = spec.or((root, query, cb) -> cb
                                .equal(root.get("habitacion").get("hotel").get("idUsuario"), criteria.getValue()));
                        break;
                    default:
                        throw new IllegalArgumentException("Clave de búsqueda no válida: " + criteria.getKey());
                }
            } else {
                switch (criteria.getOperation()) {
                    case "equals":
                        spec = spec.or((root, query, cb) -> cb.equal(root.get(criteria.getKey()), criteria.getValue()));
                        break;
                    case "contains":
                        spec = spec.or((root, query, cb) -> cb.like(root.get(criteria.getKey()),
                                "%" + criteria.getValue() + "%"));
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

        List<HuespedDTO> huespedDTOList = page.getContent().stream()
                .map(HuespedController::convertToDtoHuesped)
                .collect(Collectors.toList());

        Page<HuespedDTO> huespedDTOPage = new PageImpl<>(huespedDTOList, PageRequest.of(pageIndex, pageSize, sort),
                page.getTotalElements());

        return ResponseEntity.ok(huespedDTOPage);
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
                /*page = huespedRepository
                        .findByNombreCompletoContainingIgnoreCaseOrDniContainingIgnoreCaseOrEmailContainingIgnoreCase(
                                query, query, query, pageable);*/
                page = huespedRepository.findHuespedesByAllFilters(query, pageable);
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
        ModelMapper modelMapper = new ModelMapper();
        
        // Configurar mapeo personalizado para el campo nombreHotel
        modelMapper.typeMap(Huesped.class, HuespedDTO.class).addMappings(mapper -> {
            mapper.map(src -> src.getHabitacion().getHotel().getNombre(), HuespedDTO::setNombreHotel);
            mapper.map(src -> src.getHabitacion().getNumero(), HuespedDTO::setHabitacion);
        });
    
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
