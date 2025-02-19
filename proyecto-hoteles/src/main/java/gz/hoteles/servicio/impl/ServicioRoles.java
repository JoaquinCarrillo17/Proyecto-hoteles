package gz.hoteles.servicio.impl;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import gz.hoteles.entities.Permiso;
import gz.hoteles.entities.Rol;
import gz.hoteles.entities.Usuario;
import gz.hoteles.repositories.PermisoRepository;
import gz.hoteles.repositories.RolRepository;
import gz.hoteles.repositories.UsuarioRepository;

@Service
public class ServicioRoles {

    @Autowired
    RolRepository rolesRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    PermisoRepository permisosRepository;

    public Rol getRolByName(String name) {
        Rol r = rolesRepository.findRolByNombre(name);
        if (r != null) {
            return r;
        } else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe un rol por ese nombre");
    }

    public void crearRol(Rol rol) {
        Set<Permiso> permisos = rol.getPermisos();

        // Guardamos los permisos individualmente
        for (Permiso p : permisos) {
            permisosRepository.save(p);
        }

        rolesRepository.save(rol);
    }

    public List<Rol> getAll() {
        List<Rol> list = rolesRepository.findAll();
        if (list.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existen roles");
        } else
            return list;
    }

    public void añadirRolAUsuario(Usuario usuario, String rolName) {
        Rol r = getRolByName(rolName);
        usuario.getRoles().add(r);
    }

    public Rol getById(int id) {
        Rol r = rolesRepository.findById(id).orElse(null);
        if (r == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe un rol con ese ID");
        } else
            return r;
    }

    public void añadirPermisoaRol(String rol, String permiso) {
        Rol r = rolesRepository.findRolByNombre(rol);
        Permiso p = permisosRepository.findByNombre(permiso);
        r.getPermisos().add(p);
        rolesRepository.save(r);
    }

}
