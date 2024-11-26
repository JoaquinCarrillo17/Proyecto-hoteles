package gz.hoteles.entities;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import gz.hoteles.dto.DtoGeneral;
import gz.hoteles.dto.ReservasDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Reservas implements EntityGeneral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer idUsuario;
    private Date checkIn;
    private Date checkOut;
    private Double coste;

    @ManyToOne
    private Habitacion habitacion;

    @ManyToOne
    private Hotel hotel;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
        @JoinTable(
            name = "reservas_huespedes",
            joinColumns = @JoinColumn(name = "reservas_id"),
            inverseJoinColumns = @JoinColumn(name = "huespedes_id")
        )
    private List<Huesped> huespedes;

    @Override
    public DtoGeneral getDto() {
        ReservasDto dto = new ReservasDto();
        dto.setId(id);
        dto.setIdUsuario(idUsuario);
        dto.setCheckIn(checkIn);
        dto.setCheckOut(checkOut);
        dto.setCoste(coste);
        return dto;
    }
    
}
