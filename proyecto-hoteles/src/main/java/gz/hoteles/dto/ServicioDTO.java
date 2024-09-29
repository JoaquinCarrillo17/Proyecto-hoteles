package gz.hoteles.dto;

import gz.hoteles.entities.ServiciosHotelEnum;
import lombok.Data;

@Data
public class ServicioDTO {
    
    private int id;
    private String nombre;
    private String descripcion;
    private ServiciosHotelEnum categoria;

}
