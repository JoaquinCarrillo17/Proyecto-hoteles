package gz.hoteles.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import gz.hoteles.entities.Hotel;
import gz.hoteles.entities.Ubicacion;
import gz.hoteles.repositories.HotelRepository;
import gz.hoteles.repositories.UbicacionRepository;
import gz.hoteles.support.ListOrderCriteria;
import gz.hoteles.support.SearchCriteria;
import gz.hoteles.support.SearchRequest;

@Service
public class ServicioUbicacion {

    @Autowired
    private UbicacionRepository ubicacionRepository;
    @Autowired
    private HotelRepository hotelRepository;

    // Crear una nueva ubicación
    public Ubicacion crearUbicacion(Ubicacion ubicacion) {
        return ubicacionRepository.save(ubicacion);
    }

    // Obtener todas las ubicaciones
    public List<Ubicacion> obtenerTodasLasUbicaciones() {
        return ubicacionRepository.findAll();
    }

    // Obtener una ubicación por ID
    public Ubicacion obtenerUbicacionPorId(int id) {
        return ubicacionRepository.findById(id).orElse(null);
    }

    // Actualizar una ubicación
    public Ubicacion actualizarUbicacion(int id, Ubicacion detallesUbicacion) {
        Ubicacion ubicacion = obtenerUbicacionPorId(id);
        if (ubicacion != null) {
            ubicacion.setCiudad(detallesUbicacion.getCiudad());
            ubicacion.setPais(detallesUbicacion.getPais());
            ubicacion.setContinente(detallesUbicacion.getContinente());
            return ubicacionRepository.save(ubicacion);
        }
        return null;
    }

    // Eliminar una ubicación
    public boolean eliminarUbicacion(int id) {
        Ubicacion ubicacion = obtenerUbicacionPorId(id);
        if (ubicacion != null) {
            ubicacionRepository.delete(ubicacion);
            return true;
        }
        return false;
    }

    public Ubicacion anadirHoteles(int id, List<Integer> idsHoteles) {
        Ubicacion ubicacion = obtenerUbicacionPorId(id);
        if (ubicacion!= null) {
            for (Integer idHotel : idsHoteles) {
                Hotel h = hotelRepository.getById(idHotel);
                h.setUbicacion(ubicacion);
                hotelRepository.save(h);
            }
        }
        return ubicacion;
    }

    public Page<Ubicacion> filtrarUbicaciones(SearchRequest searchRequest) {
        List<SearchCriteria> searchCriteriaList = searchRequest.getListSearchCriteria();
        ListOrderCriteria orderCriteriaList = searchRequest.getListOrderCriteria();
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

}

