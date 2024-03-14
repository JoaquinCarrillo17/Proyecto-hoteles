package gz.hoteles.repositories;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import gz.hoteles.entities.CategoriaServicio;
import gz.hoteles.entities.Servicio;

public interface ServicioRepository extends JpaRepository<Servicio, Integer>{

    @Query("SELECT s FROM Servicio s WHERE s.nombre = :nombre")
	Page<Servicio> getServicioByNombre(String nombre, Pageable pageable);

    @Query("SELECT s FROM Servicio s WHERE s.categoria = :categoria")
    Page<Servicio> getServicioByCategoria(CategoriaServicio categoria, Pageable pageable);

    @Query("SELECT s FROM Servicio s WHERE s.descripcion = :descripcion")
    Page<Servicio> getServicioByDescripcion(String descripcion, Pageable pageable);
    
}

