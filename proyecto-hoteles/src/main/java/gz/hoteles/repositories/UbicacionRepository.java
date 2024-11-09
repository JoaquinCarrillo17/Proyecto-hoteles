package gz.hoteles.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import gz.hoteles.entities.Ubicacion;

public interface UbicacionRepository extends JpaRepository<Ubicacion, Long>, JpaSpecificationExecutor<Ubicacion> {

    Page<Ubicacion> findAll(Specification<Ubicacion> spec, Pageable pageable);

    Ubicacion findByCiudad(String ciudad);
    
}
