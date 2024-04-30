package gz.hoteles.entities;

import lombok.Data;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

