package gz.hoteles.servicio.impl;

import org.springframework.stereotype.Service;

import gz.hoteles.dto.HuespedDTO;
import gz.hoteles.entities.Huesped;

@Service
public class ServicioHuespedes extends DtoServiceImpl<HuespedDTO, Huesped> {

    @Override
    protected HuespedDTO parseDto(Huesped entity) {
        return (HuespedDTO) entity.getDto();
    }

    @Override
    protected Huesped parseEntity(HuespedDTO dto) throws Exception {
        return (Huesped) dto.getEntity();
    }
    
}
