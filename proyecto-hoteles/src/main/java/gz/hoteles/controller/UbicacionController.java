package gz.hoteles.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gz.hoteles.dto.UbicacionDto;
import gz.hoteles.entities.Ubicacion;
import gz.hoteles.servicio.impl.ServicioUbicacion;
import gz.hoteles.support.SearchRequest;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/ubicaciones")
public class UbicacionController extends ControllerDto<UbicacionDto>{

    @PostMapping("/dynamicFilterAnd")
    public ResponseEntity<?> getFilteredByDynamicSearchAnd(@RequestBody SearchRequest searchRequest) {
        Page<Ubicacion> page = ((ServicioUbicacion) this.dtoService).filtrarUbicaciones(searchRequest);

        Page<Ubicacion> ubicacionDTOPage = new PageImpl<>(page.getContent(), PageRequest.of(
                searchRequest.getPage().getPageIndex(),
                searchRequest.getPage().getPageSize()),
                page.getTotalElements());

        return ResponseEntity.ok(ubicacionDTOPage);
    }

    @GetMapping("getActivas")
    public ResponseEntity<?> getUbicacionesActivas() {
        try {
            List<UbicacionDto> ubicaciones = ((ServicioUbicacion) this.dtoService).getUbicacionesActivas();
            if (ubicaciones.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            return ResponseEntity.status(HttpStatus.OK).body(ubicaciones);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        
    }
    
}

