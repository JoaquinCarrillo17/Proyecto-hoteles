package gz.hoteles.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gz.hoteles.dto.HotelDTO;
import gz.hoteles.servicio.impl.ServicioHoteles;
import gz.hoteles.support.SearchRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/hoteles")
public class HotelController extends ControllerDto<HotelDTO>{

    private static final Logger logger = LoggerFactory.getLogger(HotelController.class);

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

    @GetMapping("/getHotelByUsuario/{idUsuario}")
    public ResponseEntity<?> getHotelByUsuario(@PathVariable Integer idUsuario) {

        HotelDTO hotelDTO = ((ServicioHoteles) this.dtoService).getHotelByUsuario(idUsuario);

        if (hotelDTO == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            logger.error("NO ENCUENTRO NINGUN HOTEL CON IDUSUARIO = " + idUsuario);
            return ResponseEntity.ok(hotelDTO);
        }
    }
    

}
