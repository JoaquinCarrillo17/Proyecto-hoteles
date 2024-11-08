package gz.hoteles.servicio.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import gz.hoteles.dto.HotelDTO;
import gz.hoteles.entities.Hotel;
import gz.hoteles.entities.ServiciosHotelEnum;
import gz.hoteles.repositories.HabitacionRepository;
import gz.hoteles.repositories.HotelRepository;
import gz.hoteles.repositories.ReservasRepository;
import gz.hoteles.support.OrderCriteria;
import gz.hoteles.support.SearchCriteria;
import gz.hoteles.support.SearchRequest;

@Service
public class ServicioHoteles extends DtoServiceImpl<HotelDTO, Hotel>  {

    @Autowired
    HotelRepository hotelRepository;

    @Autowired
    HabitacionRepository habitacionRepository;

    @Autowired
    ReservasRepository reservasRepository;

    @Autowired
    ServicioUbicacion ubicacionService;

    private List<String> hotelPhotos = IntStream.rangeClosed(1, 20)
            .mapToObj(i -> "hotel-" + i + ".jpg")
            .collect(Collectors.toList());

    public synchronized String getAvailablePhoto() {
        // Encuentra la primera foto no asignada en la base de datos
        for (String photo : hotelPhotos) {
            if (!hotelRepository.existsByFoto(photo)) { // Método en el repositorio que verifica la existencia
                return photo;
            }
        }
        throw new RuntimeException("No hay más fotos disponibles");
    }

    @Override
    protected HotelDTO parseDto(Hotel entity) {
        HotelDTO dto = (HotelDTO) entity.getDto();
        if (entity.getUbicacion() != null) {
            dto.setUbicacion(this.ubicacionService.parseDto(entity.getUbicacion()));
        }

        // Obtener el número de habitaciones del hotel
        int numeroHabitaciones = habitacionRepository.countByHotelId(entity.getId());
        dto.setNumeroHabitaciones(numeroHabitaciones);

        // Calcular el inicio y fin del día actual
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date startOfDay = calendar.getTime();

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        Date endOfDay = calendar.getTime();
        int numeroHabitacionesReservadas = reservasRepository.countByHotelIdAndCheckInBeforeAndCheckOutAfter(
                entity.getId(), endOfDay, startOfDay);
        dto.setNumeroHabitacionesReservadas(numeroHabitacionesReservadas);

        // Calcular el número de habitaciones disponibles
        int numeroHabitacionesDisponibles = numeroHabitaciones - numeroHabitacionesReservadas;
        dto.setNumeroHabitacionesDisponibles(numeroHabitacionesDisponibles);
        
        return dto;
    }

    @Override
    protected Hotel parseEntity(HotelDTO dto) throws Exception {
        Hotel entity = (Hotel) dto.getEntity();
        String foto = getAvailablePhoto();
        entity.setFoto(foto);
        return entity;
    }

    public Page<HotelDTO> getHotelesDynamicSearchAnd(SearchRequest searchRequest) {
        List<SearchCriteria> searchCriteriaList = searchRequest.getListSearchCriteria();
        OrderCriteria orderCriteriaList = searchRequest.getListOrderCriteria();
        int pageSize = searchRequest.getPage().getPageSize();
        int pageIndex = searchRequest.getPage().getPageIndex();

        Specification<Hotel> spec = Specification.where(null);
        for (SearchCriteria criteria : searchCriteriaList) {
            switch (criteria.getOperation()) {
                case "equals":
                    if (criteria.getKey().startsWith("ubicacion.")) {
                        spec = spec.and((root, query, cb) -> cb.equal(
                                root.get("ubicacion").get(criteria.getKey().split("\\.")[1]), criteria.getValue()));
                    } else if (criteria.getKey().equals("servicios")) {
                        String[] serviciosArray = criteria.getValue().split(";");
                        for (String servicio : serviciosArray) {
                            ServiciosHotelEnum servicioEnum = ServiciosHotelEnum.valueOf(servicio);
                            spec = spec.and((root, query, cb) -> cb.isMember(servicioEnum, root.get("servicios")));
                        }
                    } else {
                        spec = spec
                                .and((root, query, cb) -> cb.equal(root.get(criteria.getKey()), criteria.getValue()));
                    }
                    break;
                case "contains":
                    if (criteria.getKey().startsWith("ubicacion.")) {
                        spec = spec.and((root, query, cb) -> cb.like(
                                root.get("ubicacion").get(criteria.getKey().split("\\.")[1]),
                                "%" + criteria.getValue() + "%"));
                    } else if (criteria.getKey().equals("servicios")) {
                        String[] serviciosArray = criteria.getValue().split(";");
                        for (String servicio : serviciosArray) {
                            ServiciosHotelEnum servicioEnum = ServiciosHotelEnum.valueOf(servicio);
                            spec = spec.and((root, query, cb) -> cb.isMember(servicioEnum, root.get("servicios")));
                        }
                    } else {
                        spec = spec.and((root, query, cb) -> cb.like(root.get(criteria.getKey()),
                                "%" + criteria.getValue() + "%"));
                    }
                    break;
                // Resto de las operaciones
                default:
                    throw new IllegalArgumentException("Operador de búsqueda no válido: " + criteria.getOperation());
            }
        }

        String sortByField = orderCriteriaList.getSortBy();
        String sortDirection = orderCriteriaList.getValueSortOrder().equalsIgnoreCase("asc") ? "ASC" : "DESC";
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortByField);

