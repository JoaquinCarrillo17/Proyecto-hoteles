package gz.hoteles.servicio.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gz.hoteles.dto.HuespedDTO;
import gz.hoteles.dto.ReservasDto;
import gz.hoteles.entities.Huesped;
import gz.hoteles.entities.Reservas;
import gz.hoteles.repositories.HuespedRepository;

@Service
public class ReservasServiceImpl extends DtoServiceImpl<ReservasDto, Reservas> {

    @Autowired
    ServicioHabitaciones servicioHabitaciones;

    @Autowired
    ServicioHoteles servicioHoteles;

    @Autowired
    ServicioHuespedes servicioHuespedes;

    @Autowired
    HuespedRepository huespedRepository;

    @Override
    protected ReservasDto parseDto(Reservas entity) {
        ReservasDto dto = (ReservasDto) entity.getDto();
        if (entity.getHabitacion() != null) {
            dto.setHabitacion(this.servicioHabitaciones.parseDto(entity.getHabitacion()));
        }
        if (entity.getHotel() != null) {
            dto.setHotel(this.servicioHoteles.parseDto(entity.getHotel()));
        }
        if (entity.getHuespedes() != null && !entity.getHuespedes().isEmpty()) {
            List<HuespedDTO> huespedesDto = new ArrayList<>();
            for (Huesped huesped : entity.getHuespedes()) {
                huespedesDto.add(this.servicioHuespedes.parseDto(huesped));
            }
            dto.setHuespedes(huespedesDto);
        }
        return dto;
    }

    @Override
    protected Reservas parseEntity(ReservasDto dto) throws Exception {
        Reservas entity = (Reservas) dto.getEntity();
        if (dto.getHabitacion()!= null) {
            entity.setHabitacion(this.servicioHabitaciones.parseEntity(dto.getHabitacion()));
        }
        if (dto.getHotel()!= null) {
            entity.setHotel(this.servicioHoteles.parseEntity(dto.getHotel()));
        }
        if (dto.getHuespedes()!= null &&!dto.getHuespedes().isEmpty()) {
            List<Huesped> huespedes = new ArrayList<>();
            for (HuespedDTO huespedDto : dto.getHuespedes()) {
                Huesped huespedSaved = this.huespedRepository.save(this.servicioHuespedes.parseEntity(huespedDto));
                huespedes.add(huespedSaved);
            }
            entity.setHuespedes(huespedes);
        }
        return entity;
    }
    
}
