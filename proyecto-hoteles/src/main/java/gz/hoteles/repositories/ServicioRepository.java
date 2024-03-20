package gz.hoteles.repositories;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import gz.hoteles.entities.CategoriaServicio;
import gz.hoteles.entities.Servicio;

public interface ServicioRepository extends JpaRepository<Servicio, Integer>{

    @Query("SELECT s FROM Servicio s WHERE s.nombre = :nombre OR s.nombre LIKE %:nombre%")
	Page<Servicio> getServicioByNombre(String nombre, Pageable pageable);

    @Query("SELECT s FROM Servicio s WHERE s.categoria = :categoria")
    Page<Servicio> getServicioByCategoria(CategoriaServicio categoria, Pageable pageable);

    @Query("SELECT s FROM Servicio s WHERE s.descripcion = :descripcion")
    Page<Servicio> getServicioByDescripcion(String descripcion, Pageable pageable);

	Page<Servicio> findByNombreEquals(String value, PageRequest of);

    Page<Servicio> findByDescripcionEquals(String value, PageRequest of);

    Page<Servicio> findByCategoriaEquals(CategoriaServicio value, PageRequest of);

    Page<Servicio> findByNombreContainingIgnoreCase(String value, PageRequest of);
    
}

