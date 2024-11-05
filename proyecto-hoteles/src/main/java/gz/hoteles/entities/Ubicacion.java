package gz.hoteles.entities;

import javax.persistence.*;

import gz.hoteles.dto.DtoGeneral;
import gz.hoteles.dto.UbicacionDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ubicacion implements EntityGeneral{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ciudad;
    private String pais;
    private String continente;

    @Override
    public DtoGeneral getDto() {
        UbicacionDto dto = new UbicacionDto();

        dto.setId(id);
        dto.setCiudad(ciudad);
        dto.setContinente(continente);
        dto.setPais(pais);
        return dto;
    }

}

