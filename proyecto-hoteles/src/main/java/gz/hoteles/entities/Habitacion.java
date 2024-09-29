package gz.hoteles.entities;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Data
@Entity
public class Habitacion {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;
    private String numero;
    @Enumerated(EnumType.STRING)
    private TipoHabitacion tipoHabitacion;
    private float precioNoche;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<ServiciosHabitacionEnum> servicios = new HashSet<>();
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "hotel_fk")
    private Hotel hotel;
    //@JsonIgnore
    @OneToMany(mappedBy = "habitacion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Huesped> huespedes = new ArrayList<Huesped>();

    public void addHuesped(Huesped huesped) {
        this.huespedes.add(huesped);
    }

    public Habitacion() {

    }

    public Habitacion(int id, String numero, TipoHabitacion tipoHabitacion, float precio){
        this.id = id;
        this.numero = numero;
        this.tipoHabitacion = tipoHabitacion;
        this.precioNoche = precio;
    }

    @Override
    public String toString() {
        return "Habitacion{" +
                "id=" + id +
                ", numero='" + numero + '\'' +
                ", tipoHabitacion=" + tipoHabitacion +
                ", precioNoche=" + precioNoche +
                '}';
    }
}
