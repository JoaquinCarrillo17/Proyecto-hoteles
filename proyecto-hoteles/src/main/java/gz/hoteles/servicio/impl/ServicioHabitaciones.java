package gz.hoteles.servicio.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gz.hoteles.dto.HabitacionDTO;
import gz.hoteles.entities.Habitacion;
import gz.hoteles.entities.Hotel;
import gz.hoteles.repositories.HotelRepository;

@Service
public class ServicioHabitaciones extends DtoServiceImpl<HabitacionDTO, Habitacion> {

    @Autowired
    HotelRepository hotelRepository;

    @Override
    protected HabitacionDTO parseDto(Habitacion entity) {
        HabitacionDTO dto = (HabitacionDTO) entity.getDto();

        if (entity.getHotel() != null) {
            dto.setNombreHotel(entity.getHotel().getNombre());
            dto.setIdUsuario(entity.getHotel().getIdUsuario());
        }

        return dto;
    }

    @Override
    protected Habitacion parseEntity(HabitacionDTO dto) throws Exception {
        Habitacion entity = (Habitacion) dto.getEntity();
        if (dto.getIdUsuario() != null) {
            Hotel hotel = hotelRepository.findByIdUsuario(dto.getIdUsuario());
            if (hotel!= null) {
                entity.setHotel(hotel);
            } 
        }
        return entity;
    }
    
}
