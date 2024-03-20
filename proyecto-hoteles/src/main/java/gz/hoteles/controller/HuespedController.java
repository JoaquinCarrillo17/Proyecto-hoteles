package gz.hoteles.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

import gz.hoteles.dto.HuespedDTO;
import gz.hoteles.entities.Huesped;
import gz.hoteles.entities.JSONMapper;
import gz.hoteles.repositories.HuespedRepository;

@RestController
@RequestMapping("/huespedes")
public class HuespedController {

    @Autowired
    HuespedRepository huespedRepository;

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
        if (id <= 0) {
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
    public ResponseEntity<?> getHuespedesByFechaEntrada(@RequestParam Date fecha, @RequestParam int pages) {
        if (fecha == null) {
            throw new IllegalArgumentException("El parámetro 'fecha' no puede estar vacío");
        }
        Pageable pageable = PageRequest.of(0, pages);
        Page<Huesped> page = huespedRepository.getHuespedesByFechaEntrada(fecha, pageable);
        List<Huesped> huespedes = page.getContent();
        List<HuespedDTO> huespedesDTO = convertToDtoHuespedList(huespedes);
        if (huespedesDTO.size() > 0) {
            return ResponseEntity.ok(huespedesDTO);
        } else
            throw new ResponseStatusException(HttpStatus.NO_CONTENT,
                    "No se encontró ningún huésped con fecha de entrada '" + fecha + "'");
    }

    @GetMapping("/filteredByCheckOutDate")
    public ResponseEntity<?> getHuespedesByFechaSalida(@RequestParam Date fecha, @RequestParam int pages) {
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

    @PostMapping("/dynamicSearch")
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
            } catch (ParseException e) {
                throw new IllegalArgumentException("Error al parsear la fecha: " + e.getMessage());
            }
        }

        Page<Huesped> page = switch (field) {
            case "nombre" -> huespedRepository.findByNombreCompletoContainingIgnoreCase(value,
                    PageRequest.of(0, json.getPages(), Sort.by(Sort.Direction.fromString(sortDirection), sortByField)));
            case "dni" -> huespedRepository.findByDniEquals(value,
                    PageRequest.of(0, json.getPages(), Sort.by(Sort.Direction.fromString(sortDirection), sortByField)));
            case "email" -> huespedRepository.findByEmailEquals(value,
                    PageRequest.of(0, json.getPages(), Sort.by(Sort.Direction.fromString(sortDirection), sortByField)));
            case "fecha entrada" -> huespedRepository.findByFechaCheckInEquals(date,
                    PageRequest.of(0, json.getPages(), Sort.by(Sort.Direction.fromString(sortDirection), sortByField)));
            case "fecha salida" -> huespedRepository.findByFechaCheckOutEquals(date,
                    PageRequest.of(0, json.getPages(), Sort.by(Sort.Direction.fromString(sortDirection), sortByField)));
            default -> throw new IllegalArgumentException("No se puede filtrar por '" + field + "'");
        };

        List<Huesped> huespedes = page.getContent();
        List<HuespedDTO> huespedesDTO = convertToDtoHuespedList(huespedes);
        if (huespedesDTO.size() > 0) {
            return ResponseEntity.ok(huespedesDTO);
        } else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se encontraron hoteles con " + json.getField() + " = " + json.getValue());
    }

    @PostMapping("/dynamicSearchWithDateRange")
    public ResponseEntity<?> getHuespedesFilteredByParamWithDateRange(@RequestBody JSONMapper json,
            @RequestParam(name = "fechaEntrada", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaEntrada,
            @RequestParam(name = "fechaSalida", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaSalida) {
        if (json == null || json.getField() == null || json.getPages() <= 0
                || json.getSortBy() == null) {
            throw new IllegalArgumentException("Falta uno o más campos requeridos en el JSON");
        }
        System.out.println(fechaEntrada);
        ;
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
            getHuespedesFilteredByParam(json);
        } else {
            if (fechaEntrada == null) {
                page = switch (field) {
                    case "nombre" -> huespedRepository.findByNombreCompletoContainingIgnoreCaseAndFechaCheckOutBefore(
                            value, fechaSalida, PageRequest.of(0, json.getPages(),
                                    Sort.by(Sort.Direction.fromString(sortDirection), sortByField)));
                    case "dni" ->
                        huespedRepository.findByDniEqualsAndFechaCheckOutBefore(value, fechaSalida, PageRequest.of(0,
                                json.getPages(), Sort.by(Sort.Direction.fromString(sortDirection), sortByField)));
                    case "email" ->
                        huespedRepository.findByEmailEqualsAndFechaCheckOutBefore(value, fechaSalida, PageRequest.of(0,
                                json.getPages(), Sort.by(Sort.Direction.fromString(sortDirection), sortByField)));
                    default -> throw new IllegalArgumentException("No se puede filtrar por '" + field + "'");
                };
            } else if (fechaSalida == null) {
                page = switch (field) {
                    case "nombre" -> huespedRepository.findByNombreCompletoContainingIgnoreCaseAndFechaCheckInAfter(
                            value, fechaEntrada, PageRequest.of(0, json.getPages(),
                                    Sort.by(Sort.Direction.fromString(sortDirection), sortByField)));
                    case "dni" -> huespedRepository.findByDniEqualsAndFechaCheckInAfter(value, fechaEntrada, PageRequest
                            .of(0, json.getPages(), Sort.by(Sort.Direction.fromString(sortDirection), sortByField)));
                    case "email" ->
                        huespedRepository.findByEmailEqualsAndFechaCheckInAfter(value, fechaEntrada, PageRequest.of(0,
                                json.getPages(), Sort.by(Sort.Direction.fromString(sortDirection), sortByField)));
                    default -> throw new IllegalArgumentException("No se puede filtrar por '" + field + "'");
                };
            } else {
                page = switch (field) {
                    case "nombre" -> huespedRepository
                            .findByNombreCompletoContainingIgnoreCaseAndFechaCheckInAfterAndFechaCheckOutBefore(value,
                                    fechaEntrada, fechaSalida, PageRequest.of(0, json.getPages(),
                                            Sort.by(Sort.Direction.fromString(sortDirection), sortByField)));
                    case "dni" -> huespedRepository.findByDniEqualsAndFechaCheckInAfterAndFechaCheckOutBefore(value,
                            fechaEntrada, fechaSalida, PageRequest.of(0, json.getPages(),
                                    Sort.by(Sort.Direction.fromString(sortDirection), sortByField)));
                    case "email" -> huespedRepository.findByEmailEqualsAndFechaCheckInAfterAndFechaCheckOutBefore(value,
                            fechaEntrada, fechaSalida, PageRequest.of(0, json.getPages(),
                                    Sort.by(Sort.Direction.fromString(sortDirection), sortByField)));
                    default -> throw new IllegalArgumentException("No se puede filtrar por '" + field + "'");
                };
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

    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable(name = "id") int id, @RequestBody Huesped input) {
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
        if (id <= 0) {
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

}
