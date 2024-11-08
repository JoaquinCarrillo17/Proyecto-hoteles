package gz.hoteles.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gz.hoteles.dto.ReservasDto;

@RestController
@RequestMapping("/reservas")
public class ReservasController extends ControllerDto<ReservasDto> {
    
}
