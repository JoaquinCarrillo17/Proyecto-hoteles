package gz.hoteles.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import gz.hoteles.entities.Servicio;

public interface ServicioRepository extends JpaRepository<Servicio, Integer>{
    
}

