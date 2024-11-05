package gz.hoteles.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import gz.hoteles.dto.DtoGeneral;
import gz.hoteles.dto.HuespedDTO;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Huesped implements EntityGeneral {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private String nombreCompleto;
    private String dni;
    private String email;

    @Override
    public DtoGeneral getDto() {
        HuespedDTO dto = new HuespedDTO();

        dto.setId(id);
        dto.setNombreCompleto(nombreCompleto);
        dto.setDni(dni);
        dto.setEmail(email);
        return dto;
    }

}

