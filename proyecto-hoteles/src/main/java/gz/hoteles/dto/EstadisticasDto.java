package gz.hoteles.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasDto {
    private Map<String, Integer> reservasPorMes;  // Mes -> Número de reservas
    private Map<String, Double> gananciasPorMes; // Mes -> Ganancias
    private int totalHabitaciones;
    private int habitacionesReservadas;
    private int habitacionesLibres;
}
