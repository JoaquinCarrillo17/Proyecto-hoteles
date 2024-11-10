package gz.hoteles.dto;

import java.util.Date;
import java.util.List;

import gz.hoteles.entities.EntityGeneral;
import gz.hoteles.entities.Reservas;
import lombok.Data;

@Data
public class ReservasDto implements DtoGeneral {

    private Long id;
    private Integer idUsuario;
    private Date checkIn;
    private Date checkOut;
    private Double coste;
    private HabitacionDTO habitacion;
    private HotelDTO hotel;
    private List<HuespedDTO> huespedes;

    @Override
    public EntityGeneral getEntity() {
        Reservas entity = new Reservas();
        
        entity.setId(id);
        entity.setIdUsuario(idUsuario);
        entity.setCheckIn(checkIn);
        entity.setCheckOut(checkOut);
        entity.setCoste(coste);
        return entity;
    }
    
}
