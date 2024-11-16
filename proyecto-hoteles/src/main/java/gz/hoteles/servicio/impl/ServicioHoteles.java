package gz.hoteles.servicio.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import gz.hoteles.entities.TipoHabitacion;
import gz.hoteles.entities.Ubicacion;
import gz.hoteles.repositories.HabitacionRepository;
import gz.hoteles.repositories.HotelRepository;
import gz.hoteles.repositories.ReservasRepository;
import gz.hoteles.repositories.UbicacionRepository;
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

    @Autowired
    UbicacionRepository ubicacionRepository;

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public Date parseDate(String dateString) {
        if (dateString == null) {
            return null; // Si la cadena es nula, devolvemos null directamente
        }
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null; // Manejar el caso en el que la fecha no se puede analizar
        }
    }

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

        if (dto.getUbicacion() != null) {
            Ubicacion ubi = ubicacionRepository.findByCiudad(dto.getUbicacion().getCiudad());
            if (ubi != null) {
                entity.setUbicacion(ubi);
            } else {
                Ubicacion toSave = this.ubicacionService.parseEntity(dto.getUbicacion());
                toSave.setId(null);
                Ubicacion ubiGuardada = ubicacionRepository.save(toSave);
                entity.setUbicacion(ubiGuardada);
            }
        }

        return entity;
    }

    public Page<HotelDTO> getHotelesDynamicSearchAnd(SearchRequest searchRequest) {
        List<SearchCriteria> searchCriteriaList = searchRequest.getListSearchCriteria();
        OrderCriteria orderCriteriaList = searchRequest.getListOrderCriteria();
        int pageSize = searchRequest.getPage().getPageSize();
        int pageIndex = searchRequest.getPage().getPageIndex();

        Specification<Hotel> spec = Specification.where(null);

        TipoHabitacion tipoHabitacionFilter = null;

        for (SearchCriteria criteria : searchCriteriaList) {
            if (criteria.getKey().equals("checkIn") || criteria.getKey().equals("checkOut")) {
                continue;
            }
            if (criteria.getKey().equals("tipoHabitacion")) {
                tipoHabitacionFilter = TipoHabitacion.valueOf(criteria.getValue());
                continue;
            }
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
    
        // Obtener todos los hoteles que cumplen el filtro inicial (sin paginación)
        List<Hotel> hotelesFiltrados = hotelRepository.findAll(spec);
    
        final Date checkInDate = parseDate(searchRequest.getListSearchCriteria().stream()
                .filter(criteria -> criteria.getKey().equals("checkIn"))
                .map(SearchCriteria::getValue)
                .findFirst().orElse(null));
    
        final Date checkOutDate = parseDate(searchRequest.getListSearchCriteria().stream()
                .filter(criteria -> criteria.getKey().equals("checkOut"))
                .map(SearchCriteria::getValue)
                .findFirst().orElse(null));
    
        // Aplicar filtro de disponibilidad y tipo de habitación
        final TipoHabitacion finalTipoHabitacionFilter = tipoHabitacionFilter;
        List<Hotel> hotelesDisponibles = hotelesFiltrados.stream()
                .filter(hotel -> tieneHabitacionesDisponibles(hotel, checkInDate, checkOutDate, finalTipoHabitacionFilter))
                .collect(Collectors.toList());
    
        // Calcular total después de filtrar
        long totalElements = hotelesDisponibles.size();
    
        // Aplicar paginación manual
        List<Hotel> hotelesPaginados = hotelesDisponibles.stream()
                .skip((long) pageIndex * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());
    
        List<HotelDTO> hotelDTOList = hotelesPaginados.stream()
                .map(this::parseDto)
                .collect(Collectors.toList());
    
        return new PageImpl<>(hotelDTOList, PageRequest.of(pageIndex, pageSize, sort), totalElements);
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

    private boolean tieneHabitacionesDisponibles(Hotel hotel, Date checkInDate, Date checkOutDate, TipoHabitacion tipoHabitacion) {
        if (tipoHabitacion == null && checkInDate == null && checkOutDate == null) {
            return true; // Si no hay filtros, considerar que el hotel tiene habitaciones disponibles
        }
    
        if (checkInDate == null || checkOutDate == null) {
            // Solo filtrar por tipo de habitación si no hay rango de fechas
            return habitacionRepository.existsByHotelAndTipoHabitacion(hotel, tipoHabitacion);
        }
    
        if (tipoHabitacion == null) {
            // Filtrar por rango de fechas solamente
            return habitacionRepository.countByHotelAndDisponibilidad(hotel, checkInDate, checkOutDate) > 0;
        } else {
            // Filtrar por rango de fechas y tipo de habitación
            return habitacionRepository.countByHotelAndDisponibilidadAndTipo(hotel, checkInDate, checkOutDate, tipoHabitacion) > 0;
        }
    }
    
    

}
