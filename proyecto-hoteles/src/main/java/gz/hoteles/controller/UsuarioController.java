package gz.hoteles.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import gz.hoteles.entities.Usuario;
import gz.hoteles.repositories.UsuarioRepository;
import gz.hoteles.servicio.IServicioUsuarios;
import io.swagger.v3.oas.annotations.Operation;

// ? Controlador hecho de cara a gestionar el perfil del usuario en el front 

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    UsuarioRepository usuariosRepository;

    @Autowired
    IServicioUsuarios servicioUsuarios;

    @GetMapping()
    public ResponseEntity<?> list() {
        List<Usuario> usuarios = usuariosRepository.findAll();
        if (!usuarios.isEmpty()) {
            return ResponseEntity.ok(usuarios);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    // ? Para el perfil del usuario en el front
    @GetMapping("/getUsuarioByUsername") 
    public ResponseEntity<?> getUsuarioByUsername(@RequestParam String username) {
        Usuario u = servicioUsuarios.getUsuarioByUsername(username);
        return ResponseEntity.ok(u);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable(name = "id") int id, @RequestBody Usuario input) {
        if (id <= 0 || Integer.valueOf(id) == null) {
            throw new IllegalArgumentException("El ID debe ser un número entero positivo");
        }
        Usuario find = usuariosRepository.findById(id).orElse(null);
        if (find != null) {
            find.setNombre(input.getNombre());
            find.setUsername(input.getUsername());
            find.setPassword(input.getPassword());
            find.setEmail(input.getEmail());
            find.setFechaNacimiento(input.getFechaNacimiento());
        } else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se encontró ningún usuario con el ID proporcionado");
        Usuario save = usuariosRepository.save(find);
        return ResponseEntity.ok(save);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        servicioUsuarios.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Filtrado de usuarios por todos sus parámetros a través de un solo String")
    @GetMapping("/magicFilter")
    public ResponseEntity<?> getUsuariosByMagicFilter(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam("pageNumber") int pagina,
            @RequestParam("itemsPerPage") int itemsPorPagina,
            @RequestParam(value = "valueSortOrder") String valueSortOrder,
            @RequestParam(value = "sortBy") String sortBy) {

        // Definir la paginación y clasificación
        Sort.Direction direction = Sort.Direction.fromString(valueSortOrder.toUpperCase());
        Pageable pageable = PageRequest.of(pagina, itemsPorPagina, Sort.by(direction, sortBy));

        // Realizar la búsqueda en la base de datos
        Page<Usuario> page;
        long totalItems; // Variable para almacenar el número total de elementos coincidentes

        if (query == null || query.isEmpty()) {
            page = usuariosRepository.findAll(pageable);
            totalItems = usuariosRepository.count(); // Obtener el número total de servicios en la base de datos
        } else {

            // Intentar parsear el parámetro 'query' como fecha
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date fechaQuery = null;
            try {
                // Intenta parsear la fecha completa
                fechaQuery = dateFormat.parse(query);
            } catch (ParseException e) {
                
            }

            if (fechaQuery != null) {
                // Si se pudo parsear como fecha, se realiza la búsqueda teniendo en cuenta la
                // fecha
                page = usuariosRepository.findByQueryAndDate(query, fechaQuery, pageable);
            } else {
                // Si no se pudo parsear como fecha, se realiza la búsqueda sin tener en cuenta
                // las fechas
                page = usuariosRepository
                        .findByNombreContainingIgnoreCaseOrUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                                query, query, query, pageable);
            }

            totalItems = page.getTotalElements(); // Obtener el número total de elementos coincidentes
        }

        List<Usuario> usuarios = page.getContent();

        // Crear un objeto JSON para la respuesta que incluya tanto la lista de
        // roles como el número total de elementos
        Map<String, Object> response = new HashMap<>();
        response.put("usuarios", usuarios);
        response.put("totalItems", totalItems);

        if (!usuarios.isEmpty()) {
            return ResponseEntity.ok(response);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se encontraron usuarios por los parámetros proporcionados");
        }
    }

}
