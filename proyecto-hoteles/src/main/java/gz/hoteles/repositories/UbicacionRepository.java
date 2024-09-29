package gz.hoteles.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import gz.hoteles.entities.Ubicacion;

public interface UbicacionRepository extends JpaRepository<Ubicacion, Integer> {
    
}
