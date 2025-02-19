package gz.hoteles.servicio.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import gz.hoteles.controller.HuespedController;
import gz.hoteles.dto.HabitacionDTO;
import gz.hoteles.dto.HuespedDTO;
import gz.hoteles.entities.Huesped;
import gz.hoteles.entities.Reservas;
import gz.hoteles.repositories.HuespedRepository;
import gz.hoteles.support.OrderCriteria;
import gz.hoteles.support.SearchCriteria;
import gz.hoteles.support.SearchRequest;

@Service
public class ServicioHuespedes extends DtoServiceImpl<HuespedDTO, Huesped> {

    @Autowired
    HuespedRepository huespedRepository;

    @Override
    protected HuespedDTO parseDto(Huesped entity) {
        return (HuespedDTO) entity.getDto();
    }

    @Override
    protected Huesped parseEntity(HuespedDTO dto) throws Exception {
        return (Huesped) dto.getEntity();
    }


    public Page<HuespedDTO> getHuespedesDynamicSearchAnd(SearchRequest searchRequest) {
        List<SearchCriteria> searchCriteriaList = searchRequest.getListSearchCriteria();
        OrderCriteria orderCriteriaList = searchRequest.getListOrderCriteria();
        int pageSize = searchRequest.getPage().getPageSize();
        int pageIndex = searchRequest.getPage().getPageIndex();

        Specification<Huesped> spec = Specification.where(null);
        for (SearchCriteria criteria : searchCriteriaList) {
            switch (criteria.getOperation()) {
                case "equals":
                    spec = spec.and((root, query, cb) -> cb.equal(root.get(criteria.getKey()), criteria.getValue()));
                    break;
                case "contains":
                    spec = spec.and((root, query, cb) -> cb.like(root.get(criteria.getKey()),
                            "%" + criteria.getValue() + "%"));
                    break;
                default:
                    throw new IllegalArgumentException(
                            "Operador de búsqueda no válido: " + criteria.getOperation());
            }
        }

        String sortByField = orderCriteriaList.getSortBy();
        String sortDirection = orderCriteriaList.getValueSortOrder().equalsIgnoreCase("asc") ? "ASC" : "DESC";
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortByField);

        Page<Huesped> page = huespedRepository.findAll(spec, PageRequest.of(pageIndex, pageSize, sort));

        List<HuespedDTO> huespedDTOList = page.getContent().stream()
                .map(this::parseDto)
                .collect(Collectors.toList());

        Page<HuespedDTO> huespedDTOPage = new PageImpl<>(huespedDTOList, PageRequest.of(pageIndex, pageSize, sort),
                page.getTotalElements());
        
        return huespedDTOPage;
    }

    public Page<HuespedDTO> getHuespedesDynamicSearchOr(SearchRequest searchRequest) {

        List<SearchCriteria> searchCriteriaList = searchRequest.getListSearchCriteria();
        OrderCriteria orderCriteriaList = searchRequest.getListOrderCriteria();
        int pageSize = searchRequest.getPage().getPageSize();
        int pageIndex = searchRequest.getPage().getPageIndex();

        Specification<Huesped> spec = Specification.where(null);

        // Verificar si existe el criterio "idUsuario"
        Optional<SearchCriteria> idUsuarioCriteria = searchCriteriaList.stream()
                .filter(criteria -> "idUsuario".equals(criteria.getKey()))
                .findFirst();

        if (idUsuarioCriteria.isPresent()) {
            String idUsuario = idUsuarioCriteria.get().getValue();

            // Añadir la especificación para el filtro de idUsuario
            spec = spec.and((root, query, cb) -> {
                // Crear una subquery para filtrar huéspedes con reservas en el hotel del
                // idUsuario
                query.distinct(true);
                javax.persistence.criteria.Subquery<Long> subquery = query.subquery(Long.class);
                javax.persistence.criteria.Root<Reservas> reservasRoot = subquery.from(Reservas.class);
                subquery.select(reservasRoot.join("huespedes").get("id"))
                        .where(cb.equal(reservasRoot.join("hotel").get("idUsuario"), idUsuario));

                return cb.in(root.get("id")).value(subquery);
            });

            // Eliminar el criterio "idUsuario" de la lista para evitar procesarlo como un
            // campo normal
            searchCriteriaList = searchCriteriaList.stream()
                    .filter(criteria -> !"idUsuario".equals(criteria.getKey()))
                    .collect(Collectors.toList());
        }

        for (SearchCriteria criteria : searchCriteriaList) {            
                switch (criteria.getOperation()) {
                    case "equals":
                        spec = spec.or((root, query, cb) -> cb.equal(root.get(criteria.getKey()), criteria.getValue()));
                        break;
                    case "contains":
                        spec = spec.or((root, query, cb) -> cb.like(root.get(criteria.getKey()),
                                "%" + criteria.getValue() + "%"));
                        break;
                    default:
                        throw new IllegalArgumentException(
                                "Operador de búsqueda no válido: " + criteria.getOperation());
                }
        }

        String sortByField = orderCriteriaList.getSortBy();
        String sortDirection = orderCriteriaList.getValueSortOrder().equalsIgnoreCase("asc") ? "ASC" : "DESC";
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortByField);

        Page<Huesped> page = huespedRepository.findAll(spec, PageRequest.of(pageIndex, pageSize, sort));

        List<HuespedDTO> huespedDTOList = page.getContent().stream()
                .map(this::parseDto)
                .collect(Collectors.toList());

        Page<HuespedDTO> huespedDTOPage = new PageImpl<>(huespedDTOList, PageRequest.of(pageIndex, pageSize, sort),
                page.getTotalElements());

        return huespedDTOPage;

    }
    
}
