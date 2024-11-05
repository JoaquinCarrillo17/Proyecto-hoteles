package gz.hoteles.servicio.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import gz.hoteles.dto.UbicacionDto;
import gz.hoteles.entities.Ubicacion;
import gz.hoteles.repositories.UbicacionRepository;
import gz.hoteles.support.OrderCriteria;
import gz.hoteles.support.SearchCriteria;
import gz.hoteles.support.SearchRequest;

@Service
public class ServicioUbicacion extends DtoServiceImpl<UbicacionDto, Ubicacion>{

    @Autowired
    private UbicacionRepository ubicacionRepository;

    public Page<Ubicacion> filtrarUbicaciones(SearchRequest searchRequest) {
        List<SearchCriteria> searchCriteriaList = searchRequest.getListSearchCriteria();
        OrderCriteria orderCriteriaList = searchRequest.getListOrderCriteria();
        int pageSize = searchRequest.getPage().getPageSize();
        int pageIndex = searchRequest.getPage().getPageIndex();

        Specification<Ubicacion> spec = Specification.where(null);
        for (SearchCriteria criteria : searchCriteriaList) {
            switch (criteria.getOperation()) {
                case "equals":
                    spec = spec.and((root, query, cb) -> cb.equal(root.get(criteria.getKey()), criteria.getValue()));
                    break;
                case "contains":
                    spec = spec.and((root, query, cb) -> cb.like(root.get(criteria.getKey()), "%" + criteria.getValue() + "%"));
                    break;
                default:
                    throw new IllegalArgumentException("Operador de búsqueda no válido: " + criteria.getOperation());
            }
        }

        String sortByField = orderCriteriaList.getSortBy();
        String sortDirection = orderCriteriaList.getValueSortOrder().equalsIgnoreCase("asc") ? "ASC" : "DESC";
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortByField);

        Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);
        return ubicacionRepository.findAll(spec, pageable);
    }

    @Override
    protected UbicacionDto parseDto(Ubicacion entity) {
        return (UbicacionDto) entity.getDto();
    }

    @Override
    protected Ubicacion parseEntity(UbicacionDto dto) throws Exception {
        return (Ubicacion) dto.getEntity();
    }

}

