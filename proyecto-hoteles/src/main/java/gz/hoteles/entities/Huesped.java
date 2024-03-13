package gz.hoteles.entities;

import lombok.Data;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Data
@Entity
public class Huesped {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;
    private String nombre;
    private String apellido;
    private String dni;
    private String email;
    private Date fechaCheckIn;
    private Date fechaCheckOut;
}

