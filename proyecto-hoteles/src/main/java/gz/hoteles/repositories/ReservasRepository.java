package gz.hoteles.repositories;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import gz.hoteles.entities.Reservas;

public interface ReservasRepository extends JpaRepository<Reservas, Long>, JpaSpecificationExecutor<Reservas> {

    int countByHotelIdAndCheckInBeforeAndCheckOutAfter(Long id, Date now, Date now2);
    
}
