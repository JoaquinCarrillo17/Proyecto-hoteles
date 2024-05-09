package gz.hoteles.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import gz.hoteles.entities.Rol;

public interface RolRepository extends JpaRepository<Rol, Integer> {

    Rol findRolByNombre(String name);

    Page<Rol> findByNombreContainingIgnoreCaseOrDescripcionContainingIgnoreCase(String query, String query2,
            Pageable pageable);

    
    
}
