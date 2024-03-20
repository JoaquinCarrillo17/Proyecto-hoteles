package gz.hoteles.dto;

import java.util.ArrayList;
import java.util.List;

import gz.hoteles.entities.CategoriaServicio;
import lombok.Data;

@Data
public class ServicioDTO {
    
    private int id;
    private String nombre;
    private String descripcion;
    private CategoriaServicio categoria;
    private List<HotelDTO> hoteles = new ArrayList<HotelDTO>();

}
