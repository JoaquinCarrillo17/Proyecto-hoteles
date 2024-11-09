package gz.hoteles.dto;

import java.util.Set;

import gz.hoteles.entities.EntityGeneral;
import gz.hoteles.entities.Hotel;
import gz.hoteles.entities.ServiciosHotelEnum;
import lombok.Data;

@Data
public class HotelDTO implements DtoGeneral {
    private Long id;
    private String nombre;
    private String direccion;
    private String telefono;
    private String email;
    private String sitioWeb;
    private Integer idUsuario;
    private String foto;
    private UbicacionDto ubicacion;
    private Set<ServiciosHotelEnum> servicios;
    private int numeroHabitaciones;
    private int numeroHabitacionesDisponibles;
    private int numeroHabitacionesReservadas;
    
    @Override
    public EntityGeneral getEntity() {
        Hotel entity = new Hotel();
        entity.setId(id);
        entity.setNombre(nombre);
        entity.setDireccion(direccion);
        entity.setTelefono(telefono);
        entity.setIdUsuario(idUsuario);
        entity.setEmail(email);
        entity.setSitioWeb(sitioWeb);
        entity.setFoto(foto);
        entity.setServicios(servicios);
        return entity;
    }

}
