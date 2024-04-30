package gz.hoteles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
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
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import gz.hoteles.controller.HuespedController;
import gz.hoteles.dto.HuespedDTO;
import gz.hoteles.entities.Huesped;
import gz.hoteles.repositories.HuespedRepository;

@ExtendWith(MockitoExtension.class)
public class HuespedControllerTest {
    @Mock
    HuespedRepository huespedRepository;

    @InjectMocks
    HuespedController huespedController;

    private static final ModelMapper modelMapper = new ModelMapper();

    public static HuespedDTO convertToDtoHuesped(Huesped huesped) {
        return modelMapper.map(huesped, HuespedDTO.class);
    }

    public static List<HuespedDTO> convertToDtoHuespedList(List<Huesped> huespedes) {
        return huespedes.stream()
                .map(huesped -> convertToDtoHuesped(huesped))
                .collect(Collectors.toList());
    }

    @Test
    public void testList() {
        List<Huesped> mockHuespedList = new ArrayList<>();
        mockHuespedList.add(new Huesped(1, "Nombre", "12345678A", "correo@ejemplo.com"));
        mockHuespedList.add(new Huesped(2, "Otro nombre", "87654321B", "otrocorreo@ejemplo.com"));
        when(huespedRepository.findAll()).thenReturn(mockHuespedList);

        ResponseEntity<?> responseEntity = huespedController.list();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        List<HuespedDTO> result = (List<HuespedDTO>) responseEntity.getBody();

        assertEquals(2, result.size());
    }

    @Test
    public void testGetWithValidId() {
        int validId = 1;
        Huesped mockHuesped = new Huesped(validId, "Nombre", "12345678A", "correo@ejemplo.com");

        when(huespedRepository.findById(validId)).thenReturn(Optional.of(mockHuesped));
        ResponseEntity<?> responseEntity = huespedController.get(validId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(convertToDtoHuesped(mockHuesped), responseEntity.getBody());
    }

    @Test
    public void testGetWithValidIdButNoHuespedFound() {
        int validId = 1;

        // Configuración del comportamiento del mock del repositorio
        when(huespedRepository.findById(validId)).thenReturn(Optional.empty());

        // Verificar que se lance una excepción cuando no se encuentra ningún huésped con el ID proporcionado
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            huespedController.get(validId);
        });

