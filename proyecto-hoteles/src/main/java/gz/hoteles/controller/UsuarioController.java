package gz.hoteles.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    UsuarioRepository usuariosRepository;

    @Autowired
    IServicioUsuarios servicioUsuarios;

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
    
}
