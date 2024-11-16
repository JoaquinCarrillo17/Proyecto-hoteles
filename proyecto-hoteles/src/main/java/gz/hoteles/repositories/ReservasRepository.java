package gz.hoteles.repositories;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import gz.hoteles.entities.Habitacion;
import gz.hoteles.entities.Reservas;

public interface ReservasRepository extends JpaRepository<Reservas, Long>, JpaSpecificationExecutor<Reservas> {

    int countByHotelIdAndCheckInBeforeAndCheckOutAfter(Long id, Date now, Date now2);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Reservas r " +
        "WHERE r.habitacion = :habitacion AND " +
        "(r.checkIn < :checkOutDate AND r.checkOut > :checkInDate)")
    boolean existsByHabitacionAndFechasSolapadas(@Param("habitacion") Habitacion habitacion,
                                                @Param("checkInDate") Date checkInDate,
                                                @Param("checkOutDate") Date checkOutDate);

    
}
