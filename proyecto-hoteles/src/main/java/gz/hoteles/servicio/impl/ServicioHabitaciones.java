package gz.hoteles.servicio.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import gz.hoteles.controller.HabitacionController;
import gz.hoteles.dto.HabitacionDTO;
import gz.hoteles.entities.Habitacion;
import gz.hoteles.entities.Hotel;
import gz.hoteles.entities.ServiciosHabitacionEnum;
import gz.hoteles.entities.TipoHabitacion;
import gz.hoteles.repositories.HabitacionRepository;
import gz.hoteles.repositories.HotelRepository;
import gz.hoteles.support.OrderCriteria;
import gz.hoteles.support.SearchCriteria;
import gz.hoteles.support.SearchRequest;

@Service
public class ServicioHabitaciones extends DtoServiceImpl<HabitacionDTO, Habitacion> {

    @Autowired
    HotelRepository hotelRepository;

    @Autowired
    HabitacionRepository habitacionRepository;

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


    public Page<HabitacionDTO> getHabitacionesDynamicSearchOr(SearchRequest searchRequest) {
        List<SearchCriteria> searchCriteriaList = searchRequest.getListSearchCriteria();
        OrderCriteria orderCriteriaList = searchRequest.getListOrderCriteria();
        int pageSize = searchRequest.getPage().getPageSize();
        int pageIndex = searchRequest.getPage().getPageIndex();

        Specification<Habitacion> spec = Specification.where(null);

        for (SearchCriteria criteria : searchCriteriaList) {
            switch (criteria.getOperation()) {
                case "equals":
                    if (criteria.getKey().equals("hotel.idUsuario")) {
                        // Filtrar por hotel.idUsuario
                        spec = spec.and(
                                (root, query, cb) -> cb.equal(root.get("hotel").get("idUsuario"), criteria.getValue()));
                    } else if (criteria.getKey().equals("servicios")) {
                        // Filtrar por servicios de la habitación
                        spec = spec.or((root, query, cb) -> cb
                                .isMember(ServiciosHabitacionEnum.valueOf(criteria.getValue()), root.get("servicios")));
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

        Page<Habitacion> page = habitacionRepository.findAll(spec, PageRequest.of(pageIndex, pageSize, sort));

        List<HabitacionDTO> habitacionDTOList = page.getContent().stream()
                .map(this::parseDto)
                .collect(Collectors.toList());

        Page<HabitacionDTO> habitacionDTOPage = new PageImpl<>(habitacionDTOList, PageRequest.of(pageIndex, pageSize, sort),
                page.getTotalElements());

        return habitacionDTOPage;
    }
    

    public Page<HabitacionDTO> getHabitacionesDynamicSearchAnd(SearchRequest searchRequest) {
        List<SearchCriteria> searchCriteriaList = searchRequest.getListSearchCriteria();
        OrderCriteria orderCriteriaList = searchRequest.getListOrderCriteria();
        int pageSize = searchRequest.getPage().getPageSize();
        int pageIndex = searchRequest.getPage().getPageIndex();

        Specification<Habitacion> spec = Specification.where(null);

        for (SearchCriteria criteria : searchCriteriaList) {
            switch (criteria.getOperation()) {
                case "equals":
                    if (criteria.getKey().equals("hotel.idUsuario")) {
                        // Filtrar por hotel.idUsuario
                        spec = spec.and(
                                (root, query, cb) -> cb.equal(root.get("hotel").get("idUsuario"), criteria.getValue()));
                    } else if (criteria.getKey().equals("tipoHabitacion")) {
                        // Filtrar por hotel.idUsuario
                        spec = spec.and(
                                (root, query, cb) -> cb.equal(root.get(criteria.getKey()),
                                        TipoHabitacion.valueOf(criteria.getValue())));
                    } else if (criteria.getKey().equals("hotel.id")) {
                        // Filtrar por hotel.id
                        spec = spec.and(
                                (root, query, cb) -> cb.equal(root.get("hotel").get("id"), criteria.getValue()));
                    } else if (criteria.getKey().equals("servicios")) {
                        String[] serviciosArray = criteria.getValue().split(";");
                        for (String servicio : serviciosArray) {
                            ServiciosHabitacionEnum servicioEnum = ServiciosHabitacionEnum.valueOf(servicio);
                            spec = spec.and((root, query, cb) -> cb.isMember(servicioEnum, root.get("servicios")));
                        }
                    } else {
                        spec = spec
                                .and((root, query, cb) -> cb.equal(root.get(criteria.getKey()), criteria.getValue()));
                    }
                    break;
                case "contains":
                    if (criteria.getKey().equals("hotel.nombre")) {
                        // Filtrar por hotel.nombre
                        spec = spec.and(
                                (root, query, cb) -> cb.like(root.get("hotel").get("nombre"),
                                        "%" + criteria.getValue() + "%"));
                    } else if (criteria.getKey().equals("servicios")) {
                        String[] serviciosArray = criteria.getValue().split(";");
                        for (String servicio : serviciosArray) {
                            ServiciosHabitacionEnum servicioEnum = ServiciosHabitacionEnum.valueOf(servicio);
                            spec = spec.and((root, query, cb) -> cb.isMember(servicioEnum, root.get("servicios")));
                        }
                    } else {
                        spec = spec.and(
                                (root, query, cb) -> cb.like(root.get(criteria.getKey()),
                                        "%" + criteria.getValue() + "%"));
                    }
                    break;
                case "greaterThanOrEqual":
                    if (criteria.getKey().equals("precioNoche")) {
                        spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("precioNoche"), criteria.getValue()));
                    }
                    break;
                case "lessThanOrEqual":
                    if (criteria.getKey().equals("precioNoche")) {
                        spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("precioNoche"), criteria.getValue()));
                    }
                    break;
                // Otras operaciones...
                default:
                    throw new IllegalArgumentException("Operador de búsqueda no válido: " + criteria.getOperation());
            }
        }

        String sortByField = orderCriteriaList.getSortBy();
        String sortDirection = orderCriteriaList.getValueSortOrder().equalsIgnoreCase("asc") ? "ASC" : "DESC";
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortByField);

        Page<Habitacion> page = habitacionRepository.findAll(spec, PageRequest.of(pageIndex, pageSize, sort));

        List<HabitacionDTO> habitacionDTOList = page.getContent().stream()
                .map(this::parseDto)
                .collect(Collectors.toList());

        Page<HabitacionDTO> habitacionDTOPage = new PageImpl<>(habitacionDTOList,
                PageRequest.of(pageIndex, pageSize, sort),
                page.getTotalElements());

        return habitacionDTOPage;
    }

}
