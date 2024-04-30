package gz.hoteles.entities;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "hotel_fk")
    private Hotel hotel;
    //@JsonIgnore
    @OneToMany(mappedBy = "habitacion", cascade = CascadeType.ALL, orphanRemoval = true)
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
