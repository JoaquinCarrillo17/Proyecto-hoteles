package gz.hoteles.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import gz.hoteles.entities.Usuario;
import gz.hoteles.repositories.UsuarioRepository;

@Service
public class ServicioUsuarios implements IServicioUsuarios{

    @Autowired
    UsuarioRepository usuarioRepository;

    @Override
    public void signUp(Usuario usuario) {
        usuario.getRoles().add("ROLE_ADMIN");
        usuarioRepository.save(usuario);
    }

    @Override
    public boolean verificarCredenciales(String username, String password) {
        Usuario usuario = usuarioRepository.findByUsername(username);
        if (usuario == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El usuario 'username' no está registrado");
        } else {
            return usuario.getPassword().equals(password);
        }
    }
    
}
