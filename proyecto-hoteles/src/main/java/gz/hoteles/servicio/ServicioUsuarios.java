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

    @Autowired
    IServicioRoles servicioRoles;

    @Override
    public void signUp(Usuario usuario) {
        añadirRol(usuario);
        usuarioRepository.save(usuario); 
    }

    private void añadirRol(Usuario usuario) {
        servicioRoles.añadirRolAUsuario(usuario, "USUARIO");
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

	@Override
	public Usuario getUsuarioByUsername(String username) {
        Usuario u = usuarioRepository.findByUsername(username);
        if (u != null) {
            return u;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El usuario no existe");
        }
	}

    @Override
	public Usuario getUsuarioById(int id) {
        Usuario u = usuarioRepository.findById(id).orElse(null);
        if (u != null) {
            return u;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El usuario no existe");
        }
	}

    @Override
    public void delete(int id) {
        Usuario u = usuarioRepository.findById(id).orElse(null);
        if (u != null) {
            usuarioRepository.delete(u);
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El usuario no existe");
    }
    
}
