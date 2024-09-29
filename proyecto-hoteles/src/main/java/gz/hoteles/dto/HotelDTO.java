package gz.hoteles.dto;

import java.util.List;

import gz.hoteles.entities.ServiciosEnum;
import lombok.Data;

@Data
public class HotelDTO {
    private int id;
    private String nombre;
    private String direccion;
    private String telefono;
    private String email;
    private String sitioWeb;
    private List<ServiciosEnum> servicios;
    private int numeroHabitaciones;
    private int numeroHabitacionesDisponibles;
    private int numeroHabitacionesReservadas;

}
