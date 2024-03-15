package gz.hoteles.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import gz.hoteles.entities.Habitacion;
import gz.hoteles.entities.TipoHabitacion;

public interface HabitacionRepository extends JpaRepository<Habitacion, Integer> {
    
    @Query("SELECT h FROM Habitacion h WHERE h.numero = :numero")
    Page<Habitacion> getHabitacionesByNumero(String numero, Pageable pageable);

    @Query("SELECT h FROM Habitacion h WHERE h.tipoHabitacion = :tipo")
    Page<Habitacion> getHabitacionesByTipoHabitacion(TipoHabitacion tipo, Pageable pageable);

    @Query("SELECT h FROM Habitacion h WHERE h.precioNoche = :precio")
    Page<Habitacion> getHabitacionesByPrecioPorNoche(String precio, Pageable pageable);

    Page<Habitacion> findByNumeroEquals(String value, PageRequest of);

    Page<Habitacion> findByPrecioNocheEquals(String value, PageRequest of);

    Page<Habitacion> findByTipoHabitacionEquals(String value, PageRequest of);

}
