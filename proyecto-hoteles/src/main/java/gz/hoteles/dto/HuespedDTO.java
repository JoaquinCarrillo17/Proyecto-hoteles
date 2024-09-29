package gz.hoteles.dto;

import java.util.Date;

import lombok.Data;

@Data
public class HuespedDTO {
    
    private int id;
    private String nombreCompleto;
    private String dni;
    private String email;
    private Date fechaCheckIn;
    private Date fechaCheckOut;
    private String nombreHotel;

}
