package gz.hoteles.entities;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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
    private LocalDate fecha;


    
}
