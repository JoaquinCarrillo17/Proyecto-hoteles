package gz.hoteles.servicio;

import gz.hoteles.entities.Usuario;

public interface IServicioUsuarios {
    
    void signUp(Usuario usuario);

    boolean verificarCredenciales(String username, String password);

    Usuario getUsuarioByUsername(String username);

    void delete(int id);

}
