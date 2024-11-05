package gz.hoteles.dto;

import gz.hoteles.entities.EntityGeneral;
import gz.hoteles.entities.Ubicacion;
import lombok.Data;

@Data
public class UbicacionDto implements DtoGeneral {

    private Long id;
    private String ciudad;
    private String pais;
    private String continente;

    @Override
    public EntityGeneral getEntity() {
        Ubicacion entity = new Ubicacion();

        entity.setId(id);
        entity.setCiudad(ciudad);
        entity.setPais(pais);
        entity.setContinente(continente);
        return entity;
    }
    
}
