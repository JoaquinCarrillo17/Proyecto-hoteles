package gz.hoteles.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gz.hoteles.entities.Permiso;
import gz.hoteles.repositories.PermisoRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RequestMapping("/permisos")
@RestController
public class PermisoController {

    @Autowired
    PermisoRepository permisosRepository;

    @PostMapping()
    public ResponseEntity<?> crearPermiso(@RequestBody Permiso permiso) {
        permisosRepository.save(permiso);
        return ResponseEntity.ok().build();
    }

    @GetMapping()
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(permisosRepository.findAll());
    }
    
    
}