        // Verificar el estado de la excepción
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("No se encontró ningún huésped con el ID proporcionado", exception.getReason());
    }

    @Test
    public void testGetWithInvalidId() {
        int invalidId = -1;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            huespedController.get(invalidId);
        });
    
        assertEquals("El ID debe ser un número entero positivo", exception.getMessage());
    }

    @Test
    public void testGetHuespedesByNombreWithValidData() {
        String nombre = "Juan";
        int pages = 1;
        List<Huesped> mockHuespedList = new ArrayList<>();
        mockHuespedList.add(new Huesped(1, "Juan Pérez", "12345678A", "juan@example.com"));
        mockHuespedList.add(new Huesped(2, "Juan Rodríguez", "87654321B", "juan@example.com"));
        Page<Huesped> mockPage = new PageImpl<>(mockHuespedList);
        Pageable pageable = PageRequest.of(0, pages);

        when(huespedRepository.getHuespedesByNombre(nombre, pageable)).thenReturn(mockPage);

        ResponseEntity<?> response = huespedController.getHuespedesByNombre(nombre, pages);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Huesped> result = (List<Huesped>) response.getBody();
        assertEquals(2, result.size());
    }

    @Test
    public void testGetHuespedesByNombreWithEmptyNombre() {
        String nombre = "";
        int pages = 1;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            huespedController.getHuespedesByNombre(nombre, pages);
        });

        assertEquals("El parámetro 'nombre' no puede estar vacío", exception.getMessage());
    }

    @Test
    public void testGetHuespedesByNombreWithNoContent() {
        String nombre = "Pedro";
        int pages = 1;
        List<Huesped> mockHuespedList = new ArrayList<>();
        Page<Huesped> mockPage = new PageImpl<>(mockHuespedList);
        Pageable pageable = PageRequest.of(0, pages);

        when(huespedRepository.getHuespedesByNombre(nombre, pageable)).thenReturn(mockPage);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            huespedController.getHuespedesByNombre(nombre, pages);
        });

        assertEquals(HttpStatus.NO_CONTENT, exception.getStatus());
        assertEquals("No se encontró ningún huésped con nombre '" + nombre + "'", exception.getReason());
    }

    @Test
    public void testGetHuespedesByDniWithValidData() {
        String dni = "12345678A";
        int pages = 1;
        List<Huesped> mockHuespedList = new ArrayList<>();
        mockHuespedList.add(new Huesped(1, "Juan Pérez", dni, "juan@example.com"));
        Page<Huesped> mockPage = new PageImpl<>(mockHuespedList);
        Pageable pageable = PageRequest.of(0, pages);

        when(huespedRepository.getHuespedesByDni(dni, pageable)).thenReturn(mockPage);

        ResponseEntity<?> response = huespedController.getHuespedesByDni(dni, pages);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<HuespedDTO> result = (List<HuespedDTO>) response.getBody();
        assertEquals(1, result.size());
        assertEquals(dni, result.get(0).getDni());
    }

    @Test
    public void testGetHuespedesByDniWithEmptyDni() {
        String dni = "";
        int pages = 1;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            huespedController.getHuespedesByDni(dni, pages);
        });

        assertEquals("El parámetro 'dni' no puede estar vacío", exception.getMessage());
    }

    @Test
    public void testGetHuespedesByDniWithNoContent() {
        String dni = "00000000X";
        int pages = 1;
        List<Huesped> mockHuespedList = new ArrayList<>();
        Page<Huesped> mockPage = new PageImpl<>(mockHuespedList);
        Pageable pageable = PageRequest.of(0, pages);

        when(huespedRepository.getHuespedesByDni(dni, pageable)).thenReturn(mockPage);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            huespedController.getHuespedesByDni(dni, pages);
        });

        assertEquals(HttpStatus.NO_CONTENT, exception.getStatus());
        assertEquals("No se encontró ningún huésped con dni '" + dni + "'", exception.getReason());
    }

    @Test
    public void testGetHuespedesByEmailWithValidData() {
        String email = "test@example.com";
        int pages = 1;
        List<Huesped> mockHuespedList = new ArrayList<>();
        mockHuespedList.add(new Huesped(1, "Juan Pérez", "12345678A", email));
        Page<Huesped> mockPage = new PageImpl<>(mockHuespedList);
        Pageable pageable = PageRequest.of(0, pages);

        when(huespedRepository.getHuespedesByEmail(email, pageable)).thenReturn(mockPage);

        ResponseEntity<?> response = huespedController.getHuespedesByEmail(email, pages);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<HuespedDTO> result = (List<HuespedDTO>) response.getBody();
        assertEquals(1, result.size());
        assertEquals(email, result.get(0).getEmail());
    }

    @Test
    public void testGetHuespedesByEmailWithEmptyEmail() {
        String email = "";
        int pages = 1;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            huespedController.getHuespedesByEmail(email, pages);
        });

        assertEquals("El parámetro 'email' no puede estar vacío", exception.getMessage());
    }

    @Test
    public void testGetHuespedesByEmailWithNoContent() {
        String email = "nonexistent@example.com";
        int pages = 1;
        List<Huesped> mockHuespedList = new ArrayList<>();
        Page<Huesped> mockPage = new PageImpl<>(mockHuespedList);
        Pageable pageable = PageRequest.of(0, pages);

        when(huespedRepository.getHuespedesByEmail(email, pageable)).thenReturn(mockPage);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            huespedController.getHuespedesByEmail(email, pages);
        });

        assertEquals(HttpStatus.NO_CONTENT, exception.getStatus());
        assertEquals("No se encontró ningún huésped con email '" + email + "'", exception.getReason());
    }

    @Test
    public void testGetHuespedesByFechaEntradaWithValidData() {
        Date fecha = new Date();
        int pages = 1;
        List<Huesped> mockHuespedList = new ArrayList<>();
        mockHuespedList.add(new Huesped(1, "Juan Pérez", "12345678A", "juan@example.com", fecha, null));
        Page<Huesped> mockPage = new PageImpl<>(mockHuespedList);
        Pageable pageable = PageRequest.of(0, pages);

        when(huespedRepository.getHuespedesByFechaEntrada(fecha, pageable)).thenReturn(mockPage);

        ResponseEntity<?> response = huespedController.getHuespedesByFechaEntrada(fecha, pages);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<HuespedDTO> result = (List<HuespedDTO>) response.getBody();
        assertEquals(1, result.size());
        assertEquals(fecha, result.get(0).getFechaCheckIn());
    }

    @Test
    public void testGetHuespedesByFechaEntradaWithNullDate() {
        Date fecha = null;
        int pages = 1;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            huespedController.getHuespedesByFechaEntrada(fecha, pages);
        });

        assertEquals("El parámetro 'fecha' no puede estar vacío", exception.getMessage());
    }

    @Test
    public void testGetHuespedesByFechaEntradaWithNoContent() {
        Date fecha = new Date();
        int pages = 1;
        List<Huesped> mockHuespedList = new ArrayList<>();
        Page<Huesped> mockPage = new PageImpl<>(mockHuespedList);
        Pageable pageable = PageRequest.of(0, pages);

        when(huespedRepository.getHuespedesByFechaEntrada(fecha, pageable)).thenReturn(mockPage);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            huespedController.getHuespedesByFechaEntrada(fecha, pages);
        });

        assertEquals(HttpStatus.NO_CONTENT, exception.getStatus());
        assertEquals("No se encontró ningún huésped con fecha de entrada '" + fecha + "'", exception.getReason());
    }

    @Test
    public void testGetHuespedesByFechaSalidaWithValidData() {
        Date fecha = new Date();
        int pages = 1;
        List<Huesped> mockHuespedList = new ArrayList<>();
        mockHuespedList.add(new Huesped(1, "Juan", "12345678A", "juan@example.com", fecha, fecha));
        Page<Huesped> mockPage = new PageImpl<>(mockHuespedList);
        Pageable pageable = PageRequest.of(0, pages);

        when(huespedRepository.getHuespedesByFechaSalida(fecha, pageable)).thenReturn(mockPage);

        ResponseEntity<?> response = huespedController.getHuespedesByFechaSalida(fecha, pages);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<HuespedDTO> result = (List<HuespedDTO>) response.getBody();
        assertEquals(1, result.size());
        assertEquals(fecha, result.get(0).getFechaCheckOut());
    }

    @Test
    public void testGetHuespedesByFechaSalidaWithNullFecha() {
        Date fecha = null;
        int pages = 1;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            huespedController.getHuespedesByFechaSalida(fecha, pages);
        });

        assertEquals("El parámetro 'fecha' no puede estar vacío", exception.getMessage());
    }

    @Test
    public void testGetHuespedesByFechaSalidaWithNoContent() {
        Date fecha = new Date();
        int pages = 1;
        List<Huesped> mockHuespedList = new ArrayList<>();
        Page<Huesped> mockPage = new PageImpl<>(mockHuespedList);
        Pageable pageable = PageRequest.of(0, pages);

        when(huespedRepository.getHuespedesByFechaSalida(fecha, pageable)).thenReturn(mockPage);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            huespedController.getHuespedesByFechaSalida(fecha, pages);
        });

        assertEquals(HttpStatus.NO_CONTENT, exception.getStatus());
        assertEquals("No se encontró ningún huésped con fecha de salida '" + fecha + "'", exception.getReason());
    }

    @Test
    public void testPutWithValidId() {
        int id = 1;
        Huesped existingHuesped = new Huesped(id, "Juan", "12345678A", "juan@example.com");
        Huesped updatedHuesped = new Huesped(id, "Pedro", "87654321B", "pedro@example.com");

        when(huespedRepository.findById(id)).thenReturn(Optional.of(existingHuesped));
        when(huespedRepository.save(existingHuesped)).thenReturn(updatedHuesped);

        ResponseEntity<?> response = huespedController.put(id, updatedHuesped);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(convertToDtoHuesped(updatedHuesped), response.getBody());
    }

    @Test
    public void testPutWithInvalidId() {
        int id = -1;
        Huesped updatedHuesped = new Huesped(1, "Pedro", "87654321B", "pedro@example.com");

        when(huespedRepository.findById(id)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            huespedController.put(id, updatedHuesped);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("No se encontró ningún huésped con el ID proporcionado", exception.getReason());
    }

    @Test
    public void testPostWithValidInput() {
        Huesped inputHuesped = new Huesped(1, "Juan", "12345678A", "juan@example.com");
        Huesped savedHuesped = new Huesped(1, "Juan", "12345678A", "juan@example.com");

        when(huespedRepository.save(inputHuesped)).thenReturn(savedHuesped);

        ResponseEntity<?> response = huespedController.post(inputHuesped);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(convertToDtoHuesped(savedHuesped), response.getBody());
    }

    @Test
    public void testPostWithEmptyName() {
        Huesped inputHuesped = new Huesped(1, "", "12345678A", "juan@example.com");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            huespedController.post(inputHuesped);
        });

        assertEquals("El nombre y el dni son obligatorios", exception.getMessage());
    }

    /*@Test
    public void testPostWithNullName() {
        Huesped inputHuesped = new Huesped(1, null, "12345678A", "juan@example.com");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            huespedController.post(inputHuesped);
        });

        assertEquals("El nombre y el dni son obligatorios", exception.getMessage());
    }*/


    @Test
    public void testPostWithEmptyDni() {
        Huesped inputHuesped = new Huesped(1, "Juan", "", "juan@example.com");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            huespedController.post(inputHuesped);
        });

        assertEquals("El nombre y el dni son obligatorios", exception.getMessage());
    }

    @Test
    public void testPostWithNullDni() {
        Huesped inputHuesped = new Huesped(1, "Juan", null, "juan@example.com");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            huespedController.post(inputHuesped);
        });

        assertEquals("El nombre y el dni son obligatorios", exception.getMessage());
    }

    @Test
    public void testDeleteWithValidId() {
        int id = 1;
        Huesped foundHuesped = new Huesped(id, "Nombre", "12345678A", "correo@ejemplo.com");

        when(huespedRepository.findById(id)).thenReturn(Optional.of(foundHuesped));

        ResponseEntity<?> response = huespedController.delete(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(huespedRepository).delete(foundHuesped);
    }

    @Test
    public void testDeleteWithInvalidId() {
        int invalidId = -1;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            huespedController.delete(invalidId);
        });

        assertEquals("El ID debe ser un número entero positivo", exception.getMessage());
    }

    @Test
    public void testDeleteWithNonExistingId() {
        int nonExistingId = 999;

        when(huespedRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            huespedController.delete(nonExistingId);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("No se encontró ningún huésped con el ID proporcionado", exception.getReason());
    }


}
