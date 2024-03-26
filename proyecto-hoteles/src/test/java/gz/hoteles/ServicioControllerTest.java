package gz.hoteles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import gz.hoteles.controller.ServicioController;
import gz.hoteles.dto.ServicioDTO;
import gz.hoteles.entities.CategoriaServicio;
import gz.hoteles.entities.Servicio;
import gz.hoteles.repositories.ServicioRepository;

@ExtendWith(MockitoExtension.class)
public class ServicioControllerTest {
    
    @Mock
    ServicioRepository  servicioRepository;

    @InjectMocks
    ServicioController servicioController;

    private static final ModelMapper modelMapper = new ModelMapper();

    public static ServicioDTO convertToDtoServicio(Servicio servicio) {
        return modelMapper.map(servicio, ServicioDTO.class);
    }

    public static List<ServicioDTO> convertToDtoServicioList(List<Servicio> servicios) {
        return servicios.stream()
                .map(servicio -> convertToDtoServicio(servicio))
                .collect(Collectors.toList());
    }

    @Test
    void testListWithExistingServicios() {
        List<Servicio> servicios = new ArrayList<>();
        servicios.add(new Servicio(1, "Gimnasio Pepe", "", CategoriaServicio.GIMNASIO));
        servicios.add(new Servicio(2, "Desayuno", "", CategoriaServicio.BAR));

        when(servicioRepository.findAll()).thenReturn(servicios);

        ResponseEntity<?> response = servicioController.list();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(convertToDtoServicioList(servicios), response.getBody());
    }

