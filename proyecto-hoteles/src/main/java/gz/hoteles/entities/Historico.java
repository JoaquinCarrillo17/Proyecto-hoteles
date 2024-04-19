package gz.hoteles.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Historico {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)

    private int id;
    private int hotelesTotales;
    private int habitacionesTotales;
    private int habitacionesDisponibles;
    private int habitacionesReservadas;
    private int huespedesTotales;
    private int serviciosTotales;


    
}
