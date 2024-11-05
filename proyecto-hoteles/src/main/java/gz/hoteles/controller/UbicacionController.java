package gz.hoteles.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gz.hoteles.dto.UbicacionDto;
import gz.hoteles.entities.Ubicacion;
import gz.hoteles.servicio.impl.ServicioUbicacion;
import gz.hoteles.support.SearchRequest;

@RestController
@RequestMapping("/ubicaciones")
public class UbicacionController extends ControllerDto<UbicacionDto>{

    @Autowired
    private ServicioUbicacion ubicacionService;

    @PostMapping("/dynamicFilterAnd")
    public ResponseEntity<?> getFilteredByDynamicSearchAnd(@RequestBody SearchRequest searchRequest) {
        Page<Ubicacion> page = ubicacionService.filtrarUbicaciones(searchRequest);

        Page<Ubicacion> ubicacionDTOPage = new PageImpl<>(page.getContent(), PageRequest.of(
                searchRequest.getPage().getPageIndex(),
                searchRequest.getPage().getPageSize()),
                page.getTotalElements());

        return ResponseEntity.ok(ubicacionDTOPage);
    }
}

