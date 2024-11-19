package gz.hoteles.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import gz.hoteles.entities.Huesped;

public interface HuespedRepository extends JpaRepository<Huesped, Long>, JpaSpecificationExecutor<Huesped> {

        @Query("SELECT h FROM Huesped h WHERE h.nombreCompleto = :nombre OR h.nombreCompleto LIKE %:nombre%")
        Page<Huesped> getHuespedesByNombre(String nombre, Pageable pageable);

        @Query("SELECT h FROM Huesped h WHERE h.dni = :dni")
        Page<Huesped> getHuespedesByDni(String dni, Pageable pageable);

        @Query("SELECT h FROM Huesped h WHERE h.email = :email")
        Page<Huesped> getHuespedesByEmail(String email, Pageable pageable);

        Page<Huesped> findByNombreCompletoEquals(String value, PageRequest of);

        Page<Huesped> findByEmailEquals(String value, PageRequest of);

        Page<Huesped> findByDniEquals(String value, PageRequest of);

        Page<Huesped> findByNombreCompletoContainingIgnoreCase(String value, PageRequest of);

        /* Función filtrado get con todos los parámetros */

        Page<Huesped> findByNombreCompletoAndEmailAndDni(String nombreCompleto, String email, String dni,
                        PageRequest of);

        Page<Huesped> findByNombreCompletoAndEmail(String nombreCompleto, String email, PageRequest of);

        Page<Huesped> findByNombreCompleto(String nombreCompleto, PageRequest of);

        Page<Huesped> findByEmailAndDni(String email, String dni, PageRequest of);

        Page<Huesped> findByEmail(String email, PageRequest of);

        Page<Huesped> findByDni(String dni, PageRequest of);

        /* Fin función */

        Page<Huesped> findAll(Specification<Huesped> spec, Pageable pageable); // Para el dynamic search tocho

        /* Para el magic filter */

        Page<Huesped> findByNombreCompletoContainingIgnoreCaseOrDniContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        String nombre, String dni, String email, Pageable pageable);

        Huesped findByDni(String dni);

}
