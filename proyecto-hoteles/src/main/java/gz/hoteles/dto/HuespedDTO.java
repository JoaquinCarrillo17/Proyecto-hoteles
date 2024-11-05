package gz.hoteles.dto;


import gz.hoteles.entities.EntityGeneral;
import gz.hoteles.entities.Huesped;
import lombok.Data;

@Data
public class HuespedDTO implements DtoGeneral {
    
    private Long id;
    private String nombreCompleto;
    private String dni;
    private String email;

    @Override
    public EntityGeneral getEntity() {
        Huesped entity = new Huesped();
        
        entity.setId(id);
        entity.setNombreCompleto(nombreCompleto);
        entity.setDni(dni);
        entity.setEmail(email);
        return entity;
    }

}
