package gz.hoteles.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gz.hoteles.dto.HuespedDTO;


@RestController
@RequestMapping("/huespedes")
public class HuespedController extends ControllerDto<HuespedDTO> {

    Logger log = LoggerFactory.getLogger(HuespedController.class);


    /*@PostMapping("/dynamicFilterAnd")
    public ResponseEntity<?> getFilteredByDynamicSearchAnd(@RequestBody SearchRequest searchRequest) {

        List<SearchCriteria> searchCriteriaList = searchRequest.getListSearchCriteria();
        OrderCriteria orderCriteriaList = searchRequest.getListOrderCriteria();
        int pageSize = searchRequest.getPage().getPageSize();
        int pageIndex = searchRequest.getPage().getPageIndex();

        Specification<Huesped> spec = Specification.where(null);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        for (SearchCriteria criteria : searchCriteriaList) {
            Date date;
            if (criteria.getKey().equals("fechaCheckIn") || criteria.getKey().equals("fechaCheckOut")) {
                try {
                    date = dateFormat.parse(criteria.getValue());
                } catch (ParseException e) {
                    throw new IllegalArgumentException("Error al parsear la fecha: " + e.getMessage());
                }

                switch (criteria.getOperation()) {
                    case "equals":
                        spec = spec.and((root, query, cb) -> cb.equal(root.get(criteria.getKey()),
                                date));
                        break;
                    case "contains":
                        spec = spec.and((root, query, cb) -> cb.like(root.get(criteria.getKey()),
                                "%" + date + "%"));
                        break;
                    default:
                        throw new IllegalArgumentException(
                                "Operador de búsqueda no válido: " + criteria.getOperation());
                }
            } else {
                switch (criteria.getOperation()) {
                    case "equals":
                        spec = spec
                                .and((root, query, cb) -> cb.equal(root.get(criteria.getKey()), criteria.getValue()));
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
        }

        String sortByField = orderCriteriaList.getSortBy();
        String sortDirection = orderCriteriaList.getValueSortOrder().equalsIgnoreCase("asc") ? "ASC" : "DESC";
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortByField);

        Page<Huesped> page = huespedRepository.findAll(spec, PageRequest.of(pageIndex, pageSize, sort));

        List<HuespedDTO> huespedDTOList = page.getContent().stream()
                .map(HuespedController::convertToDtoHuesped)
                .collect(Collectors.toList());

        Page<HuespedDTO> huespedDTOPage = new PageImpl<>(huespedDTOList, PageRequest.of(pageIndex, pageSize, sort),
                page.getTotalElements());

        return ResponseEntity.ok(huespedDTOPage);
    }

    @PostMapping("/dynamicFilterOr")
    public ResponseEntity<?> getFilteredByDynamicSearchOr(@RequestBody SearchRequest searchRequest) {

        List<SearchCriteria> searchCriteriaList = searchRequest.getListSearchCriteria();
        OrderCriteria orderCriteriaList = searchRequest.getListOrderCriteria();
        int pageSize = searchRequest.getPage().getPageSize();
        int pageIndex = searchRequest.getPage().getPageIndex();

        Specification<Huesped> spec = Specification.where(null);
        // Usamos este formato para recibir la fecha en formato dd-MM-yyyy
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        // Este formato es para convertir la fecha al formato yyyy-MM-dd
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        if (searchCriteriaList != null && !searchCriteriaList.isEmpty()) {

            String value = searchCriteriaList.get(0).getValue(); // Obtenemos el valor del primer criterio de búsqueda
            Date parsedDate = null;
    
            // Intentamos parsear el valor que viene en formato dd-MM-yyyy a una fecha válida
            try {
                parsedDate = inputDateFormat.parse(value);
            } catch (ParseException e) {
                parsedDate = null;  // Si no es una fecha válida, continuamos el flujo normal
            }
    
            // Si el valor es una fecha válida, realizamos la consulta nativa
            if (parsedDate != null) {
                // Convertimos la fecha al formato yyyy-MM-dd
                String formattedDate = outputDateFormat.format(parsedDate);
                Pageable pageable = PageRequest.of(pageIndex, pageSize);
                Page<Huesped> huespedesPage = huespedRepository.findHuespedesByDateWithPagination(formattedDate, pageable);
    
                // Convertimos los resultados a DTO
                List<HuespedDTO> huespedDTOList = huespedesPage.getContent().stream()
                        .map(HuespedController::convertToDtoHuesped)
                        .collect(Collectors.toList());
                
                Page<HuespedDTO> huespedDTOPage = new PageImpl<>(huespedDTOList, PageRequest.of(pageIndex, pageSize), huespedesPage.getTotalElements());
                
                return ResponseEntity.ok(huespedDTOPage);
            }
        }

        for (SearchCriteria criteria : searchCriteriaList) {            
            if (criteria.getKey().startsWith("habitacion.")) {
                switch (criteria.getKey()) {
                    case "habitacion.numero":
                        spec = spec.or((root, query, cb) -> cb.like(root.get("habitacion").get("numero"),
                                "%" + criteria.getValue() + "%"));
                        break;
                    case "habitacion.hotel.nombre":
                        spec = spec.or((root, query, cb) -> cb.like(root.get("habitacion").get("hotel").get("nombre"),
                                "%" + criteria.getValue() + "%"));
                        break;
                    case "habitacion.hotel.idUsuario":
                        spec = spec.or((root, query, cb) -> cb
                                .equal(root.get("habitacion").get("hotel").get("idUsuario"), criteria.getValue()));
                        break;
                    default:
                        throw new IllegalArgumentException("Clave de búsqueda no válida: " + criteria.getKey());
                }
            } else {
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
        }

        String sortByField = orderCriteriaList.getSortBy();
        String sortDirection = orderCriteriaList.getValueSortOrder().equalsIgnoreCase("asc") ? "ASC" : "DESC";
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortByField);

        Page<Huesped> page = huespedRepository.findAll(spec, PageRequest.of(pageIndex, pageSize, sort));

        List<HuespedDTO> huespedDTOList = page.getContent().stream()
                .map(HuespedController::convertToDtoHuesped)
                .collect(Collectors.toList());

        Page<HuespedDTO> huespedDTOPage = new PageImpl<>(huespedDTOList, PageRequest.of(pageIndex, pageSize, sort),
                page.getTotalElements());

        return ResponseEntity.ok(huespedDTOPage);
    }*/

}
