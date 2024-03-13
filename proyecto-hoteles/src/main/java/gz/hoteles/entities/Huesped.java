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
}

