package gz.hoteles.dto;

import java.util.ArrayList;
import java.util.List;

import gz.hoteles.entities.Huesped;
import gz.hoteles.entities.TipoHabitacion;
import lombok.Data;

@Data
public class HabitacionDTO {
    
    private int id;
    private String numero;
    private TipoHabitacion tipoHabitacion;
    private float precioNoche;
    private HotelDTO hotel;  
    private List<Huesped> huespedes = new ArrayList<Huesped>();

}
