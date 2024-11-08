package gz.hoteles.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gz.hoteles.dto.HuespedDTO;
import gz.hoteles.servicio.impl.ServicioHuespedes;
import gz.hoteles.support.SearchRequest;


@RestController
@RequestMapping("/huespedes")
public class HuespedController extends ControllerDto<HuespedDTO> {

    Logger log = LoggerFactory.getLogger(HuespedController.class);


    @PostMapping("/dynamicFilterAnd")
    public ResponseEntity<?> getFilteredByDynamicSearchAnd(@RequestBody SearchRequest searchRequest) {

        Page<HuespedDTO> page = ((ServicioHuespedes) this.dtoService).getHuespedesDynamicSearchAnd(searchRequest);

        return ResponseEntity.ok(page);
    }

    @PostMapping("/dynamicFilterOr")
    public ResponseEntity<?> getFilteredByDynamicSearchOr(@RequestBody SearchRequest searchRequest) {

        Page<HuespedDTO> page = ((ServicioHuespedes) this.dtoService).getHuespedesDynamicSearchOr(searchRequest);

        return ResponseEntity.ok(page);
    }

}
