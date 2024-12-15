package gz.hoteles.dto;

import java.util.List;

import lombok.Data;

@Data
public class HotelRequestDto {

    private HotelDTO hotel;
    private List<HabitacionDTO> habitaciones;
    
}
