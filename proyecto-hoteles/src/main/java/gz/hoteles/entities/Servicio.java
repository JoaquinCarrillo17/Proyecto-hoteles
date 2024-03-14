package gz.hoteles.entities;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;


@Data
@Entity
public class Servicio {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;
    private String nombre;
    private String descripcion;
    @Enumerated(EnumType.STRING)
    private CategoriaServicio categoria;
    @JsonIgnore
    @ManyToMany(mappedBy = "servicios")
    private List<Hotel> hoteles = new ArrayList<Hotel>();
    
    public void addHotel(Hotel hotel) {
        this.hoteles.add(hotel);
    }
}
