package gz.hoteles.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gz.hoteles.dto.EstadisticasDto;
import gz.hoteles.repositories.HabitacionRepository;
import gz.hoteles.repositories.HotelRepository;
import gz.hoteles.repositories.HuespedRepository;
import gz.hoteles.servicio.impl.ReservasServiceImpl;

import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping("/historicos")
public class HistoricoController {

    @Autowired
    HotelRepository hotelRepository;
    
    @Autowired
    HotelController hotelController;

    @Autowired
    HabitacionRepository habitacionRepository;

    @Autowired
    HuespedRepository huespedRepository;

    @Autowired
    ReservasServiceImpl reservasServiceImpl;

    @GetMapping("/getHistorico")
    public ResponseEntity<?> getHistorico(@RequestParam(required = false) Long hotelId, @RequestParam int year) {
        EstadisticasDto estadisticas = reservasServiceImpl.getEstadisticasByUsuarioAndYear(hotelId, year);
        return ResponseEntity.ok(estadisticas);
    }
    
}
