package gz.hoteles.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import gz.hoteles.entities.Ubicacion;

public interface UbicacionRepository extends JpaRepository<Ubicacion, Long>, JpaSpecificationExecutor<Ubicacion> {

    Page<Ubicacion> findAll(Specification<Ubicacion> spec, Pageable pageable);

    Ubicacion findByCiudad(String ciudad);

    @Query("SELECT u FROM Ubicacion u WHERE SIZE(u.hoteles) > 0")
    List<Ubicacion> findUbicacionesConHoteles();
    
}
