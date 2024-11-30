package gz.hoteles.servicio.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import gz.hoteles.dto.EstadisticasDto;
import gz.hoteles.dto.HuespedDTO;
import gz.hoteles.dto.ReservasDto;
import gz.hoteles.entities.Huesped;
import gz.hoteles.entities.Reservas;
import gz.hoteles.entities.TipoHabitacion;
import gz.hoteles.repositories.HuespedRepository;
import gz.hoteles.repositories.ReservasRepository;
import gz.hoteles.support.OrderCriteria;
import gz.hoteles.support.SearchCriteria;
import gz.hoteles.support.SearchRequest;
import lombok.var;

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

    @Autowired
    ReservasRepository reservasRepository;

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
                Huesped huesped = huespedRepository.findByDni(huespedDto.getDni());
                if (huesped != null) {
                    huespedes.add(huesped);
                } else {
                    Huesped huespedSaved = this.huespedRepository.save(this.servicioHuespedes.parseEntity(huespedDto));
                    huespedes.add(huespedSaved);
                }
                
            }
            entity.setHuespedes(huespedes);
        }
        return entity;
    }

    public Page<ReservasDto> getReservasDynamicSearchOr(SearchRequest searchRequest) {
        List<SearchCriteria> searchCriteriaList = searchRequest.getListSearchCriteria();
        OrderCriteria orderCriteriaList = searchRequest.getListOrderCriteria();
        int pageSize = searchRequest.getPage().getPageSize();
        int pageIndex = searchRequest.getPage().getPageIndex();

        Specification<Reservas> spec = Specification.where(null);

        for (SearchCriteria criteria : searchCriteriaList) {
            switch (criteria.getOperation()) {
                case "equals":
                    if (criteria.getKey().equals("hotel.idUsuario")) {
                        // Filtrar por hotel.idUsuario
                        spec = spec.and(
                                (root, query, cb) -> cb.equal(root.get("hotel").get("idUsuario"), criteria.getValue()));
                    } else if (criteria.getKey().equals("habitacion.numero")) {
                        spec = spec.or((root, query, cb) -> cb
                                .equal(root.join("habitacion").get("numero"), criteria.getValue()));                    
                    } else if (criteria.getKey().equals("habitacion.tipoHabitacion")) {
                        spec = spec.or((root, query, cb) -> cb
                                .equal(root.join("habitacion").get("tipoHabitacion"), TipoHabitacion.valueOf(criteria.getValue())));
                    } else {
                        spec = spec.or((root, query, cb) -> cb.equal(root.get(criteria.getKey()), criteria.getValue()));
                    }
                    break;
                case "contains":
                    if (criteria.getKey().equals("hotel.nombre")) {
                        // Filtrar por hotel.nombre
                        spec = spec.or(
                                (root, query, cb) -> cb.like(root.get("hotel").get("nombre"), "%" + criteria.getValue() + "%"));
                    } else
                        spec = spec.or(
                                (root, query, cb) -> cb.like(root.get(criteria.getKey()),
                                        "%" + criteria.getValue() + "%"));
                    break;
                // Otras operaciones...
                default:
                    throw new IllegalArgumentException("Operador de búsqueda no válido: " + criteria.getOperation());
            }
        }

        String sortByField = orderCriteriaList.getSortBy();
        String sortDirection = orderCriteriaList.getValueSortOrder().equalsIgnoreCase("asc") ? "ASC" : "DESC";
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortByField);

        Page<Reservas> page;
        try {
            page = reservasRepository.findAll(spec, PageRequest.of(pageIndex, pageSize, sort));
        } catch (Exception e) {
            e.printStackTrace();
        }
        page = reservasRepository.findAll(spec, PageRequest.of(pageIndex, pageSize, sort));

        List<ReservasDto> reservasDtoList = page.getContent().stream()
                .map(this::parseDto)
                .collect(Collectors.toList());

        return new PageImpl<>(reservasDtoList, PageRequest.of(pageIndex, pageSize, sort),
                page.getTotalElements());

    }

    public EstadisticasDto getEstadisticasByUsuarioAndYear(Long hotelId, int year) {
    
        Map<String, Integer> reservasPorMes = new HashMap<>();
        Map<String, Double> gananciasPorMes = new HashMap<>();
        int totalHabitaciones, habitacionesReservadas, habitacionesLibres;
    
        if (hotelId != null) {
            // Filtrar por hotel
            var estadisticasReservas = reservasRepository.findReservasEstadisticasByUsuarioAndYear(hotelId, year);
            for (Object[] row : estadisticasReservas) {
                String mes = String.valueOf(row[0]); // Mes como número (1-12)
                reservasPorMes.put(mes, ((Number) row[1]).intValue());
                gananciasPorMes.put(mes, ((Number) row[2]).doubleValue());
            }
            totalHabitaciones = reservasRepository.countTotalHabitacionesByHotel(hotelId);
            habitacionesReservadas = reservasRepository.countHabitacionesReservadasByHotel(hotelId);
        } else {
            // No filtrar por hotel (Todos los hoteles)
            var estadisticasReservas = reservasRepository.findReservasEstadisticasByYear(year);
            for (Object[] row : estadisticasReservas) {
                String mes = String.valueOf(row[0]); // Mes como número (1-12)
                reservasPorMes.put(mes, ((Number) row[1]).intValue());
                gananciasPorMes.put(mes, ((Number) row[2]).doubleValue());
            }
            totalHabitaciones = reservasRepository.countTotalHabitaciones();
            habitacionesReservadas = reservasRepository.countHabitacionesReservadas();
        }
    
        habitacionesLibres = totalHabitaciones - habitacionesReservadas;
    
        // Crear DTO
        return new EstadisticasDto(
                reservasPorMes,
                gananciasPorMes,
                totalHabitaciones,
                habitacionesReservadas,
                habitacionesLibres
        );
    }
    
    
}
