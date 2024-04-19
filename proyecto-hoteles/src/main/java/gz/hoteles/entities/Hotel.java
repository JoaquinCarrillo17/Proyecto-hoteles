package gz.hoteles.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Data
@Entity
public class Hotel {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;
    private String nombre;
    private String direccion;
    private String telefono;
    private String email;
    private String sitioWeb;
    
    //@JsonIgnore
    @ManyToMany
    @JoinTable(name = "hotel_servicio", joinColumns = {@JoinColumn(name = "hotel_id")}, inverseJoinColumns = {@JoinColumn(name = "servicio_id") })
    private List<Servicio> servicios = new ArrayList<Servicio>();
    //@JsonIgnore
    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Habitacion> habitaciones = new ArrayList<Habitacion>();
    @JsonIgnore
    private int numeroHabitaciones = habitaciones.size();
    @JsonIgnore
    private int numeroHabitacionesDisponibles;
    @JsonIgnore
    private int numeroHabitacionesReservadas;

	public void addServicio(Servicio servicio) {
		this.servicios.add(servicio);
	}

    public void addHabitacion(Habitacion habitacion) {
        this.habitaciones.add(habitacion);
        updateHabitaciones(Collections.singletonList(habitacion));
    }

    public void updateHabitaciones(List<Habitacion> habitaciones) {
        for (Habitacion habitacion : habitaciones) {
            this.numeroHabitaciones++;
            if (habitacion.getHuespedes().size() > 0) {
                this.numeroHabitacionesReservadas++;
            } else this.numeroHabitacionesDisponibles++;
        }
    }

    public Hotel() {
        
    }

    public Hotel(int id, String nombre, String direccion, String telefono, String email, String sitioWeb) {
        this.id = id;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.email = email;
        this.sitioWeb = sitioWeb;
    }

    @Override
    public String toString() {
        return "Hotel{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", direccion='" + direccion + '\'' +
                ", telefono='" + telefono + '\'' +
                ", email='" + email + '\'' +
                ", sitioWeb='" + sitioWeb + '\'' +
                '}';
    }
    
}