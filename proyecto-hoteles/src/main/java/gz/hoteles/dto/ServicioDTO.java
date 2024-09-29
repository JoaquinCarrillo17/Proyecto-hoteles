package gz.hoteles.dto;

import gz.hoteles.entities.ServiciosEnum;
import lombok.Data;

@Data
public class ServicioDTO {
    
    private int id;
    private String nombre;
    private String descripcion;
    private ServiciosEnum categoria;

}
