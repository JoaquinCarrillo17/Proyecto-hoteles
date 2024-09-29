package gz.hoteles.servicio;

import gz.hoteles.entities.Usuario;

public interface IServicioUsuarios {
    
    Usuario signUp(Usuario usuario);

    boolean verificarCredenciales(String username, String password);

    Usuario getUsuarioByUsername(String username);

    Usuario getUsuarioById(int id);

    void delete(int id);

    void crearUsuario(Usuario usuario);

}
