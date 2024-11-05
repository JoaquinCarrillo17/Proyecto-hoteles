package gz.hoteles.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import gz.hoteles.dto.HotelDTO;
import gz.hoteles.entities.Habitacion;
import gz.hoteles.entities.Hotel;
import gz.hoteles.entities.ServiciosHotelEnum;
import gz.hoteles.repositories.HotelRepository;
import gz.hoteles.servicio.impl.ServicioHoteles;
import gz.hoteles.support.OrderCriteria;
import gz.hoteles.support.SearchCriteria;
import gz.hoteles.support.SearchRequest;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/hoteles")
public class HotelController extends ControllerDto<HotelDTO>{

    @Autowired
    HotelRepository hotelRepository;
    @Autowired
    ServicioHoteles servicioHoteles;

   /*  @PostMapping("/dynamicFilterAnd")
    public ResponseEntity<?> getFilteredByDynamicSearchAnd(@RequestBody SearchRequest searchRequest) {

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
                .map(HotelController::convertToDtoHotel)
                .collect(Collectors.toList());

        Page<HotelDTO> hotelDTOPage = new PageImpl<>(hotelDTOList, PageRequest.of(pageIndex, pageSize, sort),
                page.getTotalElements());

        return ResponseEntity.ok(hotelDTOPage);
    }

    @PostMapping("/dynamicFilterOr")
    public ResponseEntity<?> getFilteredByDynamicSearchOr(@RequestBody SearchRequest searchRequest) {

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
                // Otras operaciones...
                default:
                    throw new IllegalArgumentException("Operador de búsqueda no válido: " + criteria.getOperation());
            }
        }

        String sortByField = orderCriteriaList.getSortBy();
        String sortDirection = orderCriteriaList.getValueSortOrder().equalsIgnoreCase("asc") ? "ASC" : "DESC";
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortByField);

        Page<Hotel> page = hotelRepository.findAll(spec, PageRequest.of(pageIndex, pageSize, sort));

        List<HotelDTO> hotelDTOList = page.getContent().stream()
                .map(HotelController::convertToDtoHotel)
                .collect(Collectors.toList());

        Page<HotelDTO> hotelDTOPage = new PageImpl<>(hotelDTOList, PageRequest.of(pageIndex, pageSize, sort),
                page.getTotalElements());

        return ResponseEntity.ok(hotelDTOPage);
    }*/


}
