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

import gz.hoteles.dto.HotelDTO;
import gz.hoteles.entities.Habitacion;
import gz.hoteles.entities.Hotel;
import gz.hoteles.entities.Servicio;
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
        if (id <= 0) {
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

    @GetMapping("/filteredByName")
    public ResponseEntity<?> getHotelByNombre(@RequestParam String nombre, @RequestParam int pages) {
        if (nombre == null || nombre.isEmpty()) {
            throw new IllegalArgumentException("El parámetro 'nombre' no puede estar vacío");
        }
        Pageable pageable = PageRequest.of(0, pages);
        Page<Hotel> page = hotelRepository.getHotelByNombre(nombre, pageable);
        List<Hotel> hoteles = page.getContent();
        List<HotelDTO> hotelesDTO = convertToDtoHotelList(hoteles);
        if (hotelesDTO.size() > 0) {
            return ResponseEntity.ok(hotelesDTO);
        } else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se encontró ningún hotel por el nombre '" + nombre + "'");
    }

    @GetMapping("/filteredByAddress")
    public ResponseEntity<?> getHotelByDireccion(@RequestParam String direccion, @RequestParam int pages) {
        if (direccion == null || direccion.isEmpty()) {
            throw new IllegalArgumentException("El parámetro 'nombre' no puede estar vacío");
        }
        Pageable pageable = PageRequest.of(0, pages);
        Page<Hotel> page = hotelRepository.getHotelByDireccion(direccion, pageable);
        List<Hotel> hoteles = page.getContent();
        List<HotelDTO> hotelesDTO = convertToDtoHotelList(hoteles);
        if (hotelesDTO.size() > 0) {
            return ResponseEntity.ok(hotelesDTO);
        } else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se encontró ningún hotel por la dirección '" + direccion + "'");
    }

    @GetMapping("/filteredByPhoneNumber")
    public ResponseEntity<?> getHotelByTelefono(@RequestParam String telefono, @RequestParam int pages) {
        if (telefono == null || telefono.isEmpty()) {
            throw new IllegalArgumentException("El parámetro 'nombre' no puede estar vacío");
        }
        Pageable pageable = PageRequest.of(0, pages);
        Page<Hotel> page = hotelRepository.getHotelByTelefono(telefono, pageable);
        List<Hotel> hoteles = page.getContent();
        List<HotelDTO> hotelesDTO = convertToDtoHotelList(hoteles);
        if (hotelesDTO.size() > 0) {
            return ResponseEntity.ok(hotelesDTO);
        } else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se encontró ningún hotel por el teléfono '" + telefono + "'");
    }

    @GetMapping("/filteredByEmail")
    public ResponseEntity<?> getHotelByEmail(@RequestParam String email, @RequestParam int pages) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("El parámetro 'nombre' no puede estar vacío");
        }
        Pageable pageable = PageRequest.of(0, pages);
        Page<Hotel> page = hotelRepository.getHotelByEmail(email, pageable);
        List<Hotel> hoteles = page.getContent();
        List<HotelDTO> hotelesDTO = convertToDtoHotelList(hoteles);
        if (hotelesDTO.size() > 0) {
            return ResponseEntity.ok(hotelesDTO);
        } else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se encontró ningún hotel por el email '" + email + "'");
    }

    @GetMapping("/filteredByWebsite")
    public ResponseEntity<?> getHotelBySitioWeb(@RequestParam String sitioWeb, @RequestParam int pages) {
        if (sitioWeb == null || sitioWeb.isEmpty()) {
            throw new IllegalArgumentException("El parámetro 'nombre' no puede estar vacío");
        }
        Pageable pageable = PageRequest.of(0, pages);
        Page<Hotel> page = hotelRepository.getHotelBySitioWeb(sitioWeb, pageable);
        List<Hotel> hoteles = page.getContent();
        List<HotelDTO> hotelesDTO = convertToDtoHotelList(hoteles);
        if (hotelesDTO.size() > 0) {
            return ResponseEntity.ok(hotelesDTO);
        } else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se encontró ningún hotel por el sitio web '" + sitioWeb + "'");
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

        Specification<Hotel> spec = Specification.where(null);
        for (SearchCriteria criteria : searchCriteriaList) {
            switch (criteria.getOperation()) {
                case "equals":
                    spec = spec.and((root, query, cb) -> cb.equal(root.get(criteria.getKey()), criteria.getValue()));
                    break;
                case "not equals":
                    spec = spec.and((root, query, cb) -> cb.notEqual(root.get(criteria.getKey()), criteria.getValue()));
                    break;
                case "contains":
                    spec = spec.and((root, query, cb) -> cb.like(root.get(criteria.getKey()), "%" + criteria.getValue() + "%"));
                    break;
                case "greater than":
                    spec = spec.and((root, query, cb) -> cb.greaterThan(root.get(criteria.getKey()), criteria.getValue()));
                    break;
                case "less than":
                    spec = spec.and((root, query, cb) -> cb.lessThan(root.get(criteria.getKey()), criteria.getValue()));
                    break;
                case "greater or equals than":
                    spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get(criteria.getKey()), criteria.getValue()));
                    break;
                case "less or equals than":
                    spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get(criteria.getKey()), criteria.getValue()));
                    break;
                default:
                    throw new IllegalArgumentException("Operador de búsqueda no válido: " + criteria.getOperation());
            }
        }

        String sortByField = orderCriteriaList.getValueSortOrder();
        String sortDirection = orderCriteriaList.getSortBy().equalsIgnoreCase("asc") ? "ASC" : "DESC";
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortByField);

        Page<Hotel> page = hotelRepository.findAll(spec, PageRequest.of(pageIndex, pageSize, sort));

        List<Hotel> hoteles = page.getContent();
        List<HotelDTO> hotelesDTO = convertToDtoHotelList(hoteles);
        if (hotelesDTO.size() > 0) {
            return ResponseEntity.ok(hotelesDTO);
        } else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se encontraron hoteles por los parámetros proporcionados");

    }

    @Operation(summary = "Filtrado con GET por todos sus parámetros")
    @GetMapping("/filteredByEverything")
    public ResponseEntity<?> getFilteredByEverything(@RequestParam(required = false) String nombre,
            @RequestParam(required = false) String direccion, @RequestParam(required = false) String telefono,
            @RequestParam(required = false) String email, @RequestParam(required = false) String sitioWeb, @RequestParam int pages) {

        Page<Hotel> page = null;

        if (nombre != null && direccion != null && telefono != null && email != null && sitioWeb != null) {
            page = hotelRepository.findByNombreAndDireccionAndTelefonoAndEmailAndSitioWeb(nombre, direccion, telefono,
                    email, sitioWeb, PageRequest.of(0, pages));
        } else if (nombre != null && direccion != null && telefono != null && email != null) {
            page = hotelRepository.findByNombreAndDireccionAndTelefonoAndEmail(nombre, direccion, telefono, email,
                    PageRequest.of(0, pages));
        } else if (nombre != null && direccion != null && telefono != null) {
            page = hotelRepository.findByNombreAndDireccionAndTelefono(nombre, direccion, telefono,
                    PageRequest.of(0, pages));
        } else if (nombre != null && direccion != null && email != null) {
            page = hotelRepository.findByNombreAndDireccionAndEmail(nombre, direccion, email, PageRequest.of(0, pages));
        } else if (nombre != null && direccion != null && sitioWeb != null) {
            page = hotelRepository.findByNombreAndDireccionAndSitioWeb(nombre, direccion, sitioWeb,
                    PageRequest.of(0, pages));
        } else if (nombre != null && telefono != null && email != null) {
            page = hotelRepository.findByNombreAndTelefonoAndEmail(nombre, telefono, email, PageRequest.of(0, pages));
        } else if (nombre != null && telefono != null && sitioWeb != null) {
            page = hotelRepository.findByNombreAndTelefonoAndSitioWeb(nombre, telefono, sitioWeb,
                    PageRequest.of(0, pages));
        } else if (nombre != null && email != null && sitioWeb != null) {
            page = hotelRepository.findByNombreAndEmailAndSitioWeb(nombre, email, sitioWeb, PageRequest.of(0, pages));
        } else if (direccion != null && telefono != null && email != null && sitioWeb != null) {
            page = hotelRepository.findByDireccionAndTelefonoAndEmailAndSitioWeb(direccion, telefono, email, sitioWeb,
                    PageRequest.of(0, pages));
        } else if (direccion != null && telefono != null && email != null) {
            page = hotelRepository.findByDireccionAndTelefonoAndEmail(direccion, telefono, email,
                    PageRequest.of(0, pages));
        } else if (direccion != null && telefono != null && sitioWeb != null) {
            page = hotelRepository.findByDireccionAndTelefonoAndSitioWeb(direccion, telefono, sitioWeb,
                    PageRequest.of(0, pages));
        } else if (direccion != null && email != null && sitioWeb != null) {
            page = hotelRepository.findByDireccionAndEmailAndSitioWeb(direccion, email, sitioWeb,
                    PageRequest.of(0, pages));
        } else if (telefono != null && email != null && sitioWeb != null) {
            page = hotelRepository.findByTelefonoAndEmailAndSitioWeb(telefono, email, sitioWeb,
                    PageRequest.of(0, pages));
        } else if (nombre != null && direccion != null) {
            page = hotelRepository.findByNombreAndDireccion(nombre, direccion, PageRequest.of(0, pages));
        } else if (nombre != null && telefono != null) {
            page = hotelRepository.findByNombreAndTelefono(nombre, telefono, PageRequest.of(0, pages));
        } else if (nombre != null && email != null) {
            page = hotelRepository.findByNombreAndEmail(nombre, email, PageRequest.of(0, pages));
        } else if (nombre != null && sitioWeb != null) {
            page = hotelRepository.findByNombreAndSitioWeb(nombre, sitioWeb, PageRequest.of(0, pages));
        } else if (direccion != null && telefono != null) {
            page = hotelRepository.findByDireccionAndTelefono(direccion, telefono, PageRequest.of(0, pages));
        } else if (direccion != null && email != null) {
            page = hotelRepository.findByDireccionAndEmail(direccion, email, PageRequest.of(0, pages));
        } else if (direccion != null && sitioWeb != null) {
            page = hotelRepository.findByDireccionAndSitioWeb(direccion, sitioWeb, PageRequest.of(0, pages));
        } else if (telefono != null && email != null) {
            page = hotelRepository.findByTelefonoAndEmail(telefono, email, PageRequest.of(0, pages));
        } else if (telefono != null && sitioWeb != null) {
            page = hotelRepository.findByTelefonoAndSitioWeb(telefono, sitioWeb, PageRequest.of(0, pages));
        } else if (email != null && sitioWeb != null) {
            page = hotelRepository.findByEmailAndSitioWeb(email, sitioWeb, PageRequest.of(0, pages));
        } else if (nombre != null) {
            page = hotelRepository.findByNombre(nombre, PageRequest.of(0, pages));
        } else if (direccion != null) {
            page = hotelRepository.findByDireccion(direccion, PageRequest.of(0, pages));
        } else if (telefono != null) {
            page = hotelRepository.findByTelefono(telefono, PageRequest.of(0, pages));
        } else if (email != null) {
            page = hotelRepository.findByEmail(email, PageRequest.of(0, pages));
        } else if (sitioWeb != null) {
            page = hotelRepository.findBySitioWeb(sitioWeb, PageRequest.of(0, pages));
        } else {
            page = hotelRepository.findAll(PageRequest.of(0, pages));
        }

        List<Hotel> hoteles = page.getContent();
        List<HotelDTO> hotelesDTO = convertToDtoHotelList(hoteles);
        if (hotelesDTO.size() > 0) {
            return ResponseEntity.ok(hotelesDTO);
        } else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se encontraron hoteles por los parámetros proporcionados");

    }

    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable(name = "id") int id, @RequestBody Hotel input) {
        Hotel find = hotelRepository.findById(id).orElse(null);
        if (find != null) {
            find.setDireccion(input.getDireccion());
            find.setEmail(input.getEmail());
            find.setNombre(input.getNombre());
            find.setTelefono(input.getTelefono());
            find.setSitioWeb(input.getSitioWeb());
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

    @Operation(summary = "Añadir servicio al hotel")
    @PostMapping("/{id}/servicios")
    public ResponseEntity<?> anadirServicio(@PathVariable(name = "id") int id, @RequestBody Servicio input) {
        Hotel h = servicioHoteles.anadirServicio(id, input); // Comprobacion NOT_FOUND en la funcion
        return ResponseEntity.ok(convertToDtoHotel(h));
    }

    @Operation(summary = "Añadir habitación al hotel")
    @PostMapping("/{id}/habitaciones")
    public ResponseEntity<?> anadirHabitacion(@PathVariable(name = "id") int id, @RequestBody Habitacion input) {
        Hotel h = servicioHoteles.anadirHabitacion(id, input); // Comprobacion NOT_FOUND en la funcion
        return ResponseEntity.ok(convertToDtoHotel(h));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable(name = "id") int id) {
        if (id <= 0) {
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

    /* ====== MAPPER ====== */

    public static HotelDTO convertToDtoHotel(Hotel hotel) {
        return modelMapper.map(hotel, HotelDTO.class);
    }

    public static List<HotelDTO> convertToDtoHotelList(List<Hotel> hoteles) {
        return hoteles.stream()
                .map(hotel -> convertToDtoHotel(hotel))
                .collect(Collectors.toList());
    }

}
