package gz.hoteles.repositories;

import java.util.Date;
import java.util.List;

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

    @Query("SELECT h.id FROM Hotel h WHERE h.idUsuario = :idUsuario")
    Long findHotelIdByUsuario(int idUsuario);

    @Query("SELECT MONTH(r.checkIn), COUNT(r.id), SUM(r.coste) " +
            "FROM Reservas r " +
            "WHERE r.hotel.id = :hotelId AND YEAR(r.checkIn) = :year " +
            "GROUP BY MONTH(r.checkIn)")
    List<Object[]> findReservasEstadisticasByUsuarioAndYear(Long hotelId, int year);

    @Query("SELECT MONTH(r.checkIn), COUNT(r.id), SUM(r.coste) " +
            "FROM Reservas r " +
            "WHERE YEAR(r.checkIn) = :year " +
            "GROUP BY MONTH(r.checkIn)")
    List<Object[]> findReservasEstadisticasByYear(@Param("year") int year);

    @Query("SELECT COUNT(h) FROM Habitacion h WHERE h.hotel.id = :hotelId")
    int countTotalHabitacionesByHotel(Long hotelId);

    @Query("SELECT COUNT(r.habitacion.id) FROM Reservas r WHERE r.habitacion.hotel.id = :hotelId AND r.checkOut > CURRENT_DATE")
    int countHabitacionesReservadasByHotel(Long hotelId);

    @Query("SELECT COUNT(h) FROM Habitacion h")
    int countTotalHabitaciones();

    @Query("SELECT COUNT(r.habitacion.id) FROM Reservas r WHERE r.checkOut > CURRENT_DATE")
    int countHabitacionesReservadas();

}
