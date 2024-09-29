package gz.hoteles.dto;

import java.util.Set;

import gz.hoteles.entities.ServiciosHabitacionEnum;
import gz.hoteles.entities.TipoHabitacion;
import lombok.Data;

@Data
public class HabitacionDTO {
    
    private int id;
    private String numero;
    private TipoHabitacion tipoHabitacion;
    private float precioNoche;
    private String nombreHotel;
    private String idUsuario;
    private Set<ServiciosHabitacionEnum> servicios;

}
