package gz.hoteles.support;

import java.util.List;

import lombok.Data;

@Data
public class SearchRequest {
    private OrderCriteria listOrderCriteria;
    private List<SearchCriteria> listSearchCriteria;
    private PageDto page;
}
