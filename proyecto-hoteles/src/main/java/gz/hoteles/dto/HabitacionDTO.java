package gz.hoteles.dto;

import java.util.Set;

import gz.hoteles.entities.EntityGeneral;
import gz.hoteles.entities.Habitacion;
import gz.hoteles.entities.ServiciosHabitacionEnum;
import gz.hoteles.entities.TipoHabitacion;
import lombok.Data;

@Data
public class HabitacionDTO implements DtoGeneral {
    
    private Long id;
    private String numero;
    private TipoHabitacion tipoHabitacion;
    private float precioNoche;
    private String nombreHotel;
    private Integer idUsuario;
    private String foto;
    private Set<ServiciosHabitacionEnum> servicios;

    @Override
    public EntityGeneral getEntity() {
        Habitacion entity = new Habitacion();
        
        entity.setId(id);
        entity.setNumero(numero);
        entity.setTipoHabitacion(tipoHabitacion);
        entity.setPrecioNoche(precioNoche);
        entity.setFoto(foto);
        entity.setServicios(servicios);
        return entity;
    }
}
