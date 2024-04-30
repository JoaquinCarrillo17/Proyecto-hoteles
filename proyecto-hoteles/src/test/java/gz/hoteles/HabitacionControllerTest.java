package gz.hoteles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
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

import gz.hoteles.dto.HabitacionDTO;
import gz.hoteles.controller.HabitacionController;
import gz.hoteles.entities.Habitacion;
import gz.hoteles.entities.Huesped;
import gz.hoteles.entities.TipoHabitacion;
import gz.hoteles.repositories.HabitacionRepository;
import gz.hoteles.servicio.ServicioHoteles;

@ExtendWith(MockitoExtension.class)
public class HabitacionControllerTest {

    @Mock
    HabitacionRepository habitacionRepository;

    @InjectMocks
    HabitacionController habitacionController;
    @Mock
    ServicioHoteles servicioHoteles;

    private static final ModelMapper modelMapper = new ModelMapper();

    public static HabitacionDTO convertToDtoHabitacion(Habitacion habitacion) {
        return modelMapper.map(habitacion, HabitacionDTO.class);
    }

    public static List<HabitacionDTO> convertToDtoHabitacionList(List<Habitacion> habitaciones) {
        return habitaciones.stream()
                           .map(habitacion -> convertToDtoHabitacion(habitacion))
                           .collect(Collectors.toList());
    }

