package gz.hoteles.support;

import java.util.List;

import lombok.Data;

@Data
public class SearchRequest {
    private ListOrderCriteria listOrderCriteria;
    private List<SearchCriteria> listSearchCriteria;
    private Page page;
}
