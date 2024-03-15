package gz.hoteles.entities;

import lombok.Data;

@Data
public class JSONMapper {
    private String field;
    private String value;
    private int pages;
    private String sortBy;
}
