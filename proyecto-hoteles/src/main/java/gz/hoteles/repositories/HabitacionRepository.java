package gz.hoteles.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import gz.hoteles.entities.Habitacion;

public interface HabitacionRepository extends JpaRepository<Habitacion, Integer> {
    
}
