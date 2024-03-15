package gz.hoteles.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import gz.hoteles.entities.Hotel;

public interface HotelRepository extends JpaRepository<Hotel, Integer>{

    @Query("SELECT h FROM Hotel h WHERE h.nombre = :nombre")
    Page<Hotel> getHotelByNombre(String nombre, Pageable pageable);

    @Query("SELECT h FROM Hotel h WHERE h.direccion = :direccion")
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
    
}