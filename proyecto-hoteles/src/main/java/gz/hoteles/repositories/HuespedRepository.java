package gz.hoteles.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import gz.hoteles.entities.Huesped;

public interface HuespedRepository extends JpaRepository<Huesped, Integer> {

    @Query("SELECT h FROM Huesped h WHERE h.nombreCompleto = :nombre")
	List<Huesped> getHuespedesByNombre(String nombre);

    @Query("SELECT h FROM Huesped h WHERE h.dni = :dni")
	List<Huesped> getHuespedesByDni(String dni);

    @Query("SELECT h FROM Huesped h WHERE h.email = :email")
	List<Huesped> getHuespedesByEmail(String email);

    @Query("SELECT h FROM Huesped h WHERE h.fechaCheckIn = :fecha")
	List<Huesped> getHuespedesByFechaEntrada(Date fecha);

    @Query("SELECT h FROM Huesped h WHERE h.fechaCheckOut = :fecha")
	List<Huesped> getHuespedesByFechaSalida(Date fecha);
    
}

