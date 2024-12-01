package gz.hoteles.servicio.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import gz.hoteles.dto.HabitacionDTO;
import gz.hoteles.entities.Habitacion;
import gz.hoteles.entities.ServiciosHabitacionEnum;
import gz.hoteles.entities.TipoHabitacion;
import gz.hoteles.repositories.HabitacionRepository;
import gz.hoteles.repositories.HotelRepository;
import gz.hoteles.repositories.ReservasRepository;
import gz.hoteles.support.OrderCriteria;
import gz.hoteles.support.SearchCriteria;
import gz.hoteles.support.SearchRequest;

@Service
public class ServicioHabitaciones extends DtoServiceImpl<HabitacionDTO, Habitacion> {

    @Autowired
    HotelRepository hotelRepository;

    @Autowired
    ServicioHoteles hotelService;

    @Autowired
    HabitacionRepository habitacionRepository;

    @Autowired
    ReservasRepository reservasRepository;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    // Método para convertir una cadena de fecha en un objeto Date
    public Date parseDate(String dateString) {
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            return null; // Manejar el caso en el que la fecha no se puede analizar
        }
    }

    private String asignarFotoAleatoria() {
        Random random = new Random();
        int fotoIndex = random.nextInt(5) + 1; // Genera un número entre 1 y 5
        return "habitacion-" + fotoIndex + ".jpg";
    }

    @Override
    protected HabitacionDTO parseDto(Habitacion entity) {
        HabitacionDTO dto = (HabitacionDTO) entity.getDto();

        if (entity.getHotel() != null) {
            dto.setHotel(this.hotelService.parseDto(entity.getHotel()));
        }

        return dto;
    }

    @Override
    protected Habitacion parseEntity(HabitacionDTO dto) throws Exception {
        Habitacion entity = (Habitacion) dto.getEntity();

        if (dto.getHotel() != null) {
            entity.setHotel(this.hotelService.parseEntity(dto.getHotel()));
        }

        entity.setFoto(this.asignarFotoAleatoria());
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
                    } else if (criteria.getKey().equals("tipoHabitacion")) {
                        spec = spec.or((root, query, cb) -> cb
                                .equal(root.get("tipoHabitacion"), TipoHabitacion.valueOf(criteria.getValue())));
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
            if (criteria.getKey().equals("checkIn") || criteria.getKey().equals("checkOut")) {
                continue;
            }
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

        // Obtener todas las habitaciones que cumplen el filtro inicial (sin paginación)
        List<Habitacion> habitacionesFiltradas = habitacionRepository.findAll(spec);

        // Obtener fechas de búsqueda, si están presentes
        Optional<Date> checkInDateOptional = searchRequest.getListSearchCriteria().stream()
                .filter(criteria -> criteria.getKey().equals("checkIn"))
                .map(SearchCriteria::getValue)
                .map(this::parseDate)
                .findFirst();

        Optional<Date> checkOutDateOptional = searchRequest.getListSearchCriteria().stream()
                .filter(criteria -> criteria.getKey().equals("checkOut"))
                .map(SearchCriteria::getValue)
                .map(this::parseDate)
                .findFirst();

        List<Habitacion> habitacionesDisponibles;
        if (checkInDateOptional.isPresent() && checkOutDateOptional.isPresent()) {
            // Filtrar habitaciones por disponibilidad solo si ambas fechas están presentes
            Date checkInDate = checkInDateOptional.get();
            Date checkOutDate = checkOutDateOptional.get();
            habitacionesDisponibles = habitacionesFiltradas.stream()
                    .filter(habitacion -> verificarDisponibilidad(habitacion, checkInDate, checkOutDate))
                    .collect(Collectors.toList());
        } else {
            // Si no hay fechas, usar las habitaciones filtradas sin más
            habitacionesDisponibles = habitacionesFiltradas;
        }

        // Calcular total después de filtrar
        long totalElements = habitacionesDisponibles.size();

        // Aplicar paginación manual
        List<Habitacion> habitacionesPaginadas = habitacionesDisponibles.stream()
                .skip((long) pageIndex * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());

        List<HabitacionDTO> habitacionDTOList = habitacionesPaginadas.stream()
                .map(this::parseDto)
                .collect(Collectors.toList());

        return new PageImpl<>(habitacionDTOList, PageRequest.of(pageIndex, pageSize, sort), totalElements);
    }

    private boolean verificarDisponibilidad(Habitacion habitacion, Date checkInDate, Date checkOutDate) {
        // Si existe al menos una reserva que se solape, la habitación no está disponible
        return !reservasRepository.existsByHabitacionAndFechasSolapadas(habitacion, checkInDate, checkOutDate);
    }

    public List<Habitacion> crearHabitaciones(List<HabitacionDTO> habitacionesDto) throws Exception {

        List<Habitacion> habitaciones = new ArrayList<>();

        for (HabitacionDTO dto : habitacionesDto) {
            Habitacion habitacion = parseEntity(dto);
            habitaciones.add(habitacionRepository.save(habitacion));
        }

        return habitaciones;
    }

}
