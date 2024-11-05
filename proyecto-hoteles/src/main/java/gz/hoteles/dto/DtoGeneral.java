package gz.hoteles.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import gz.hoteles.entities.EntityGeneral;

public interface DtoGeneral {
	
	@JsonIgnore
	EntityGeneral getEntity();
}
