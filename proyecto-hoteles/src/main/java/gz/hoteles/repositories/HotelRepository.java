package gz.hoteles.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import gz.hoteles.entities.Hotel;

public interface HotelRepository extends JpaRepository<Hotel, Integer>{
    
}