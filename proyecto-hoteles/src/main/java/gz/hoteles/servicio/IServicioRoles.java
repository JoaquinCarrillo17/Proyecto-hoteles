package gz.hoteles.servicio;

import java.util.List;

import gz.hoteles.entities.Rol;
import gz.hoteles.entities.Usuario;

public interface IServicioRoles {
    
    Rol getRolByName(String name);

    void crearRol(Rol rol);

    List<Rol> getAll();

    void añadirRolAUsuario(Usuario usuario, String rolName);

    void añadirRolIndirecto(int idRol, String rol);

    void borrarRolIndirecto(int idRol, String rol);

    Rol getById(int id);

}
