package gz.hoteles.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

    Page<Habitacion> findByPrecioNocheEquals(Float value, PageRequest of);

    Page<Habitacion> findByTipoHabitacionEquals(TipoHabitacion value, PageRequest of);

    /* Funciones GET tocho */

    Page<Habitacion> findByNumeroAndTipoHabitacionAndPrecioNoche(String numero, TipoHabitacion tipoHabitacion,
            Float precioNoche, PageRequest of);

    Page<Habitacion> findByNumeroAndTipoHabitacion(String numero, TipoHabitacion tipoHabitacion, PageRequest of);

    Page<Habitacion> findByNumeroAndPrecioNoche(String numero, Float precioNoche, PageRequest of);

    Page<Habitacion> findByTipoHabitacionAndPrecioNoche(TipoHabitacion tipoHabitacion, Float precioNoche,
            PageRequest of);

    Page<Habitacion> findByNumero(String numero, PageRequest of);

    Page<Habitacion> findByTipoHabitacion(TipoHabitacion tipoHabitacion, PageRequest of);

    Page<Habitacion> findByPrecioNoche(Float precioNoche, PageRequest of);

    /* Fin funciones GET tocho */

    Page<Habitacion> findAll(Specification<Habitacion> spec, Pageable pageable); // Para el dynamic search tocho

}
