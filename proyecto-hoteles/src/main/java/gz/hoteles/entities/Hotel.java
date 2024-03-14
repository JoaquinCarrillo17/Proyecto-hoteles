package gz.hoteles.entities;

import java.util.ArrayList;
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
    @JoinTable(name = "hotel_servicio", joinColumns = {@JoinColumn(name = "hotel_fk")}, inverseJoinColumns = {@JoinColumn(name = "servicio_fk") })
    private List<Servicio> servicios = new ArrayList<Servicio>();
    //@JsonIgnore
    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Habitacion> habitaciones = new ArrayList<Habitacion>();

	public void addServicio(Servicio servicio) {
		this.servicios.add(servicio);
	}

    public void addHabitacion(Habitacion habitacion) {
        this.habitaciones.add(habitacion);
    }
    
}