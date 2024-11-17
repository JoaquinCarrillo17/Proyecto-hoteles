package gz.hoteles.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gz.hoteles.dto.ReservasDto;
import gz.hoteles.servicio.impl.ReservasServiceImpl;
import gz.hoteles.support.SearchRequest;

@RestController
@RequestMapping("/reservas")
public class ReservasController extends ControllerDto<ReservasDto> {

    @PostMapping("/dynamicFilterOr")
    public ResponseEntity<?> getFilteredByDynamicSearchOr(@RequestBody SearchRequest searchRequest) {

        Page<ReservasDto> page = ((ReservasServiceImpl) this.dtoService).getReservasDynamicSearchOr(searchRequest);

        return ResponseEntity.ok(page);
    }
    
}
