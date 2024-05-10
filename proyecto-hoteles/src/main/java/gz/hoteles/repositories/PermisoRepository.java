package gz.hoteles.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import gz.hoteles.entities.Permiso;

public interface PermisoRepository extends JpaRepository<Permiso, Integer>{
    
}
