package gz.hoteles.entities;

import lombok.Data;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Data
@Entity
public class Huesped {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;
    private String nombreCompleto;
    private String dni;
    private String email;
    private Date fechaCheckIn;
    private Date fechaCheckOut;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="habitacion_fk")
    private Habitacion habitacion;

    public Huesped() {
        
    }

    public Huesped (int id, String nombre, String dni, String email) {
        this.id = id;
        this.nombreCompleto = nombre;
        this.dni = dni;
        this.email = email;
    }

    public Huesped (int id, String nombre, String dni, String email, Date fechaCheckIn, Date fechaCheckOut) {
        this.id = id;
        this.nombreCompleto = nombre;
        this.dni = dni;
        this.email = email;
        this.fechaCheckIn = fechaCheckIn;
        this.fechaCheckOut = fechaCheckOut;
    }
}