        Page<Hotel> page = hotelRepository.findAll(spec, PageRequest.of(pageIndex, pageSize, sort));

        List<HotelDTO> hotelDTOList = page.getContent().stream()
                .map(this::parseDto)
                .collect(Collectors.toList());

        Page<HotelDTO> hotelDTOPage = new PageImpl<>(hotelDTOList, PageRequest.of(pageIndex, pageSize, sort),
                page.getTotalElements());

        return hotelDTOPage;
    }

    public Page<HotelDTO> getHotelesDynamicSearchOr(SearchRequest searchRequest) {

        List<SearchCriteria> searchCriteriaList = searchRequest.getListSearchCriteria();
        OrderCriteria orderCriteriaList = searchRequest.getListOrderCriteria();
        int pageSize = searchRequest.getPage().getPageSize();
        int pageIndex = searchRequest.getPage().getPageIndex();

        Specification<Hotel> spec = Specification.where(null);

        Optional<SearchCriteria> idUsuarioCriteria = searchCriteriaList.stream()
            .filter(criteria -> "idUsuario".equals(criteria.getKey()))
            .findFirst();

        if (idUsuarioCriteria.isPresent()) {
            // Añadir filtro idUsuario en forma de AND
            spec = spec.and((root, query, cb) -> 
                cb.equal(root.get("idUsuario"), idUsuarioCriteria.get().getValue()));
        }

        for (SearchCriteria criteria : searchCriteriaList) {
            switch (criteria.getOperation()) {
                case "equals":
                    if (criteria.getKey().startsWith("ubicacion.")) {
                        spec = spec.or((root, query, cb) -> cb.equal(
                                root.get("ubicacion").get(criteria.getKey().split("\\.")[1]), criteria.getValue()));
                    } else if (criteria.getKey().equals("servicios")) {
                        spec = spec.or((root, query, cb) -> cb.isMember(ServiciosHotelEnum.valueOf(criteria.getValue()),
                                root.get("servicios")));
                    } else {
                        spec = spec.or((root, query, cb) -> cb.equal(root.get(criteria.getKey()), criteria.getValue()));
                    }
                    break;
                case "contains":
                    if (criteria.getKey().startsWith("ubicacion.")) {
                        spec = spec.or((root, query, cb) -> cb.like(
                                root.get("ubicacion").get(criteria.getKey().split("\\.")[1]),
                                "%" + criteria.getValue() + "%"));
                    } else if (criteria.getKey().equals("servicios")) {
                        spec = spec.or((root, query, cb) -> cb.isMember(ServiciosHotelEnum.valueOf(criteria.getValue()),
                                root.get("servicios")));
                    } else {
                        spec = spec.or((root, query, cb) -> cb.like(root.get(criteria.getKey()),
                                "%" + criteria.getValue() + "%"));
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Operador de búsqueda no válido: " + criteria.getOperation());
            }
        }

        String sortByField = orderCriteriaList.getSortBy();
        String sortDirection = orderCriteriaList.getValueSortOrder().equalsIgnoreCase("asc") ? "ASC" : "DESC";
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortByField);

        Page<Hotel> page = hotelRepository.findAll(spec, PageRequest.of(pageIndex, pageSize, sort));

        List<HotelDTO> hotelDTOList = page.getContent().stream()
                .map(this::parseDto)
                .collect(Collectors.toList());

        Page<HotelDTO> hotelDTOPage = new PageImpl<>(hotelDTOList, PageRequest.of(pageIndex, pageSize, sort),
                page.getTotalElements());

        return hotelDTOPage;

    }

}
