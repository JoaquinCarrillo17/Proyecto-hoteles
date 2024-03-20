package gz.hoteles.entities;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

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
}
