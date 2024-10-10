package gz.hoteles.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import gz.hoteles.dto.HotelDTO;
import gz.hoteles.entities.Habitacion;
import gz.hoteles.entities.Hotel;
import gz.hoteles.entities.ServiciosHotelEnum;
import gz.hoteles.repositories.HotelRepository;
import gz.hoteles.servicio.IServicioHoteles;
import gz.hoteles.support.ListOrderCriteria;
import gz.hoteles.support.SearchCriteria;
import gz.hoteles.support.SearchRequest;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/hoteles")
public class HotelController {

    @Autowired
    HotelRepository hotelRepository;
    @Autowired
    IServicioHoteles servicioHoteles;

    private static final ModelMapper modelMapper = new ModelMapper();

    @GetMapping()
    public ResponseEntity<?> list() {
        List<Hotel> hoteles = hotelRepository.findAll();
        List<HotelDTO> hotelesDTO = convertToDtoHotelList(hoteles);
        if (hotelesDTO.size() > 0) {
            return ResponseEntity.ok(hotelesDTO);
        } else
            throw new ResponseStatusException(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable(name = "id") int id) {
        if (id <= 0 || Integer.valueOf(id) == null) {
            throw new IllegalArgumentException("El ID debe ser un número entero positivo");
        }
        Hotel hotel = hotelRepository.findById(id).orElse(null);
        if (hotel == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se encontró ningún hotel por el ID proporcionado");
        } else {
            HotelDTO hotelDTO = convertToDtoHotel(hotel);
            return ResponseEntity.ok(hotelDTO);
        }
    }

    @PostMapping("/dynamicFilterAnd")
    public ResponseEntity<?> getFilteredByDynamicSearchAnd(@RequestBody SearchRequest searchRequest) {

        List<SearchCriteria> searchCriteriaList = searchRequest.getListSearchCriteria();
        ListOrderCriteria orderCriteriaList = searchRequest.getListOrderCriteria();
        int pageSize = searchRequest.getPage().getPageSize();
        int pageIndex = searchRequest.getPage().getPageIndex();

        Specification<Hotel> spec = Specification.where(null);
        for (SearchCriteria criteria : searchCriteriaList) {
            switch (criteria.getOperation()) {
                case "equals":
                    if (criteria.getKey().startsWith("ubicacion.")) {
                        spec = spec.and((root, query, cb) -> cb.equal(
                                root.get("ubicacion").get(criteria.getKey().split("\\.")[1]), criteria.getValue()));
                    } else if (criteria.getKey().equals("servicios")) {
                        spec = spec.and((root, query, cb) -> cb
                                .isMember(ServiciosHotelEnum.valueOf(criteria.getValue()), root.get("servicios")));
                    } else {
                        spec = spec
                                .and((root, query, cb) -> cb.equal(root.get(criteria.getKey()), criteria.getValue()));
                    }
                    break;
                case "contains":
                    if (criteria.getKey().startsWith("ubicacion.")) {
                        spec = spec.and((root, query, cb) -> cb.like(
                                root.get("ubicacion").get(criteria.getKey().split("\\.")[1]),
                                "%" + criteria.getValue() + "%"));
                    } else if (criteria.getKey().equals("servicios")) {
                        spec = spec.and((root, query, cb) -> cb
                                .isMember(ServiciosHotelEnum.valueOf(criteria.getValue()), root.get("servicios")));
                    } else {
                        spec = spec.and((root, query, cb) -> cb.like(root.get(criteria.getKey()),
                                "%" + criteria.getValue() + "%"));
                    }
                    break;
                // Resto de las operaciones
                default:
                    throw new IllegalArgumentException("Operador de búsqueda no válido: " + criteria.getOperation());
            }
        }

        String sortByField = orderCriteriaList.getSortBy();
        String sortDirection = orderCriteriaList.getValueSortOrder().equalsIgnoreCase("asc") ? "ASC" : "DESC";
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortByField);

        Page<Hotel> page = hotelRepository.findAll(spec, PageRequest.of(pageIndex, pageSize, sort));

        List<HotelDTO> hotelDTOList = page.getContent().stream()
                .map(HotelController::convertToDtoHotel)
                .collect(Collectors.toList());

        Page<HotelDTO> hotelDTOPage = new PageImpl<>(hotelDTOList, PageRequest.of(pageIndex, pageSize, sort),
                page.getTotalElements());

        return ResponseEntity.ok(hotelDTOPage);
    }

