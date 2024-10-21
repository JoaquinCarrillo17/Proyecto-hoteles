package gz.hoteles.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gz.hoteles.entities.Ubicacion;
import gz.hoteles.servicio.ServicioUbicacion;
import gz.hoteles.support.SearchRequest;

@RestController
@RequestMapping("/ubicaciones")
public class UbicacionController {

    @Autowired
    private ServicioUbicacion ubicacionService;

    // Crear una nueva ubicación
    @PostMapping
    public ResponseEntity<Ubicacion> post(@RequestBody Ubicacion ubicacion) {
        Ubicacion nuevaUbicacion = ubicacionService.crearUbicacion(ubicacion);
        return new ResponseEntity<>(nuevaUbicacion, HttpStatus.CREATED);
    }

    // Obtener todas las ubicaciones
    @GetMapping
    public ResponseEntity<List<Ubicacion>> getAll() {
        List<Ubicacion> ubicaciones = ubicacionService.obtenerTodasLasUbicaciones();
        return new ResponseEntity<>(ubicaciones, HttpStatus.OK);
    }

    // Obtener una ubicación por ID
    @GetMapping("/{id}")
    public ResponseEntity<Ubicacion> getById(@PathVariable int id) {
        Ubicacion ubicacion = ubicacionService.obtenerUbicacionPorId(id);
        if (ubicacion != null) {
            return new ResponseEntity<>(ubicacion, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Actualizar una ubicación
    @PutMapping("/{id}")
    public ResponseEntity<Ubicacion> put(@PathVariable int id, @RequestBody Ubicacion detallesUbicacion) {
        Ubicacion ubicacionActualizada = ubicacionService.actualizarUbicacion(id, detallesUbicacion);
        if (ubicacionActualizada != null) {
            return new ResponseEntity<>(ubicacionActualizada, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Eliminar una ubicación
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        boolean eliminado = ubicacionService.eliminarUbicacion(id);
        if (eliminado) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{id}/hoteles")
    public ResponseEntity<?> anadirHoteles(@PathVariable(name = "id") int id, @RequestBody List<Integer> idsHoteles) {
        if (id <= 0  || Integer.valueOf(id) == null) {
            throw new IllegalArgumentException("El ID debe ser un número entero positivo");
        }
        Ubicacion u = ubicacionService.anadirHoteles(id, idsHoteles);
        if (u != null) {
            return new ResponseEntity<>(u, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/dynamicFilterAnd")
    public ResponseEntity<?> getFilteredByDynamicSearchAnd(@RequestBody SearchRequest searchRequest) {
        Page<Ubicacion> page = ubicacionService.filtrarUbicaciones(searchRequest);

        /*List<UbicacionDTO> ubicacionDTOList = page.getContent().stream()
                .map(UbicacionController::convertToDtoUbicacion)
                .collect(Collectors.toList());*/

        Page<Ubicacion> ubicacionDTOPage = new PageImpl<>(page.getContent(), PageRequest.of(
                searchRequest.getPage().getPageIndex(),
                searchRequest.getPage().getPageSize()),
                page.getTotalElements());

        return ResponseEntity.ok(ubicacionDTOPage);
    }
}