    @Test
    public void testListWithRooms() {
        // Simula que hay habitaciones en la base de datos
        List<Habitacion> mockRooms = Arrays.asList(
                new Habitacion(1, "1", TipoHabitacion.DOBLE, 10),
                new Habitacion(2, "2", TipoHabitacion.INDIVIDUAL, 10));
        when(habitacionRepository.findAll()).thenReturn(mockRooms);

        ResponseEntity<?> response = habitacionController.list();

        List<HabitacionDTO> expectedDTOs = convertToDtoHabitacionList(mockRooms);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedDTOs, response.getBody());
    }

    @Test
    public void testListWithNoRooms() {
        // Simula que no hay habitaciones en la base de datos
        when(habitacionRepository.findAll()).thenReturn(Collections.emptyList());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            habitacionController.list();
        });

        assertEquals(HttpStatus.NO_CONTENT, exception.getStatus());
        assertEquals("No se ha encontrado ninguna habitación", exception.getReason());
    }

    @Test
    public void testGetExistingHabitacion() {
        Habitacion habitacion = new Habitacion(1, "1", TipoHabitacion.DOBLE, 10);
        when(habitacionRepository.findById(1)).thenReturn(Optional.of(habitacion));

        ResponseEntity<?> response = habitacionController.get(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(convertToDtoHabitacion(habitacion), response.getBody());
    }

    @Test
    public void testGetNonExistingHabitacion() {
        when(habitacionRepository.findById(1)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            habitacionController.get(1);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("No se encontró ninguna habitación con el ID proporcionado", exception.getReason());
    }

    @Test
    public void testGetWithInvalidId() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            habitacionController.get(-1);
        });

        assertEquals("El ID debe ser un número entero positivo", exception.getMessage());
    }

    @Test
    public void testGetHabitacionesByNumero() {
        List<Habitacion> habitaciones = new ArrayList<>();
        habitaciones.add(new Habitacion(1, "101", TipoHabitacion.DOBLE, 100));
        habitaciones.add(new Habitacion(2, "102", TipoHabitacion.INDIVIDUAL, 80));
        Page<Habitacion> page = new PageImpl<>(habitaciones);
        when(habitacionRepository.getHabitacionesByNumero("101", PageRequest.of(0, 10))).thenReturn(page);

        ResponseEntity<?> response = habitacionController.getHabitacionesByNumero("101", 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(convertToDtoHabitacionList(habitaciones), response.getBody());
    }

    @Test
    public void testGetHabitacionesByNumeroNoContent() {
        List<Habitacion> habitaciones = new ArrayList<>();
        Page<Habitacion> page = new PageImpl<>(habitaciones);
        when(habitacionRepository.getHabitacionesByNumero("101", PageRequest.of(0, 10))).thenReturn(page);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            habitacionController.getHabitacionesByNumero("101", 10);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("No se encontró ninguna habitación con el número '101'", exception.getReason());
    }

    @Test
    public void testGetHabitacionesByNumeroEmpty() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            habitacionController.getHabitacionesByNumero("", 10);
        });

        assertEquals("El parámetro 'número' no puede estar vacío", exception.getMessage());
    }

    @Test
    public void testGetHabitacionesByNumeroNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            habitacionController.getHabitacionesByNumero(null, 10);
        });

        assertEquals("El parámetro 'número' no puede estar vacío", exception.getMessage());
    }

    @Test
    public void testGetHabitacionesByNumeroInvalidPages() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            habitacionController.getHabitacionesByNumero("101", -1);
        });

        assertEquals("Page size must not be less than one", exception.getMessage());
    }

    @Test
    public void testGetHabitacionesByTipoHabitacion() {
        List<Habitacion> habitaciones = new ArrayList<>();
        habitaciones.add(new Habitacion(1, "101", TipoHabitacion.DOBLE, 100));
        habitaciones.add(new Habitacion(2, "102", TipoHabitacion.INDIVIDUAL, 80));
        Page<Habitacion> page = new PageImpl<>(habitaciones);
        when(habitacionRepository.getHabitacionesByTipoHabitacion(TipoHabitacion.DOBLE, PageRequest.of(0, 10)))
                .thenReturn(page);

        ResponseEntity<?> responseEntity = habitacionController.getHabitacionesByTipoHabitacion(TipoHabitacion.DOBLE,
                10);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        List<HabitacionDTO> result = (List<HabitacionDTO>) responseEntity.getBody();
        assertNotNull(result);
        assertEquals(convertToDtoHabitacionList(habitaciones), result);
    }

    @Test
    public void testGetHabitacionesByTipoHabitacionNullTipo() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            habitacionController.getHabitacionesByTipoHabitacion(null, 10);
        });

        assertEquals("El parámetro 'tipo' no puede ser nulo", exception.getMessage());
    }

    @Test
    public void testGetHabitacionesByTipoHabitacionNoContent() {
        List<Habitacion> habitaciones = new ArrayList<>();
        Page<Habitacion> page = new PageImpl<>(habitaciones);
        when(habitacionRepository.getHabitacionesByTipoHabitacion(TipoHabitacion.DOBLE, PageRequest.of(0, 10)))
                .thenReturn(page);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            habitacionController.getHabitacionesByTipoHabitacion(TipoHabitacion.DOBLE, 10);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("No se encontró ninguna habitación del tipo 'DOBLE'", exception.getReason());
    }

    @Test
    public void testGetHabitacionesByPrecioPorNoche() {
        List<Habitacion> habitaciones = new ArrayList<>();
        habitaciones.add(new Habitacion(1, "101", TipoHabitacion.DOBLE, 100));
        habitaciones.add(new Habitacion(2, "102", TipoHabitacion.INDIVIDUAL, 80));
        Page<Habitacion> page = new PageImpl<>(habitaciones);
        when(habitacionRepository.getHabitacionesByPrecioPorNoche("100", PageRequest.of(0, 10)))
                .thenReturn(page);

        ResponseEntity<?> responseEntity = habitacionController.getHabitacionesByPrecioPorNoche("100", 10);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        List<HabitacionDTO> result = (List<HabitacionDTO>) responseEntity.getBody();
        assertNotNull(result);
        assertEquals(convertToDtoHabitacionList(habitaciones), result);
    }

    @Test
    public void testGetHabitacionesByPrecioPorNocheNullPrecio() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            habitacionController.getHabitacionesByPrecioPorNoche(null, 10);
        });

        assertEquals("El parámetro 'precio' no puede ser nulo.", exception.getMessage());
    }

    @Test
    public void testGetHabitacionesByPrecioPorNocheEmptyPrecio() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            habitacionController.getHabitacionesByPrecioPorNoche("", 10);
        });

        assertEquals("El parámetro 'precio' no puede ser nulo.", exception.getMessage());
    }

    @Test
    public void testGetHabitacionesByPrecioPorNocheNoContent() {
        List<Habitacion> habitaciones = new ArrayList<>();
        Page<Habitacion> page = new PageImpl<>(habitaciones);
        when(habitacionRepository.getHabitacionesByPrecioPorNoche("100", PageRequest.of(0, 10)))
                .thenReturn(page);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            habitacionController.getHabitacionesByPrecioPorNoche("100", 10);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("No se encontró ninguna habitación con precio '" + 100 + "€'", exception.getReason());
    }

    @Test
    void testPutExistingHabitacion() {
        int id = 1;
        Habitacion inputHabitacion = new Habitacion(1, "101", TipoHabitacion.DOBLE, 100);
        Habitacion existingHabitacion = new Habitacion(1, "101", TipoHabitacion.DOBLE, 100);

        when(habitacionRepository.findById(id)).thenReturn(Optional.of(existingHabitacion));
        when(habitacionRepository.save(existingHabitacion)).thenReturn(existingHabitacion);

        ResponseEntity<?> response = habitacionController.put(id, inputHabitacion);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        HabitacionDTO savedHabitacion = (HabitacionDTO) response.getBody();
        assertEquals(inputHabitacion.getId(), savedHabitacion.getId());
    }

    @Test
    void testPutNonExistingHabitacion() {
        int id = 1;
        Habitacion inputHabitacion = new Habitacion(1, "101", TipoHabitacion.DOBLE, 100);

        when(habitacionRepository.findById(id)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> habitacionController.put(id, inputHabitacion));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("No se encontró ninguna habitación por el ID proporcionado", exception.getReason());
    }

    @Test
    void testPutWithInvalidId() {
        int id = -1;
        Habitacion inputHabitacion = new Habitacion(1, "101", TipoHabitacion.DOBLE, 100);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> habitacionController.put(id, inputHabitacion));

        assertEquals("El ID debe ser un número entero positivo", exception.getMessage());
    }

    @Test
    void testPostWithValidInput() {
        Habitacion inputHabitacion = new Habitacion(1, "101", TipoHabitacion.DOBLE, 100);
        Habitacion savedHabitacion = new Habitacion(1, "101", TipoHabitacion.DOBLE, 100);

        when(servicioHoteles.crearHabitacion(inputHabitacion)).thenReturn(savedHabitacion);

        ResponseEntity<?> response = habitacionController.post(inputHabitacion);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        HabitacionDTO returnedHabitacion = (HabitacionDTO) response.getBody();
        assertEquals(convertToDtoHabitacion(savedHabitacion), returnedHabitacion);
    }

    @Test
    void testPostWithNullTipoHabitacion() {
        Habitacion inputHabitacion = new Habitacion(1, "101", null, 100);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> habitacionController.post(inputHabitacion));

        assertEquals("Debes introducir el tipo de habitación", exception.getMessage());
    }

    @Test
    void testAnadirHuespedWithValidInput() {
        int habitacionId = 1;
        Huesped huesped = new Huesped(1, "Nombre", "12345678A", "correo@ejemplo.com");
        Habitacion habitacion = new Habitacion(1, "101", TipoHabitacion.DOBLE, 100);

        when(servicioHoteles.anadirHuesped(habitacionId, huesped)).thenReturn(habitacion);

        ResponseEntity<?> response = habitacionController.anadirHuesped(habitacionId, huesped);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        HabitacionDTO returnedHabitacion = (HabitacionDTO) response.getBody();
        assertEquals(convertToDtoHabitacion(habitacion), returnedHabitacion);
    }

    @Test
    void testAnadirHuespedWithInvalidId() {
        int invalidId = -1;
        Huesped huesped = new Huesped(1, "Nombre", "12345678A", "correo@ejemplo.com");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            habitacionController.anadirHuesped(invalidId, huesped);
        });

        assertEquals("El ID debe ser un número entero positivo", exception.getMessage());
    }

    @Test
    void testAnadirHuespedWithNotFoundHabitacion() {
        int habitacionId = 1;
        Huesped huesped = new Huesped(1, "Nombre", "12345678A", "correo@ejemplo.com");

        when(servicioHoteles.anadirHuesped(habitacionId, huesped))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No se encontró ninguna habitacion por el ID proporcionado"));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            habitacionController.anadirHuesped(habitacionId, huesped);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("No se encontró ninguna habitacion por el ID proporcionado", exception.getReason());
    }

    @Test
    void testDeleteExistingHabitacion() {
        int habitacionId = 1;
        Habitacion habitacion = new Habitacion(habitacionId, "101", TipoHabitacion.DOBLE, 100);

        when(habitacionRepository.findById(habitacionId)).thenReturn(Optional.of(habitacion));

        ResponseEntity<?> response = habitacionController.delete(habitacionId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(habitacionRepository, times(1)).delete(habitacion);
    }

    @Test
    void testDeleteNonExistingHabitacion() {
        int habitacionId = 1;

        when(habitacionRepository.findById(habitacionId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            habitacionController.delete(habitacionId);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("No se encontró ninguna habitacion por el ID proporcionado", exception.getReason());
        verify(habitacionRepository, never()).delete(any());
    }

}