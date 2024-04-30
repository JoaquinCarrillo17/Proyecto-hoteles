package gz.hoteles.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import gz.hoteles.entities.Usuario;
import gz.hoteles.repositories.UsuarioRepository;
import gz.hoteles.security.JwtTokenProvider;
import gz.hoteles.servicio.IServicioUsuarios;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    UsuarioRepository usuariosRepository;

    @Autowired
    IServicioUsuarios servicioUsuarios;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.secret}")
    private String secretKey;

    @GetMapping()
    public ResponseEntity<?> list() {
        List<Usuario> usuarios = usuariosRepository.findAll();
        if (!usuarios.isEmpty()) {
            return ResponseEntity.ok(usuarios);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping("/signUp")
    public ResponseEntity<?> signUp(@RequestBody Usuario usuario) {
        servicioUsuarios.signUp(usuario);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
        if (servicioUsuarios.verificarCredenciales(username, password)) {
            String token = jwtTokenProvider.createToken(username);
            return ResponseEntity.ok(token);
        } else
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Contraseña incorrecta");
    }

    /*private String generarToken(String username) {
        String key = secretKey;
        Date caducidad = Date.from(Instant.now().plusSeconds(3600));// 1 hora de validez

        // Generar el token JWT
        String token = Jwts.builder()
                .setSubject(username)
                .setExpiration(caducidad)
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();

        return token;
    }*/

}