    @Test
    void testListWithNoServicios() {
        when(servicioRepository.findAll()).thenReturn(Collections.emptyList());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            servicioController.list();
        });

        assertEquals(HttpStatus.NO_CONTENT, exception.getStatusCode());
        assertEquals("No se ha encontrado ningún servicio", exception.getReason());
    }

    @Test
    void testGetWithValidId() {
        Servicio servicio = new Servicio(1, "Gimnasio Pepe", "", CategoriaServicio.GIMNASIO);

        when(servicioRepository.findById(1)).thenReturn(Optional.of(servicio));

        ResponseEntity<?> response = servicioController.get(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(convertToDtoServicio(servicio), response.getBody());
    }

    @Test
    void testGetWithInvalidId() {
        int invalidId = -1;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            servicioController.get(invalidId);
        });

        assertEquals("El ID debe ser un número entero positivo", exception.getMessage());
    }

    @Test
    void testGetWithNonExistingServicio() {
        int nonExistingId = 10;

        when(servicioRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            servicioController.get(nonExistingId);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("No se encontró ningún servicio con el ID proporcionado", exception.getReason());
    }

    @Test
    void testGetServicioByNombreWithValidInput() {
        String nombre = "Gimnasio";
        List<Servicio> servicios = new ArrayList<>();
        servicios.add(new Servicio(1, "Gimnasio Pepe", "", CategoriaServicio.GIMNASIO));
        servicios.add(new Servicio(2, "Gimnasio 24/7", "", CategoriaServicio.GIMNASIO));
        Page<Servicio> page = new PageImpl<>(servicios);

        when(servicioRepository.getServicioByNombre(nombre, PageRequest.of(0, 10))).thenReturn(page);

        ResponseEntity<?> response = servicioController.getServicioByNombre(nombre, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(convertToDtoServicioList(servicios), response.getBody());
    }

    @Test
    void testGetServicioByNombreWithEmptyInput() {
        String nombre = "";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            servicioController.getServicioByNombre(nombre, 10);
        });

        assertEquals("El parámetro 'nombre' no puede estar vacío", exception.getMessage());
    }

    @Test
    void testGetServicioByNombreWithNoContent() {
        String nombre = "Spa";

        Page<Servicio> emptyPage = new PageImpl<>(new ArrayList<>());

        when(servicioRepository.getServicioByNombre(nombre, PageRequest.of(0, 10))).thenReturn(emptyPage);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            servicioController.getServicioByNombre(nombre, 10);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void testGetServicioByCategoriaWithValidInput() {
        CategoriaServicio categoria = CategoriaServicio.GIMNASIO;
        List<Servicio> servicios = new ArrayList<>();
        servicios.add(new Servicio(1, "Gimnasio Pepe", "", categoria));
        servicios.add(new Servicio(2, "Gimnasio 24/7", "", categoria));
        Page<Servicio> page = new PageImpl<>(servicios);

        when(servicioRepository.getServicioByCategoria(categoria, PageRequest.of(0, 10))).thenReturn(page);

        ResponseEntity<?> response = servicioController.getServicioByCategoria(categoria, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(convertToDtoServicioList(servicios), response.getBody());
    }

    @Test
    void testGetServicioByCategoriaWithNullInput() {
        CategoriaServicio categoria = null;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            servicioController.getServicioByCategoria(categoria, 10);
        });

        assertEquals("El parámetro 'categoría' no puede ser nulo", exception.getMessage());
    }

    @Test
    void testGetServicioByCategoriaWithNoContent() {
        CategoriaServicio categoria = CategoriaServicio.LAVANDERIA;

        Page<Servicio> emptyPage = new PageImpl<>(new ArrayList<>());

        when(servicioRepository.getServicioByCategoria(categoria, PageRequest.of(0, 10))).thenReturn(emptyPage);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            servicioController.getServicioByCategoria(categoria, 10);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void testGetServicioByDescripcionWithValidInput() {
        String descripcion = "gimnasio";
        List<Servicio> servicios = new ArrayList<>();
        servicios.add(new Servicio(1, "Gimnasio Pepe", descripcion, CategoriaServicio.GIMNASIO));
        servicios.add(new Servicio(2, "Gimnasio 24/7", descripcion, CategoriaServicio.GIMNASIO));
        Page<Servicio> page = new PageImpl<>(servicios);

        when(servicioRepository.getServicioByDescripcion(descripcion, PageRequest.of(0, 10))).thenReturn(page);

        ResponseEntity<?> response = servicioController.getServicioByDescripcion(descripcion, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(convertToDtoServicioList(servicios), response.getBody());
    }

    @Test
    void testGetServicioByDescripcionWithEmptyInput() {
        String descripcion = "";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            servicioController.getServicioByDescripcion(descripcion, 10);
        });

        assertEquals("El parámetro 'descripción' no puede estar vacío", exception.getMessage());
    }

    @Test
    void testGetServicioByDescripcionWithNoContent() {
        String descripcion = "piscina";

        Page<Servicio> emptyPage = new PageImpl<>(new ArrayList<>());

        when(servicioRepository.getServicioByDescripcion(descripcion, PageRequest.of(0, 10))).thenReturn(emptyPage);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            servicioController.getServicioByDescripcion(descripcion, 10);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void testPutWithValidInput() {
        int id = 1;
        Servicio inputServicio = new Servicio(1, "Gimnasio", "Gimnasio 24/7", CategoriaServicio.GIMNASIO);
        Servicio existingServicio = new Servicio(1, "Gimnasio", "Gimnasio Pepe", CategoriaServicio.GIMNASIO);

        when(servicioRepository.findById(id)).thenReturn(java.util.Optional.of(existingServicio));
        when(servicioRepository.save(existingServicio)).thenReturn(existingServicio);

        ResponseEntity<?> response = servicioController.put(id, inputServicio);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(convertToDtoServicio(existingServicio), response.getBody());
    }

    @Test
    void testPutWithInvalidId() {
        int id = -1;
        Servicio inputServicio = new Servicio(1, "Gimnasio", "Gimnasio 24/7", CategoriaServicio.GIMNASIO);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            servicioController.put(id, inputServicio);
        });

        assertEquals("El ID debe ser un número entero positivo", exception.getMessage());
    }

    @Test
    void testPutWithNonexistentServicio() {
        int id = 1;
        Servicio inputServicio = new Servicio(1, "Gimnasio", "Gimnasio 24/7", CategoriaServicio.GIMNASIO);

        when(servicioRepository.findById(id)).thenReturn(java.util.Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            servicioController.put(id, inputServicio);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("No se encontró ningun servicio por el ID proporcionado", exception.getReason());
    }
    
    @Test
    void testPostWithValidInput() {
        Servicio inputServicio = new Servicio(1, "Gimnasio", "Gimnasio 24/7", CategoriaServicio.GIMNASIO);

        when(servicioRepository.save(any(Servicio.class))).thenReturn(inputServicio);
        ResponseEntity<?> response = servicioController.post(inputServicio);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        ServicioDTO savedServicio = (ServicioDTO) response.getBody();
        assertEquals(convertToDtoServicio(inputServicio), savedServicio);
    }

    @Test
    void testPostWithNullName() {
        Servicio inputServicio = new Servicio(1, null, "Gimnasio 24/7", CategoriaServicio.GIMNASIO);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            servicioController.post(inputServicio);
        });
        assertEquals("Los campos 'nombre' y 'categoria' son obligatorios", exception.getMessage());
    }

    @Test
    void testPostWithEmptyName() {
        Servicio inputServicio = new Servicio(1, "", "Gimnasio 24/7", CategoriaServicio.GIMNASIO);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            servicioController.post(inputServicio);
        });

        assertEquals("Los campos 'nombre' y 'categoria' son obligatorios", exception.getMessage());
    }

    @Test
    void testPostWithNullCategory() {
        Servicio inputServicio = new Servicio(1, "Gimnasio", "Gimnasio 24/7", null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            servicioController.post(inputServicio);
        });

        assertEquals("Los campos 'nombre' y 'categoria' son obligatorios", exception.getMessage());
    }

    @Test
    void testDeleteWithValidId() {
        int id = 1;
        Servicio servicio = new Servicio();
        when(servicioRepository.findById(id)).thenReturn(java.util.Optional.of(servicio));

        ResponseEntity<?> response = servicioController.delete(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(servicioRepository, times(1)).delete(servicio);
    }

    @Test
    void testDeleteWithInvalidId() {
        int id = -1;
        when(servicioRepository.findById(id)).thenReturn(java.util.Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            servicioController.delete(id);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("No se encontró ningun servicio por el ID proporcionado", exception.getReason());
    }

}
