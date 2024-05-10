package gz.hoteles.servicio;

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
public class ServicioRoles implements IServicioRoles {

    @Autowired
    RolRepository rolesRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    PermisoRepository permisosRepository;

    @Override
    public Rol getRolByName(String name) {
        Rol r = rolesRepository.findRolByNombre(name);
        if (r != null) {
            return r;
        } else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe un rol por ese nombre");
    }

    @Override
    public void crearRol(Rol rol) {
        Set<Permiso> permisos = rol.getPermisos();

        // Guardamos los permisos individualmente
        for (Permiso p : permisos) {
            permisosRepository.save(p);
        }

        rolesRepository.save(rol);
    }

    @Override
    public List<Rol> getAll() {
        List<Rol> list = rolesRepository.findAll();
        if (list.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existen roles");
        } else
            return list;
    }

    @Override
    public void añadirRolAUsuario(Usuario usuario, String rolName) {
        Rol r = getRolByName(rolName);
        usuario.getRoles().add(r);
    }

    /*
     * @Override
     * public void añadirPermiso(int idRol, String rol) {
     * Rol r = rolesRepository.findById(idRol).orElse(null);
     * if (r != null) {
     * r.getRolesIndirectos().add(rol);
     * rolesRepository.save(r);
     * } else throw new ResponseStatusException(HttpStatus.NOT_FOUND,
     * "No existe un rol con ese id");
     * }
     * 
     * @Override
     * public void borrarPermiso(int idRol, String rol) {
     * Rol r = rolesRepository.findById(idRol).orElse(null);
     * if (r != null) {
     * if (r.getRolesIndirectos().contains(rol)) {
     * r.getRolesIndirectos().remove(rol);
     * rolesRepository.save(r);
     * } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
     * "El rol no contiene el rol indirecto " + rol);
     * } else throw new ResponseStatusException(HttpStatus.NOT_FOUND,
     * "No existe un rol con ese id");
     * }
     */

    @Override
    public Rol getById(int id) {
        Rol r = rolesRepository.findById(id).orElse(null);
        if (r == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe un rol con ese ID");
        } else
            return r;
    }

    @Override
    public void añadirRolIndirecto(int idRol, String rol) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'añadirRolIndirecto'");
    }

    @Override
    public void borrarRolIndirecto(int idRol, String rol) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'borrarRolIndirecto'");
    }

}
