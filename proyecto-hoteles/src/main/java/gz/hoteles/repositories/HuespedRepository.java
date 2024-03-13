package gz.hoteles.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import gz.hoteles.entities.Huesped;

public interface HuespedRepository extends JpaRepository<Huesped, Integer> {
    
}

