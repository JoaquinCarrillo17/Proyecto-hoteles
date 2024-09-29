package gz.hoteles.dto;

import java.util.Set;

import gz.hoteles.entities.ServiciosHotelEnum;
import lombok.Data;

@Data
public class HotelDTO {
    private int id;
    private String nombre;
    private String direccion;
    private String telefono;
    private String email;
    private String sitioWeb;
    private String idUsuario;
    private Set<ServiciosHotelEnum> servicios;
    private int numeroHabitaciones;
    private int numeroHabitacionesDisponibles;
    private int numeroHabitacionesReservadas;

}
