package gz.hoteles.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

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

import gz.hoteles.dto.DtoGeneral;
import gz.hoteles.dto.HabitacionDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Habitacion implements EntityGeneral {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private String numero;
    @Enumerated(EnumType.STRING)
    private TipoHabitacion tipoHabitacion;
    private float precioNoche;
    private String foto;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<ServiciosHabitacionEnum> servicios = new HashSet<>();
    
    @ManyToOne
    @JoinColumn(name = "hotel_fk")
    private Hotel hotel;

    /*@OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Habitacion> habitaciones = new HashSet<>();*/

    /*@OneToMany(mappedBy = "habitacion", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Reservas> reservas = new HashSet<>();*/

    @Override
    public DtoGeneral getDto() {
        HabitacionDTO dto = new HabitacionDTO();

        dto.setId(this.getId());
        dto.setNumero(this.getNumero());
        dto.setTipoHabitacion(this.getTipoHabitacion());
        dto.setPrecioNoche(this.getPrecioNoche());
        dto.setFoto(this.getFoto());
        dto.setServicios(this.getServicios());
        return dto;
    }

}
