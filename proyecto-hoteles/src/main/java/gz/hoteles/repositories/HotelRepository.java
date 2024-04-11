package gz.hoteles.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import gz.hoteles.entities.Hotel;
import gz.hoteles.entities.CategoriaServicio;

public interface HotelRepository extends JpaRepository<Hotel, Integer>{

    @Query("SELECT h FROM Hotel h WHERE h.nombre = :nombre OR h.nombre LIKE %:nombre%")
    Page<Hotel> getHotelByNombre(String nombre, Pageable pageable);

    @Query("SELECT h FROM Hotel h WHERE h.direccion = :direccion OR h.direccion LIKE %:direccion%")
    Page<Hotel> getHotelByDireccion(String direccion, Pageable pageable);

    @Query("SELECT h FROM Hotel h WHERE h.telefono = :telefono")
    Page<Hotel> getHotelByTelefono(String telefono, Pageable pageable);

    @Query("SELECT h FROM Hotel h WHERE h.email = :email")
    Page<Hotel> getHotelByEmail(String email, Pageable pageable);

    @Query("SELECT h FROM Hotel h WHERE h.sitioWeb = :sitioWeb")
    Page<Hotel> getHotelBySitioWeb(String sitioWeb, Pageable pageable);

    Page<Hotel> findBySitioWebEquals(String value, PageRequest of);
    Page<Hotel> findByEmailEquals(String value, PageRequest of);
    Page<Hotel> findByTelefonoEquals(String value, PageRequest of);
    Page<Hotel> findByNombreEquals(String value, PageRequest of);
    Page<Hotel> findByDireccionEquals(String value, PageRequest of);

    Page<Hotel> findByNombreContainingIgnoreCase(String value, PageRequest of);
    Page<Hotel> findByDireccionContainingIgnoreCase(String value, PageRequest of);

    /* Funciones filtrado GET tocho */

    Page<Hotel> findByNombreAndDireccionAndTelefonoAndEmailAndSitioWeb(String nombre, String direccion, String telefono,
            String email, String sitioWeb, PageRequest of);

    Page<Hotel> findByNombreAndDireccionAndTelefonoAndEmail(String nombre, String direccion, String telefono,
            String email, PageRequest of);

    Page<Hotel> findByNombreAndDireccionAndTelefono(String nombre, String direccion, String telefono, PageRequest of);

    Page<Hotel> findByNombreAndDireccionAndEmail(String nombre, String direccion, String email, PageRequest of);

    Page<Hotel> findByNombreAndDireccionAndSitioWeb(String nombre, String direccion, String sitioWeb, PageRequest of);

    Page<Hotel> findByNombreAndTelefonoAndEmail(String nombre, String telefono, String email, PageRequest of);

    Page<Hotel> findByNombreAndTelefonoAndSitioWeb(String nombre, String telefono, String sitioWeb, PageRequest of);

    Page<Hotel> findByNombreAndEmailAndSitioWeb(String nombre, String email, String sitioWeb, PageRequest of);

    Page<Hotel> findByDireccionAndTelefonoAndEmailAndSitioWeb(String direccion, String telefono, String email,
            String sitioWeb, PageRequest of);

    Page<Hotel> findByDireccionAndTelefonoAndEmail(String direccion, String telefono, String email, PageRequest of);

    Page<Hotel> findByDireccionAndTelefonoAndSitioWeb(String direccion, String telefono, String sitioWeb,
            PageRequest of);

    Page<Hotel> findByDireccionAndEmailAndSitioWeb(String direccion, String email, String sitioWeb, PageRequest of);

    Page<Hotel> findByTelefonoAndEmailAndSitioWeb(String telefono, String email, String sitioWeb, PageRequest of);

	Page<Hotel> findByNombreAndDireccion(String nombre, String direccion, PageRequest of);

    Page<Hotel> findByNombreAndTelefono(String nombre, String telefono, PageRequest of);

    Page<Hotel> findByNombreAndEmail(String nombre, String email, PageRequest of);

    Page<Hotel> findByNombreAndSitioWeb(String nombre, String sitioWeb, PageRequest of);

    Page<Hotel> findByDireccionAndTelefono(String direccion, String telefono, PageRequest of);

    Page<Hotel> findByDireccionAndEmail(String direccion, String email, PageRequest of);

    Page<Hotel> findByDireccionAndSitioWeb(String direccion, String sitioWeb, PageRequest of);

    Page<Hotel> findByTelefonoAndEmail(String telefono, String email, PageRequest of);

	Page<Hotel> findByTelefonoAndSitioWeb(String telefono, String sitioWeb, PageRequest of);

    Page<Hotel> findByEmailAndSitioWeb(String email, String sitioWeb, PageRequest of);

    Page<Hotel> findByNombre(String nombre, PageRequest of);

    Page<Hotel> findByDireccion(String direccion, PageRequest of);

    Page<Hotel> findByTelefono(String telefono, PageRequest of);

    Page<Hotel> findByEmail(String email, PageRequest of);

    Page<Hotel> findBySitioWeb(String sitioWeb, PageRequest of);

    /* Fin */
    
    Page<Hotel> findAll(Specification<Hotel> spec, Pageable pageable); // Para el dynamic search tocho

    @Query("SELECT DISTINCT h FROM Hotel h JOIN h.servicios s WHERE s.categoria = :categoria")
    Page<Hotel> findByCategoriaServicio(CategoriaServicio categoria, Pageable pageable); // Filtrar hoteles por tipo de servicio

    Page<Hotel> findByNombreContainingIgnoreCaseOrDireccionContainingIgnoreCaseOrTelefonoContainingIgnoreCaseOrEmailContainingIgnoreCaseOrSitioWebContainingIgnoreCase(
            String nombre, String direccion, String telefono, String email, String sitioWeb, Pageable pageable); // Para el magic filter

}