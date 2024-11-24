package gz.hoteles.servicio.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import gz.hoteles.entities.Usuario;
import gz.hoteles.repositories.UsuarioRepository;

@Service
public class ServicioUsuarios  {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    ServicioRoles servicioRoles;

    @Autowired
    ServicioEmails servicioEmails;

    public Usuario signUp(Usuario usuario) {
        añadirRol(usuario);

        String destinatario = usuario.getEmail();
        String asunto = "Registro exitoso";
        String contenido = "¡Hola " + usuario.getNombre() + ", tu registro en JC Hotel Group ha sido exitoso!";
        //servicioEmails.enviarCorreo(destinatario, asunto, contenido);

        return usuarioRepository.save(usuario);
    }

    private void añadirRol(Usuario usuario) {
        if (usuario.getUsername().contains("admin")) {
            servicioRoles.añadirRolAUsuario(usuario, "ADMIN");
        } else servicioRoles.añadirRolAUsuario(usuario, "USUARIO");
    }

    public boolean verificarCredenciales(String username, String password) {
        Usuario usuario = usuarioRepository.findByUsername(username);
        if (usuario == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El usuario 'username' no está registrado");
        } else {
            return usuario.getPassword().equals(password);
        }
    }

    public Usuario getUsuarioByUsername(String username) {
        Usuario u = usuarioRepository.findByUsername(username);
        if (u != null) {
            return u;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El usuario no existe");
        }
    }

    public Usuario getUsuarioById(int id) {
        Usuario u = usuarioRepository.findById(id).orElse(null);
        if (u != null) {
            return u;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El usuario no existe");
        }
    }

    public void delete(int id) {
        Usuario u = usuarioRepository.findById(id).orElse(null);
        if (u != null) {
            usuarioRepository.delete(u);
        } else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El usuario no existe");
    }

    public void crearUsuario(Usuario usuario) {
        usuarioRepository.save(usuario);
    }

}
