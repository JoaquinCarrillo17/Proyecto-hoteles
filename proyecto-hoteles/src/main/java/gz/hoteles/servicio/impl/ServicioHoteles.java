package gz.hoteles.servicio.impl;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gz.hoteles.dto.HotelDTO;
import gz.hoteles.entities.Hotel;
import gz.hoteles.repositories.HotelRepository;

@Service
public class ServicioHoteles extends DtoServiceImpl<HotelDTO, Hotel>  {

    @Autowired
    HotelRepository hotelRepository;

    @Autowired
    ServicioUbicacion ubicacionService;

    private List<String> hotelPhotos = IntStream.rangeClosed(1, 20)
            .mapToObj(i -> "hotel-" + i + ".jpg")
            .collect(Collectors.toList());

    public synchronized String getAvailablePhoto() {
        // Encuentra la primera foto no asignada en la base de datos
        for (String photo : hotelPhotos) {
            if (!hotelRepository.existsByFoto(photo)) { // Método en el repositorio que verifica la existencia
                return photo;
            }
        }
        throw new RuntimeException("No hay más fotos disponibles");
    }

    @Override
    protected HotelDTO parseDto(Hotel entity) {
        HotelDTO dto = (HotelDTO) entity.getDto();
        if (entity.getUbicacion() != null) {
            dto.setUbicacion(this.ubicacionService.parseDto(entity.getUbicacion()));
        }
        return dto;
    }

    @Override
    protected Hotel parseEntity(HotelDTO dto) throws Exception {
        Hotel entity = (Hotel) dto.getEntity();
        String foto = getAvailablePhoto();
        entity.setFoto(foto);
        return entity;
    }


}
