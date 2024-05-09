package gz.hoteles.repositories;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import gz.hoteles.entities.Hotel;
import gz.hoteles.entities.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Usuario findByUsername(String username);

    @Query("SELECT u FROM Usuario u WHERE " +
            "LOWER(u.nombre) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "DATE(u.fechaNacimiento) = DATE(:fecha)")
    Page<Usuario> findByQueryAndDate(String query, Date fecha, Pageable pageable);

    Page<Usuario> findByNombreContainingIgnoreCaseOrUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String query, String query2, String query3, Pageable pageable);

}
