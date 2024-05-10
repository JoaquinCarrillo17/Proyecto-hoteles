package gz.hoteles.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import gz.hoteles.entities.Rol;
import gz.hoteles.repositories.RolRepository;
import gz.hoteles.servicio.IServicioRoles;
import io.swagger.v3.oas.annotations.Operation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;



@RestController
@RequestMapping("/roles")
public class RolController {

    @Autowired
    IServicioRoles servicioRoles;

    @Autowired
    RolRepository rolesRepository;

    @PostMapping()
    public ResponseEntity<?> crearRol(@RequestBody Rol rol) {
        servicioRoles.crearRol(rol);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> añadirRolIndirecto(@PathVariable(name = "id") int idRol, @RequestParam String rol) {
        servicioRoles.añadirRolIndirecto(idRol, rol);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> borrarRolIndirecto(@PathVariable(name = "id") int idRol, @RequestParam String rol) {
        servicioRoles.borrarRolIndirecto(idRol, rol);
        return ResponseEntity.ok().build();
    }

    @GetMapping()
    public ResponseEntity<?> getAll() {
        List<Rol> roles = servicioRoles.getAll();
        return ResponseEntity.ok(roles); 
    }

    @DeleteMapping() 
    public ResponseEntity<?> removeAll() {
        List<Rol> roles = servicioRoles.getAll();
        for (Rol r : roles) {
            rolesRepository.delete(r);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRolById(@PathVariable (name="id") int id) {
        Rol r = servicioRoles.getById(id);
        return ResponseEntity.ok(r);
    }

    @Operation(summary = "Filtrado de roles por todos sus parámetros a través de un solo String")
    @GetMapping("/magicFilter")
    public ResponseEntity<?> getRolesByMagicFilter(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam("pageNumber") int pagina,
            @RequestParam("itemsPerPage") int itemsPorPagina,
            @RequestParam(value = "valueSortOrder") String valueSortOrder,
            @RequestParam(value = "sortBy") String sortBy) {

        // Definir la paginación y clasificación
        Sort.Direction direction = Sort.Direction.fromString(valueSortOrder.toUpperCase());
        Pageable pageable = PageRequest.of(pagina, itemsPorPagina, Sort.by(direction, sortBy));

        // Realizar la búsqueda en la base de datos
        Page<Rol> page;
        long totalItems; // Variable para almacenar el número total de elementos coincidentes

        if (query == null || query.isEmpty()) {
            page = rolesRepository.findAll(pageable);
            totalItems = rolesRepository.count(); // Obtener el número total de servicios en la base de datos
        } else {
                page = rolesRepository.findByNombreContainingIgnoreCaseOrDescripcionContainingIgnoreCase(
                        query, query, pageable);

            totalItems = page.getTotalElements(); // Obtener el número total de elementos coincidentes
        }

        List<Rol> roles = page.getContent();

        // Crear un objeto JSON para la respuesta que incluya tanto la lista de
        // roles como el número total de elementos
        Map<String, Object> response = new HashMap<>();
        response.put("roles", roles);
        response.put("totalItems", totalItems);

        if (!roles.isEmpty()) {
            return ResponseEntity.ok(response);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se encontraron roles por los parámetros proporcionados");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable(name = "id") int id, @RequestBody Rol input) {
        if (id <= 0  || Integer.valueOf(id) == null) {
            throw new IllegalArgumentException("El ID debe ser un número entero positivo");
        }
        Rol find = rolesRepository.findById(id).orElse(null);
        if (find != null) {
            find.setNombre(input.getNombre());
            find.setDescripcion(input.getDescripcion());
            find.setPermisos(input.getPermisos());
        } else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se encontró ningún rol por el ID proporcionado");
        Rol save = rolesRepository.save(find);
        return ResponseEntity.ok(save);
    }


}
