package gz.hoteles.entities;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

    @OneToMany(mappedBy = "ubicacion", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<Hotel> hoteles = new HashSet<>();

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

