package sk.r3n.jdbc.ora;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import sk.r3n.dto.ColumnId;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class TJoinDto implements Serializable {

    @ColumnId(table = "t_join", column = "id")
    private Long id;

    @ColumnId(table = "t_join", column = "t_base_types_fk")
    private Long tBaseTypesFk;

    @ColumnId(table = "t_join", column = "t_join_string")
    private String tJoinString;
}
