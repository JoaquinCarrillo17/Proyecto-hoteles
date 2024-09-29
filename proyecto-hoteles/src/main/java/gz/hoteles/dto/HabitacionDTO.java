package gz.hoteles.dto;

import gz.hoteles.entities.TipoHabitacion;
import lombok.Data;

@Data
public class HabitacionDTO {
    
    private int id;
    private String numero;
    private TipoHabitacion tipoHabitacion;
    private float precioNoche;
    private String nombreHotel;

}
