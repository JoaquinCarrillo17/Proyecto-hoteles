package gz.hoteles.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gz.hoteles.dto.HabitacionDTO;
import gz.hoteles.servicio.impl.ServicioHabitaciones;
import gz.hoteles.support.SearchRequest;

@RestController
@RequestMapping("/habitaciones")
public class HabitacionController extends ControllerDto<HabitacionDTO> {

    @PostMapping("/dynamicFilterOr")
    public ResponseEntity<?> getFilteredByDynamicSearchOr(@RequestBody SearchRequest searchRequest) {

        Page<HabitacionDTO> page = ((ServicioHabitaciones) this.dtoService).getHabitacionesDynamicSearchOr(searchRequest);

        return ResponseEntity.ok(page);
    }

    @PostMapping("/dynamicFilterAnd")
    public ResponseEntity<?> getFilteredByDynamicSearchAnd(@RequestBody SearchRequest searchRequest) {

        Page<HabitacionDTO> page = ((ServicioHabitaciones) this.dtoService).getHabitacionesDynamicSearchAnd(searchRequest);

        return ResponseEntity.ok(page);
    } 


}
