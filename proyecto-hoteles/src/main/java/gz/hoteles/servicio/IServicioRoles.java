package gz.hoteles.servicio;

import java.util.List;

import gz.hoteles.entities.Rol;

public interface IServicioRoles {
    
    Rol getRolByName(String name);

    void crearRol(Rol rol);

    List<Rol> getAll();

    void añadirRolAUsuario(int idUsuario, String rolName);

    void añadirRolIndirecto(int idRol, String rol);

    void borrarRolIndirecto(int idRol, String rol);

}
