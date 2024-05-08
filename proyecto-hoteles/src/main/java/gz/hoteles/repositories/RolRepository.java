package gz.hoteles.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import gz.hoteles.entities.Rol;

public interface RolRepository extends JpaRepository<Rol, Integer> {

    Rol findRolByNombre(String name);

    
    
}
