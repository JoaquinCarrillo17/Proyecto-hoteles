package gz.hoteles.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import gz.hoteles.dto.DtoGeneral;

public interface EntityGeneral {
	
	@JsonIgnore
	public DtoGeneral getDto();
	
}
