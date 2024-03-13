package gz.hoteles.entities;

import lombok.Data;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Data
@Entity
public class Habitacion {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;
    private int idHotel;
    private String numero;
    private TipoHabitacion tipoHabitacion;
    private float precioNoche;
    
}
