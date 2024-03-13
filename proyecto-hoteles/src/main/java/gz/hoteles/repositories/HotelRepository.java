package gz.hoteles.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import gz.hoteles.entities.Hotel;

public interface HotelRepository extends JpaRepository<Hotel, Integer>{

    @Query("SELECT h FROM Hotel h WHERE h.nombre = :nombre")
    List<Hotel> getHotelByNombre(String nombre);

    @Query("SELECT h FROM Hotel h WHERE h.direccion = :direccion")
    List<Hotel> getHotelByDireccion(String direccion);

    @Query("SELECT h FROM Hotel h WHERE h.telefono = :telefono")
    List<Hotel> getHotelByTelefono(String telefono);

    @Query("SELECT h FROM Hotel h WHERE h.email = :email")
    List<Hotel> getHotelByEmail(String email);

    @Query("SELECT h FROM Hotel h WHERE h.sitioWeb = :sitioWeb")
    List<Hotel> getHotelBySitioWeb(String sitioWeb);
    
}