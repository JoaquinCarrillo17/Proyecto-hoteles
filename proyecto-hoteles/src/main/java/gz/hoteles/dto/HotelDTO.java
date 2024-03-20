package gz.hoteles.dto;

import java.util.ArrayList;
import java.util.List;

import gz.hoteles.entities.Habitacion;
import gz.hoteles.entities.Servicio;
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
    private List<Servicio> servicios = new ArrayList<Servicio>();
    private List<Habitacion> habitaciones = new ArrayList<Habitacion>();
}
