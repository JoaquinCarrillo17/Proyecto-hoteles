package gz.hoteles.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
@Entity
public class Usuario {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;
    private String nombre;
    private String username;
    private String password;
    private String email;
    //@JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate fechaNacimiento;
    @JsonIgnore
    @ElementCollection
    private List<String> roles = new ArrayList<String>();

}
