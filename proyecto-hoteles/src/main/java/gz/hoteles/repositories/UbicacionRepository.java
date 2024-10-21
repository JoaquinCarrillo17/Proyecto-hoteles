package gz.hoteles.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import gz.hoteles.entities.Ubicacion;

public interface UbicacionRepository extends JpaRepository<Ubicacion, Integer> {

    Page<Ubicacion> findAll(Specification<Ubicacion> spec, Pageable pageable);
    
}
