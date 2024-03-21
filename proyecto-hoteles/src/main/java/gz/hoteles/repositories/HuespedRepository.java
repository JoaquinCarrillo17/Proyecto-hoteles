package gz.hoteles.repositories;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import gz.hoteles.entities.Huesped;

public interface HuespedRepository extends JpaRepository<Huesped, Integer> {

        @Query("SELECT h FROM Huesped h WHERE h.nombreCompleto = :nombre OR h.nombreCompleto LIKE %:nombre%")
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

        Page<Huesped> findByFechaCheckInEquals(Date value, PageRequest of);

        Page<Huesped> findByFechaCheckOutEquals(Date value, PageRequest of);

        Page<Huesped> findByNombreCompletoContainingIgnoreCase(String value, PageRequest of);

        Page<Huesped> findByNombreCompletoContainingIgnoreCaseAndFechaCheckInAfterAndFechaCheckOutBefore(String value,
                        Date fechaEntrada,
                        Date fechaSalida, PageRequest of);

        Page<Huesped> findByDniEqualsAndFechaCheckInAfterAndFechaCheckOutBefore(String value, Date fechaEntrada,
                        Date fechaSalida,
                        PageRequest of);

        Page<Huesped> findByEmailEqualsAndFechaCheckInAfterAndFechaCheckOutBefore(String value, Date fechaEntrada,
                        Date fechaSalida, PageRequest of);

        Page<Huesped> findByNombreCompletoContainingIgnoreCaseAndFechaCheckOutBefore(String value, Date fechaSalida,
                        PageRequest of);

        Page<Huesped> findByDniEqualsAndFechaCheckOutBefore(String value, Date fechaSalida, PageRequest of);

        Page<Huesped> findByEmailEqualsAndFechaCheckOutBefore(String value, Date fechaSalida, PageRequest of);

        Page<Huesped> findByNombreCompletoContainingIgnoreCaseAndFechaCheckInAfter(String value, Date fechaEntrada,
                        PageRequest of);

        Page<Huesped> findByDniEqualsAndFechaCheckInAfter(String value, Date fechaEntrada, PageRequest of);

        Page<Huesped> findByEmailEqualsAndFechaCheckInAfter(String value, Date fechaEntrada, PageRequest of);

        /* Función filtrado get con todos los parámetros */

        Page<Huesped> findByNombreCompletoAndEmailAndDni(String nombreCompleto, String email, String dni,
                        PageRequest of);

        Page<Huesped> findByNombreCompletoAndEmail(String nombreCompleto, String email, PageRequest of);

        Page<Huesped> findByNombreCompleto(String nombreCompleto, PageRequest of);

        Page<Huesped> findByEmailAndDni(String email, String dni, PageRequest of);

        Page<Huesped> findByEmail(String email, PageRequest of);

        Page<Huesped> findByDni(String dni, PageRequest of);

        Page<Huesped> findByNombreCompletoAndEmailAndDniAndFechaCheckInAfterAndFechaCheckOutBefore(
                String nombreCompleto, String email, String dni, Date fechaCheckIn, Date fechaCheckOut, PageRequest of);

        Page<Huesped> findByNombreCompletoAndEmailAndFechaCheckInAfterAndFechaCheckOutBefore(String nombreCompleto,
                String email, Date fechaCheckIn, Date fechaCheckOut, PageRequest of);

        Page<Huesped> findByNombreCompletoAndFechaCheckInAfterAndFechaCheckOutBefore(String nombreCompleto,
                Date fechaCheckIn, Date fechaCheckOut, PageRequest of);

        Page<Huesped> findByEmailAndDniAndFechaCheckInAfterAndFechaCheckOutBefore(String email, String dni,
                Date fechaCheckIn, Date fechaCheckOut, PageRequest of);

        Page<Huesped> findByEmailAndFechaCheckInAfterAndFechaCheckOutBefore(String email, Date fechaCheckIn,
                Date fechaCheckOut, PageRequest of);

        Page<Huesped> findByDniAndFechaCheckInAfterAndFechaCheckOutBefore(String dni, Date fechaCheckIn,
                Date fechaCheckOut, PageRequest of);

        Page<Huesped> findByFechaCheckInAfterAndFechaCheckOutBefore(Date fechaCheckIn, Date fechaCheckOut,
                PageRequest of);

        Page<Huesped> findByNombreCompletoAndEmailAndDniAndFechaCheckInAfter(String nombreCompleto, String email,
                String dni, Date fechaCheckIn, PageRequest of);

        Page<Huesped> findByNombreCompletoAndEmailAndFechaCheckInAfter(String nombreCompleto, String email,
                Date fechaCheckIn, PageRequest of);

        Page<Huesped> findByNombreCompletoAndFechaCheckInAfter(String nombreCompleto, Date fechaCheckIn,
                PageRequest of);

        Page<Huesped> findByEmailAndDniAndFechaCheckInAfter(String email, String dni, Date fechaCheckIn,
                PageRequest of);

        Page<Huesped> findByEmailAndFechaCheckInAfter(String email, Date fechaCheckIn, PageRequest of);

        Page<Huesped> findByDniAndFechaCheckInAfter(String dni, Date fechaCheckIn, PageRequest of);

        Page<Huesped> findByFechaCheckInAfter(Date fechaCheckIn, PageRequest of);

        Page<Huesped> findByNombreCompletoAndEmailAndDniAndFechaCheckOutBefore(String nombreCompleto, String email,
                String dni, Date fechaCheckOut, PageRequest of);

        Page<Huesped> findByNombreCompletoAndEmailAndFechaCheckOutBefore(String nombreCompleto, String email,
                Date fechaCheckOut, PageRequest of);

        Page<Huesped> findByNombreCompletoAndFechaCheckOutBefore(String nombreCompleto, Date fechaCheckOut,
                PageRequest of);

        Page<Huesped> findByEmailAndDniAndFechaCheckOutBefore(String email, String dni, Date fechaCheckOut,
                PageRequest of);

        Page<Huesped> findByEmailAndFechaCheckOutBefore(String email, Date fechaCheckOut, PageRequest of);

        Page<Huesped> findByDniAndFechaCheckOutBefore(String dni, Date fechaCheckOut, PageRequest of);

        Page<Huesped> findByFechaCheckOutBefore(Date fechaCheckOut, PageRequest of);

        /* Fin función */

}
