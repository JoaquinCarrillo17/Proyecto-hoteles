package gz.hoteles.dto;

import java.util.List;


import gz.hoteles.support.OrderCriteria;
import gz.hoteles.support.PageDto;
import gz.hoteles.support.SearchCriteria;
import lombok.Data;

@Data
public class DynamicSearchPaginatorDto {

	private List<SearchCriteria> listSearchCriteria;
	private List<OrderCriteria> listOrderCriteria;
	private PageDto page;
}
