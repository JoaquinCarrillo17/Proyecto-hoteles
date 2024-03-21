package gz.hoteles.dto;

import gz.hoteles.entities.CategoriaServicio;
import lombok.Data;

@Data
public class ServicioDTO {
    
    private int id;
    private String nombre;
    private String descripcion;
    private CategoriaServicio categoria;

}
