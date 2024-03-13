package gz.hoteles.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import gz.hoteles.entities.Habitacion;
import gz.hoteles.entities.TipoHabitacion;

public interface HabitacionRepository extends JpaRepository<Habitacion, Integer> {
    
    @Query("SELECT h FROM Habitacion h WHERE h.numero = :numero")
    List<Habitacion> getHabitacionesByNumero(String numero);

    @Query("SELECT h FROM Habitacion h WHERE h.tipoHabitacion = :tipo")
    List<Habitacion> getHabitacionesByTipoHabitacion(TipoHabitacion tipo);

    @Query("SELECT h FROM Habitacion h WHERE h.precioNoche = :precio")
    List<Habitacion> getHabitacionesByPrecioPorNoche(String precio);

}
