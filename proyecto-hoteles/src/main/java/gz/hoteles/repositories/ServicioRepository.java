package gz.hoteles.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import gz.hoteles.entities.CategoriaServicio;
import gz.hoteles.entities.Servicio;

public interface ServicioRepository extends JpaRepository<Servicio, Integer>{

    @Query("SELECT s FROM Servicio s WHERE s.nombre = :nombre")
	List<Servicio> getServicioByNombre(String nombre);

    @Query("SELECT s FROM Servicio s WHERE s.categoria = :categoria")
    List<Servicio> getServicioByCategoria(CategoriaServicio categoria);

    @Query("SELECT s FROM Servicio s WHERE s.descripcion = :descripcion")
    List<Servicio> getServicioByDescripcion(String descripcion);
    
}

