package gz.hoteles.repositories;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import gz.hoteles.entities.Huesped;

public interface HuespedRepository extends JpaRepository<Huesped, Integer> {

    @Query("SELECT h FROM Huesped h WHERE h.nombreCompleto = :nombre")
	Page<Huesped> getHuespedesByNombre(String nombre, Pageable pageable);

    @Query("SELECT h FROM Huesped h WHERE h.dni = :dni")
	Page<Huesped> getHuespedesByDni(String dni, Pageable pageable);

    @Query("SELECT h FROM Huesped h WHERE h.email = :email")
	Page<Huesped> getHuespedesByEmail(String email, Pageable pageable);

    @Query("SELECT h FROM Huesped h WHERE h.fechaCheckIn = :fecha")
	Page<Huesped> getHuespedesByFechaEntrada(Date fecha, Pageable pageable);

    @Query("SELECT h FROM Huesped h WHERE h.fechaCheckOut = :fecha")
	Page<Huesped> getHuespedesByFechaSalida(Date fecha, Pageable pageable);

    Page<Huesped> findByNombreCompletoEquals(String value, PageRequest of);

    Page<Huesped> findByEmailEquals(String value, PageRequest of);

    Page<Huesped> findByDniEquals(String value, PageRequest of);

    Page<Huesped> findByFechaCheckInEquals(String value, PageRequest of);

    Page<Huesped> findByFechaCheckOutEquals(String value, PageRequest of);
    
}

