package gz.hoteles;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class ProyectoHotelesApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProyectoHotelesApplication.class, args);
    }

    /*@Component
    class HotelDataInitializer implements CommandLineRunner {

        @Autowired
        IServicioHoteles servicioHoteles;

        @Autowired
        HistoricoRepository historicoRepository;

        @Override
        public void run(String... args) throws Exception {
            List<Hotel> hoteles = generarHoteles();
            for (Hotel hotel : hoteles) {
                servicioHoteles.crearHotel(hotel);
            }
            // Obtener la fecha de hace 3 semanas
            LocalDate fechaBase = LocalDate.now().minusWeeks(3);

            // Convertir LocalDate a Date
            for (int i = 0; i < 5; i++) {
                Historico historico = generarHistoricoAleatorio(fechaBase);
                historicoRepository.save(historico);
                // Ajustar la fecha base para el siguiente historico
                fechaBase = sumarDias(fechaBase, 5);
            }
        }

        public static Historico generarHistoricoAleatorio(LocalDate fecha) {
            Random random = new Random();
            Historico historico = new Historico();
            historico.setHotelesTotales(random.nextInt(100) + 1);
            historico.setHabitacionesTotales(random.nextInt(500) + 1);
            int habitacionesDisponibles = random.nextInt(historico.getHabitacionesTotales() + 1);
            historico.setHabitacionesDisponibles(habitacionesDisponibles);
            historico.setHabitacionesReservadas(historico.getHabitacionesTotales() - habitacionesDisponibles);
            historico.setHuespedesTotales(random.nextInt(1000) + 1);
            historico.setServiciosTotales(random.nextInt(100) + 1);
            historico.setFecha(fecha);
            return historico;
        }

        public static LocalDate sumarDias(LocalDate fecha, int dias) {
            return fecha.plusDays(dias);
        }

        private List<Hotel> generarHoteles() {
            List<Hotel> hoteles = new ArrayList<>();

            // Crear hoteles con datos inventados pero coherentes
            hoteles.add(crearHotel("Hotel Paco", "Calle Paco", "912345678", "info@hotelpaco.com", "www.hotelpaco.com"));
            hoteles.add(crearHotel("JC Hotel Miguel", "Calle Miguel", "564821548", "info@miguelhotel.com",
                    "www.miguelhotel.com"));
            hoteles.add(crearHotel("Hotel Luis", "Calle Luis", "912345678", "info@hotelluis.com", "www.hotelluis.com"));
            hoteles.add(
                    crearHotel("Hotel Juan", "Calle Juan", "258145648", "JCjuanhotel@hotel.com", "www.juanhotel.com"));
            hoteles.add(
                    crearHotel("Hotel Pepe", "Calle Pepejc", "912345678", "info@pepehotel.com", "www.pepehotel.com"));

            for (int i = 1; i <= 10; i++) {
                String nombre = "Hotel " + i;
                String direccion = "Calle " + i;
                String telefono = "9" + String.valueOf(12345678 + i);
                String email = "info@hotel" + i + ".com";
                String sitioWeb = "www.hotel" + i + ".com";
                hoteles.add(crearHotel(nombre, direccion, telefono, email, sitioWeb));
            }

            return hoteles;
        }

        private Hotel crearHotel(String nombre, String direccion, String telefono, String email, String sitioWeb) {
            Hotel hotel = new Hotel();
            hotel.setNombre(nombre);
            hotel.setDireccion(direccion);
            hotel.setTelefono(telefono);
            hotel.setEmail(email);
            hotel.setSitioWeb(sitioWeb);

            // Agregar servicios ficticios
            List<Servicio> servicios = new ArrayList<>();
            Servicio servicio = new Servicio();
            servicio.setNombre("Servicio del " + nombre);
            servicio.setDescripcion("Descripción del servicio del " + nombre);
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
            huesped.setNombreCompleto("Huesped " + nombre);
            huesped.setDni("12345678A");
            huesped.setEmail("huesped@" + nombre.replace(" ", "").toLowerCase() + ".com");
            huesped.setFechaCheckIn(new Date());
            huesped.setFechaCheckOut(new Date());
            huespedes.add(huesped);
            habitacion.setHuespedes(huespedes);
            habitaciones.add(habitacion);
            hotel.setHabitaciones(habitaciones);

            return hotel;
        }
    }*/
}
