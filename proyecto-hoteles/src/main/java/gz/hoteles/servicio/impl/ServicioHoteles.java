package gz.hoteles.servicio.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import gz.hoteles.dto.HotelDTO;
import gz.hoteles.entities.Habitacion;
import gz.hoteles.entities.Hotel;
import gz.hoteles.entities.Reservas;
import gz.hoteles.entities.ServiciosHotelEnum;
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

        Date checkInDate = null;
        Date checkOutDate = null;

        // Formato de fecha esperado
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

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
                    } else if (criteria.getKey().equals("checkIn")) {
                        try {
                            checkInDate = dateFormat.parse((String) criteria.getValue());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    } else if (criteria.getKey().equals("checkOut")) {
                        try {
                            checkOutDate = dateFormat.parse((String) criteria.getValue());
                        } catch (ParseException e) {
                            e.printStackTrace();
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

        /*// Si ambas fechas están presentes, añadir lógica de disponibilidad
        if (checkInDate != null && checkOutDate != null) {
            final Date finalCheckInDate = checkInDate;
            final Date finalCheckOutDate = checkOutDate;

            spec = spec.and((root, query, cb) -> {
                // Subconsulta para verificar disponibilidad de habitaciones
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<Habitacion> habitacionRoot = subquery.from(Habitacion.class);
                Join<Habitacion, Reservas> reservasJoin = habitacionRoot.join("reservas", JoinType.LEFT);

                subquery.select(cb.count(habitacionRoot.get("id")))
                        .where(
                            cb.equal(habitacionRoot.get("hotel"), root), // Mismo hotel
                            cb.or(
                                cb.isNull(reservasJoin), // No hay reservas en esta habitación
                                cb.and(
                                    cb.lessThanOrEqualTo(reservasJoin.get("checkOut").as(Date.class), finalCheckInDate), // La reserva termina antes de la fecha de check-in
                                    cb.greaterThanOrEqualTo(reservasJoin.get("checkIn").as(Date.class), finalCheckOutDate) // La reserva empieza después de la fecha de check-out
                                )
                            )
                        );

                // El hotel debe tener al menos una habitación disponible
                return cb.greaterThan(subquery, 0L);
            });
        }*/

        String sortByField = orderCriteriaList.getSortBy();
        String sortDirection = orderCriteriaList.getValueSortOrder().equalsIgnoreCase("asc") ? "ASC" : "DESC";
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortByField);
        Page<Hotel> page = null;
        try {
            page = hotelRepository.findAll(spec, PageRequest.of(pageIndex, pageSize, sort));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Page<Hotel> page = hotelRepository.findAll(spec, PageRequest.of(pageIndex, pageSize, sort));

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
