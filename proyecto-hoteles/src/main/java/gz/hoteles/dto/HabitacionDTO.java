package gz.hoteles.dto;

import java.util.List;
import java.util.Set;

import gz.hoteles.entities.Huesped;
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
    private String foto;
    private Set<ServiciosHabitacionEnum> servicios;
    private List<Huesped> huespedes;

}
