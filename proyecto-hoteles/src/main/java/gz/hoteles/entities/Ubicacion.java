package gz.hoteles.entities;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import lombok.Data;


@Entity
@Data
public class Ubicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String ciudad;
    private String pais;
    private String continente;

    public Ubicacion(String ciudad, String pais, String continente) {
        this.ciudad = ciudad;
        this.pais = pais;
        this.continente = continente;
    }

    public Ubicacion() {

    }
}

