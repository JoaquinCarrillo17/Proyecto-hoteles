package gz.hoteles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import gz.hoteles.controller.HotelController;
import gz.hoteles.entities.Hotel;
import gz.hoteles.repositories.HotelRepository;
import gz.hoteles.servicio.ServicioHoteles;

@ExtendWith(MockitoExtension.class)
public class HotelControllerTest {
    
    @Mock
    HotelRepository hotelRepository;

    @InjectMocks
    HotelController hotelController;
    @InjectMocks
    ServicioHoteles servicioHoteles;

    @Test
    void testListWithHotels() {
        List<Hotel> hoteles = new ArrayList<>();
        hoteles.add(new Hotel());
        when(hotelRepository.findAll()).thenReturn(hoteles);

        ResponseEntity<?> response = hotelController.list();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(hoteles, response.getBody());
    }

    @Test
    void testListWithoutHotels() {
        when(hotelRepository.findAll()).thenReturn(new ArrayList<>());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            hotelController.list();
        });

        assertEquals(HttpStatus.NO_CONTENT, exception.getStatusCode());
    }

    @Test
    void testGetWithValidId() {
        Hotel hotel = new Hotel();
        hotel.setId(1);
        when(hotelRepository.findById(1)).thenReturn(java.util.Optional.ofNullable(hotel));

        ResponseEntity<?> response = hotelController.get(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(hotel, response.getBody());
    }

    @Test
    void testGetWithInvalidId() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            hotelController.get(0);
        });

        assertEquals("El ID debe ser un número entero positivo", exception.getMessage());
    }

    @Test
    void testGetWithNonExistingId() {
        when(hotelRepository.findById(1)).thenReturn(java.util.Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            hotelController.get(1);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("No se encontró ningún hotel por el ID proporcionado", exception.getReason());
    }

    @Test
    void testGetHotelByNombreWithValidInput() {
        String nombre = "Hotel ABC";
        List<Hotel> hoteles = new ArrayList<>();
        hoteles.add(new Hotel(1, "Hotel ABC", "Dirección ABC", "123456789", "hotel@abc.com", "www.hotelabc.com"));
        PageImpl<Hotel> page = new PageImpl<>(hoteles);
        when(hotelRepository.getHotelByNombre(eq(nombre), any(Pageable.class))).thenReturn(page);

        ResponseEntity<?> response = hotelController.getHotelByNombre(nombre, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(hoteles, response.getBody());
    }

    @Test
    void testGetHotelByNombreWithEmptyInput() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            hotelController.getHotelByNombre("", 10);
        });

        assertEquals("El parámetro 'nombre' no puede estar vacío", exception.getMessage());
    }

    @Test
    void testGetHotelByNombreWithNoContent() {
        String nombre = "Hotel XYZ";
        when(hotelRepository.getHotelByNombre(eq(nombre), any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            hotelController.getHotelByNombre(nombre, 10);
        });

        assertEquals(HttpStatus.NO_CONTENT, exception.getStatusCode());
    }

    @Test
    void testGetHotelByDireccionWithValidInput() {
        String direccion = "Calle Principal";
        List<Hotel> hoteles = new ArrayList<>();
        hoteles.add(new Hotel(1, "Hotel A", "Calle Principal", "123456789", "hotel@hotelA.com", "www.hotelA.com"));
        PageImpl<Hotel> page = new PageImpl<>(hoteles);
        when(hotelRepository.getHotelByDireccion(eq(direccion), any(Pageable.class))).thenReturn(page);

        ResponseEntity<?> response = hotelController.getHotelByDireccion(direccion, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(hoteles, response.getBody());
    }

    @Test
    void testGetHotelByDireccionWithEmptyInput() {
        String direccion = "";
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            hotelController.getHotelByDireccion(direccion, 10);
        });

        assertEquals("El parámetro 'nombre' no puede estar vacío", exception.getMessage());
    }

    @Test
    void testGetHotelByDireccionWithNullInput() {
        String direccion = null;
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            hotelController.getHotelByDireccion(direccion, 10);
        });

        assertEquals("El parámetro 'nombre' no puede estar vacío", exception.getMessage());
    }

    @Test
    void testGetHotelByDireccionWithNoContent() {
        String direccion = "Calle Principal";
        PageImpl<Hotel> page = new PageImpl<>(Collections.emptyList());
        when(hotelRepository.getHotelByDireccion(eq(direccion), any(Pageable.class))).thenReturn(page);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            hotelController.getHotelByDireccion(direccion, 10);
        });

        assertEquals(HttpStatus.NO_CONTENT, exception.getStatusCode());
    }

    @Test
    void testGetHotelByTelefonoWithValidInput() {
        String telefono = "123456789";
        List<Hotel> hoteles = new ArrayList<>();
        hoteles.add(new Hotel(1, "Hotel A", "Calle Principal", "123456789", "hotel@hotelA.com", "www.hotelA.com"));
        PageImpl<Hotel> page = new PageImpl<>(hoteles);
        when(hotelRepository.getHotelByTelefono(eq(telefono), any(Pageable.class))).thenReturn(page);

        ResponseEntity<?> response = hotelController.getHotelByTelefono(telefono, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(hoteles, response.getBody());
    }

    @Test
    void testGetHotelByTelefonoWithEmptyInput() {
        String telefono = "";
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            hotelController.getHotelByTelefono(telefono, 10);
        });

        assertEquals("El parámetro 'nombre' no puede estar vacío", exception.getMessage());
    }

    @Test
    void testGetHotelByTelefonoWithNullInput() {
        String telefono = null;
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            hotelController.getHotelByTelefono(telefono, 10);
        });

        assertEquals("El parámetro 'nombre' no puede estar vacío", exception.getMessage());
    }

    @Test
    void testGetHotelByTelefonoWithNoContent() {
        String telefono = "123456789";
        PageImpl<Hotel> page = new PageImpl<>(Collections.emptyList());
        when(hotelRepository.getHotelByTelefono(eq(telefono), any(Pageable.class))).thenReturn(page);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            hotelController.getHotelByTelefono(telefono, 10);
        });

        assertEquals(HttpStatus.NO_CONTENT, exception.getStatusCode());
    }

    @Test
    void testGetHotelByEmailWithValidInput() {
        String email = "hotel@example.com";
        List<Hotel> hoteles = new ArrayList<>();
        hoteles.add(new Hotel(1, "Hotel A", "Calle Principal", "123456789", "hotel@example.com", "www.hotelA.com"));
        PageImpl<Hotel> page = new PageImpl<>(hoteles);
        when(hotelRepository.getHotelByEmail(eq(email), any(Pageable.class))).thenReturn(page);

        ResponseEntity<?> response = hotelController.getHotelByEmail(email, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(hoteles, response.getBody());
    }

    @Test
    void testGetHotelByEmailWithEmptyInput() {
        String email = "";
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            hotelController.getHotelByEmail(email, 10);
        });

        assertEquals("El parámetro 'nombre' no puede estar vacío", exception.getMessage());
    }

    @Test
    void testGetHotelByEmailWithNullInput() {
        String email = null;
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            hotelController.getHotelByEmail(email, 10);
        });

        assertEquals("El parámetro 'nombre' no puede estar vacío", exception.getMessage());
    }

    @Test
    void testGetHotelByEmailWithNoContent() {
        String email = "hotel@example.com";
        PageImpl<Hotel> page = new PageImpl<>(Collections.emptyList());
        when(hotelRepository.getHotelByEmail(eq(email), any(Pageable.class))).thenReturn(page);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            hotelController.getHotelByEmail(email, 10);
        });

        assertEquals(HttpStatus.NO_CONTENT, exception.getStatusCode());
    }

    @Test
    void testGetHotelBySitioWebWithValidInput() {
        String sitioWeb = "www.hotel.com";
        List<Hotel> hoteles = new ArrayList<>();
        hoteles.add(new Hotel(1, "Hotel A", "Calle Principal", "123456789", "hotel@example.com", "www.hotel.com"));
        PageImpl<Hotel> page = new PageImpl<>(hoteles);
        when(hotelRepository.getHotelBySitioWeb(eq(sitioWeb), any(Pageable.class))).thenReturn(page);

        ResponseEntity<?> response = hotelController.getHotelBySitioWeb(sitioWeb, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(hoteles, response.getBody());
    }

    @Test
    void testGetHotelBySitioWebWithEmptyInput() {
        String sitioWeb = "";
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            hotelController.getHotelBySitioWeb(sitioWeb, 10);
        });

        assertEquals("El parámetro 'nombre' no puede estar vacío", exception.getMessage());
    }

    @Test
    void testGetHotelBySitioWebWithNullInput() {
        String sitioWeb = null;
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            hotelController.getHotelBySitioWeb(sitioWeb, 10);
        });

        assertEquals("El parámetro 'nombre' no puede estar vacío", exception.getMessage());
    }

    @Test
    void testGetHotelBySitioWebWithNoContent() {
        String sitioWeb = "www.hotel.com";
        PageImpl<Hotel> page = new PageImpl<>(Collections.emptyList());
        when(hotelRepository.getHotelBySitioWeb(eq(sitioWeb), any(Pageable.class))).thenReturn(page);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            hotelController.getHotelBySitioWeb(sitioWeb, 10);
        });

        assertEquals(HttpStatus.NO_CONTENT, exception.getStatusCode());
    }

    /*@Test
    void testPutWithValidId() {
        int id = 1;
        Hotel inputHotel = new Hotel(1, "Nuevo Hotel", "Nueva Dirección", "987654321", "nuevo_hotel@example.com",
                "www.nuevohotel.com");
        Hotel existingHotel = new Hotel(1, "Hotel A", "Calle Principal", "123456789", "hotel@example.com",
                "www.hotel.com");

        when(hotelRepository.findById(id)).thenReturn(Optional.of(existingHotel));

        ResponseEntity<?> response = hotelController.put(id, inputHotel);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(inputHotel, response.getBody());
    }*/

    /*@Test
    void testPutWithInvalidId() {
        int id = -1;
        Hotel inputHotel = new Hotel(1, "Nuevo Hotel", "Nueva Dirección", "987654321", "nuevo_hotel@example.com",
                "www.nuevohotel.com");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            hotelController.put(id, inputHotel);
        });

        assertEquals("El ID debe ser un número entero positivo", exception.getMessage());
    }*/

    @Test
    void testPutWithNonExistingHotel() {
        int id = 1;
        Hotel inputHotel = new Hotel(1, "Nuevo Hotel", "Nueva Dirección", "987654321", "nuevo_hotel@example.com",
                "www.nuevohotel.com");

        when(hotelRepository.findById(id)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            hotelController.put(id, inputHotel);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("No se encontró ningún hotel con el ID proporcionado", exception.getReason());
    }

    /*@Test
    void testPostWithValidInput() {
        Hotel inputHotel = new Hotel(1, "Nuevo Hotel", "Nueva Dirección", "987654321", "nuevo_hotel@example.com",
                "www.nuevohotel.com");

        ResponseEntity<?> response = hotelController.post(inputHotel);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(inputHotel, response.getBody());
    }*/

    @Test
    void testPostWithMissingName() {
        Hotel inputHotel = new Hotel(1, null, "Nueva Dirección", "987654321", "nuevo_hotel@example.com",
                "www.nuevohotel.com");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            hotelController.post(inputHotel);
        });

        assertEquals("Los campos 'nombre' y 'direccion' son obligatorios", exception.getMessage());
    }

    @Test
    void testPostWithMissingAddress() {
        Hotel inputHotel = new Hotel(1, "Nuevo Hotel", null, "987654321", "nuevo_hotel@example.com",
                "www.nuevohotel.com");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            hotelController.post(inputHotel);
        });

        assertEquals("Los campos 'nombre' y 'direccion' son obligatorios", exception.getMessage());
    }

    @Test
    void testPostWithMissingNameAndAddress() {
        Hotel inputHotel = new Hotel(1, null, null, "987654321", "nuevo_hotel@example.com", "www.nuevohotel.com");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            hotelController.post(inputHotel);
        });

        assertEquals("Los campos 'nombre' y 'direccion' son obligatorios", exception.getMessage());
    }

    @Test
    void testDeleteWithExistingHotel() {
        int hotelId = 1;
        Hotel existingHotel = new Hotel(hotelId, "Hotel Existente", "Dirección Existente", "123456789",
                "hotel_existente@example.com", "www.hotelexistente.com");
        when(hotelRepository.findById(hotelId)).thenReturn(Optional.of(existingHotel));

        ResponseEntity<?> response = hotelController.delete(hotelId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testDeleteWithNonExistingHotel() {
        int hotelId = 1;
        when(hotelRepository.findById(hotelId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            hotelController.delete(hotelId);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("No se encontró ningún hotel por el ID proporcionado", exception.getReason());
    }

    @Test
    void testDeleteWithNegativeId() {
        int hotelId = -1;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            hotelController.delete(hotelId);
        });

        assertEquals("El ID debe ser un número entero positivo", exception.getMessage());
    }

}
