package gz.hoteles.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;



import lombok.Data;

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

    public Servicio() {
        
    }

    public Servicio(int id, String nombre, String descripcion, CategoriaServicio categoria) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoria = categoria;
    }
}
