package gz.hoteles.dto;

import lombok.Data;

@Data
public class HotelDTO {
    private int id;
    private String nombre;
    private String direccion;
    private String telefono;
    private String email;
    private String sitioWeb;
    private int numeroHabitaciones;
    private int numeroHabitacionesDisponibles;
    private int numeroHabitacionesReservadas;

}
