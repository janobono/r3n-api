package sk.r3n.jdbc.postgre;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import sk.r3n.dto.ColumnId;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@ToString
public class TBaseTypesDto implements Serializable {

    @ColumnId(table = "t_base_types", column = "id")
    private Long id;

    @ColumnId(table = "t_base_types", column = "t_short")
    private Short tShort;

    @ColumnId(table = "t_base_types", column = "t_integer")
    private Integer tInteger;

    @ColumnId(table = "t_base_types", column = "t_long")
    private Long tLong;

    @ColumnId(table = "t_base_types", column = "t_big_decimal")
    private BigDecimal tBigDecimal;

    @ColumnId(table = "t_base_types", column = "t_string_char")
    private String tStringChar;

    @ColumnId(table = "t_base_types", column = "t_string_text")
    private String tStringText;

    @ColumnId(table = "t_base_types", column = "t_string_varchar")
    private String tStringVarchar;

    @ColumnId(table = "t_base_types", column = "t_string_scdf")
    private String tStringScdf;

    @ColumnId(table = "t_base_types", column = "t_blob")
    private File tBlob;

    @ColumnId(table = "t_base_types", column = "t_time_stamp")
    private LocalDateTime tTimeStamp;

    @ColumnId(table = "t_base_types", column = "t_time")
    private LocalTime tTime;

    @ColumnId(table = "t_base_types", column = "t_date")
    private LocalDate tDate;

    @ColumnId(table = "t_base_types", column = "t_boolean")
    private Boolean tBoolean;
}
