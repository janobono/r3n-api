package sk.r3n.example.dal.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import sk.r3n.dto.ColumnId;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class HotelDto implements Serializable {

    @ColumnId(table = "hotel", column = "id")
    private Long id;

    @ColumnId(table = "hotel", column = "name")
    private String name;

    @ColumnId(table = "hotel", column = "note")
    private String note;
}