    @PostMapping("/dynamicFilterOr")
    public ResponseEntity<?> getFilteredByDynamicSearchOr(@RequestBody SearchRequest searchRequest) {

        List<SearchCriteria> searchCriteriaList = searchRequest.getListSearchCriteria();
        ListOrderCriteria orderCriteriaList = searchRequest.getListOrderCriteria();
        int pageSize = searchRequest.getPage().getPageSize();
        int pageIndex = searchRequest.getPage().getPageIndex();

        Specification<Hotel> spec = Specification.where(null);

        Optional<SearchCriteria> idUsuarioCriteria = searchCriteriaList.stream()
            .filter(criteria -> "idUsuario".equals(criteria.getKey()))
            .findFirst();

        if (idUsuarioCriteria.isPresent()) {
            // Añadir filtro idUsuario en forma de AND
            spec = spec.and((root, query, cb) -> 
                cb.equal(root.get("idUsuario"), idUsuarioCriteria.get().getValue()));
        }

        for (SearchCriteria criteria : searchCriteriaList) {
            switch (criteria.getOperation()) {
                case "equals":
                    if (criteria.getKey().startsWith("ubicacion.")) {
                        spec = spec.or((root, query, cb) -> cb.equal(
                                root.get("ubicacion").get(criteria.getKey().split("\\.")[1]), criteria.getValue()));
                    } else if (criteria.getKey().equals("servicios")) {
                        spec = spec.or((root, query, cb) -> cb.isMember(ServiciosHotelEnum.valueOf(criteria.getValue()),
                                root.get("servicios")));
                    } else {
                        spec = spec.or((root, query, cb) -> cb.equal(root.get(criteria.getKey()), criteria.getValue()));
                    }
                    break;
                case "contains":
                    if (criteria.getKey().startsWith("ubicacion.")) {
                        spec = spec.or((root, query, cb) -> cb.like(
                                root.get("ubicacion").get(criteria.getKey().split("\\.")[1]),
                                "%" + criteria.getValue() + "%"));
                    } else if (criteria.getKey().equals("servicios")) {
                        spec = spec.or((root, query, cb) -> cb.isMember(ServiciosHotelEnum.valueOf(criteria.getValue()),
                                root.get("servicios")));
                    } else {
                        spec = spec.or((root, query, cb) -> cb.like(root.get(criteria.getKey()),
                                "%" + criteria.getValue() + "%"));
                    }
                    break;
                // Otras operaciones...
                default:
                    throw new IllegalArgumentException("Operador de búsqueda no válido: " + criteria.getOperation());
            }
        }

        String sortByField = orderCriteriaList.getSortBy();
        String sortDirection = orderCriteriaList.getValueSortOrder().equalsIgnoreCase("asc") ? "ASC" : "DESC";
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortByField);

        Page<Hotel> page = hotelRepository.findAll(spec, PageRequest.of(pageIndex, pageSize, sort));

        List<HotelDTO> hotelDTOList = page.getContent().stream()
                .map(HotelController::convertToDtoHotel)
                .collect(Collectors.toList());

        Page<HotelDTO> hotelDTOPage = new PageImpl<>(hotelDTOList, PageRequest.of(pageIndex, pageSize, sort),
                page.getTotalElements());

        return ResponseEntity.ok(hotelDTOPage);
    }

    @GetMapping("/{id}/full")
    public ResponseEntity<?> getHotelFull(@PathVariable(name = "id") int id) {
        if (id <= 0  || Integer.valueOf(id) == null) {
            throw new IllegalArgumentException("El ID debe ser un número entero positivo");
        }
        Hotel hotel = hotelRepository.findById(id).orElse(null);
        if (hotel == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se encontró ningún hotel por el ID proporcionado");
        } else {
            return ResponseEntity.ok(hotel);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable(name = "id") int id, @RequestBody Hotel input) {
        if (id <= 0 || Integer.valueOf(id) == null) {
            throw new IllegalArgumentException("El ID debe ser un número entero positivo");
        }
        Hotel find = hotelRepository.findById(id).orElse(null);
        if (find != null) {
            find.setDireccion(input.getDireccion());
            find.setEmail(input.getEmail());
            find.setNombre(input.getNombre());
            find.setTelefono(input.getTelefono());
            find.setSitioWeb(input.getSitioWeb());
            find.setServicios(input.getServicios());
        } else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se encontró ningún hotel con el ID proporcionado");
        Hotel save = hotelRepository.save(find);
        return ResponseEntity.ok(convertToDtoHotel(save));
    }

    @PostMapping
    public ResponseEntity<?> post(@RequestBody Hotel input) { // Los campos nombre y direccion seran obligatorios
        if (input.getNombre() == null || input.getNombre().isEmpty() ||
                input.getDireccion() == null || input.getDireccion().isEmpty()) {
            throw new IllegalArgumentException("Los campos 'nombre' y 'direccion' son obligatorios");
        }
        Hotel save = servicioHoteles.crearHotel(input);
        return ResponseEntity.ok(convertToDtoHotel(save));
    }

    @Operation(summary = "Añadir habitación al hotel")
    @PostMapping("/{id}/habitaciones")
    public ResponseEntity<?> anadirHabitacion(@PathVariable(name = "id") int id, @RequestBody Habitacion input) {
        if (id <= 0 || Integer.valueOf(id) == null) {
            throw new IllegalArgumentException("El ID debe ser un número entero positivo");
        }
        Hotel h = servicioHoteles.anadirHabitacion(id, input); // Comprobacion NOT_FOUND en la funcion
        return ResponseEntity.ok(convertToDtoHotel(h));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable(name = "id") int id) {
        if (id <= 0 || Integer.valueOf(id) == null) {
            throw new IllegalArgumentException("El ID debe ser un número entero positivo");
        }
        Hotel findById = hotelRepository.findById(id).orElse(null);
        if (findById != null) {
            hotelRepository.delete(findById);
        } else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se encontró ningún hotel por el ID proporcionado");
        return ResponseEntity.ok().build();
    }

    public HotelDTO getHotelByIdUsuario(int idUsuario) {
        Hotel hotel = hotelRepository.findByIdUsuario(idUsuario);
        return convertToDtoHotel(hotel);
    }

    /* ====== MAPPER ====== */

    public static HotelDTO convertToDtoHotel(Hotel hotel) {
        if (hotel == null) {
            throw new IllegalArgumentException("El hotel no puede ser nulo");
        }

        return modelMapper.map(hotel, HotelDTO.class);
    }

    public static List<HotelDTO> convertToDtoHotelList(List<Hotel> hoteles) {
        return hoteles.stream()
                .map(hotel -> convertToDtoHotel(hotel))
                .collect(Collectors.toList());
    }

}
