package gz.hoteles.servicio;

import java.util.List;

import gz.hoteles.entities.Rol;
import gz.hoteles.entities.Usuario;

public interface IServicioRoles {
    
    Rol getRolByName(String name);

    void crearRol(Rol rol);

    void añadirPermisoaRol(String rol, String permiso);

    List<Rol> getAll();

    void añadirRolAUsuario(Usuario usuario, String rolName);

    Rol getById(int id);

}
