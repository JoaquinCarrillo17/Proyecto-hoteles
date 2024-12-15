package gz.hoteles.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import gz.hoteles.entities.Permiso;
import gz.hoteles.repositories.PermisoRepository;

import java.util.Optional;

@RequestMapping("/permisos")
@RestController
public class PermisoController {

    @Autowired
    PermisoRepository permisosRepository;

    // Crear un permiso
    @PostMapping()
    public ResponseEntity<?> crearPermiso(@RequestBody Permiso permiso) {
        permisosRepository.save(permiso);
        return ResponseEntity.ok().build();
    }

    // Obtener todos los permisos
    @GetMapping()
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(permisosRepository.findAll());
    }

    // Obtener un permiso por su ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getPermisoById(@PathVariable int id) {
        Optional<Permiso> permiso = permisosRepository.findById(id);
        if (permiso.isPresent()) {
            return ResponseEntity.ok(permiso.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró ningún permiso con el ID proporcionado.");
        }
    }

    // Actualizar un permiso
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePermiso(@PathVariable int id, @RequestBody Permiso permisoInput) {
        Optional<Permiso> permisoOptional = permisosRepository.findById(id);
        if (permisoOptional.isPresent()) {
            Permiso permiso = permisoOptional.get();
            permiso.setNombre(permisoInput.getNombre());
            permiso.setDescripcion(permisoInput.getDescripcion());
            permisosRepository.save(permiso);
            return ResponseEntity.ok(permiso);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró ningún permiso con el ID proporcionado.");
        }
    }

    // Eliminar un permiso
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePermiso(@PathVariable int id) {
        if (permisosRepository.existsById(id)) {
            permisosRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró ningún permiso con el ID proporcionado.");
        }
    }
}
