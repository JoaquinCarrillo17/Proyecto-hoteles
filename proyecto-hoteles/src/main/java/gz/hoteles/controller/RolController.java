package gz.hoteles.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gz.hoteles.entities.Rol;
import gz.hoteles.servicio.IServicioRoles;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/roles")
public class RolController {

    @Autowired
    IServicioRoles servicioRoles;

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


}
