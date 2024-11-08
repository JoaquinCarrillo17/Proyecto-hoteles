package gz.hoteles.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gz.hoteles.dto.HotelDTO;
import gz.hoteles.servicio.impl.ServicioHoteles;
import gz.hoteles.support.SearchRequest;

@RestController
@RequestMapping("/hoteles")
public class HotelController extends ControllerDto<HotelDTO>{

    @PostMapping("/dynamicFilterAnd")
    public ResponseEntity<?> getFilteredByDynamicSearchAnd(@RequestBody SearchRequest searchRequest) {

        Page<HotelDTO> page = ((ServicioHoteles) this.dtoService).getHotelesDynamicSearchAnd(searchRequest);

        return ResponseEntity.ok(page);
    }

    @PostMapping("/dynamicFilterOr")
    public ResponseEntity<?> getFilteredByDynamicSearchOr(@RequestBody SearchRequest searchRequest) {

        Page<HotelDTO> page = ((ServicioHoteles) this.dtoService).getHotelesDynamicSearchOr(searchRequest);

        return ResponseEntity.ok(page);
    }


}
