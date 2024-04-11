package gz.hoteles;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import gz.hoteles.entities.CategoriaServicio;
import gz.hoteles.entities.Habitacion;
import gz.hoteles.entities.Hotel;
import gz.hoteles.entities.Huesped;
import gz.hoteles.entities.Servicio;
import gz.hoteles.entities.TipoHabitacion;
import gz.hoteles.servicio.IServicioHoteles;

@SpringBootApplication
public class ProyectoHotelesApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProyectoHotelesApplication.class, args);
	}

	@Component
	class HotelDataInitializer implements CommandLineRunner {

		@Autowired
    	IServicioHoteles servicioHoteles;

		@Override
		public void run(String... args) throws Exception {
			List<Hotel> hoteles = generarHoteles();
			for (Hotel hotel : hoteles) {
				servicioHoteles.crearHotel(hotel);
			}
		}

		private List<Hotel> generarHoteles() {
			List<Hotel> hoteles = new ArrayList<>();

			// Crear 5 hoteles con datos inventados pero coherentes
			for (int i = 1; i <= 5; i++) {
				Hotel hotel = new Hotel();
				hotel.setNombre("Hotel " + i);
				hotel.setDireccion("Dirección del Hotel " + i);
				hotel.setTelefono("Teléfono del Hotel " + i);
				hotel.setEmail("correo" + i + "@hotel.com");
				hotel.setSitioWeb("www.hotel" + i + ".com");

				// Agregar servicios ficticios
				List<Servicio> servicios = new ArrayList<>();
				Servicio servicio = new Servicio();
				servicio.setNombre("Servicio del Hotel " + i);
				servicio.setDescripcion("Descripción del servicio del Hotel " + i);
				servicio.setCategoria(CategoriaServicio.GIMNASIO);
				servicios.add(servicio);
				hotel.setServicios(servicios);

				// Agregar habitaciones ficticias
				List<Habitacion> habitaciones = new ArrayList<>();
				Habitacion habitacion = new Habitacion();
				habitacion.setNumero("101");
				habitacion.setTipoHabitacion(TipoHabitacion.INDIVIDUAL);
				habitacion.setPrecioNoche(100);
				// Agregar un huésped ficticio
				List<Huesped> huespedes = new ArrayList<>();
				Huesped huesped = new Huesped();
				huesped.setNombreCompleto("Huesped " + i);
				huesped.setDni("12345678A");
				huesped.setEmail("huesped" + i + "@hotel.com");
				huespedes.add(huesped);
				habitacion.setHuespedes(huespedes);
				habitaciones.add(habitacion);
				hotel.setHabitaciones(habitaciones);

				hoteles.add(hotel);
			}

			return hoteles;
		}
	}

}
