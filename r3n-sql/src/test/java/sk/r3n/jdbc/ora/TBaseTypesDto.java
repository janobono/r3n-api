package sk.r3n.jdbc.ora;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import sk.r3n.dto.ColumnId;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class TBaseTypesDto implements Serializable {

    @ColumnId(table = "t_base_types", column = "id")
    protected Long id;

    @ColumnId(table = "t_base_types", column = "t_short")
    protected Short tShort;

    @ColumnId(table = "t_base_types", column = "t_integer")
    protected Integer tInteger;

    @ColumnId(table = "t_base_types", column = "t_long")
    protected Long tLong;

    @ColumnId(table = "t_base_types", column = "t_big_decimal")
    protected BigDecimal tBigDecimal;

    @ColumnId(table = "t_base_types", column = "t_string_char")
    protected String tStringChar;

    @ColumnId(table = "t_base_types", column = "t_string_clob")
    protected String tStringClob;

    @ColumnId(table = "t_base_types", column = "t_string_varchar2")
    protected String tStringVarchar2;

    @ColumnId(table = "t_base_types", column = "t_string_scdf")
    protected String tStringScdf;

    @ColumnId(table = "t_base_types", column = "t_blob")
    protected File tBlob;

    @ColumnId(table = "t_base_types", column = "t_time_stamp")
    protected LocalDateTime tTimeStamp;

    @ColumnId(table = "t_base_types", column = "t_date")
    protected LocalDate tDate;

    @ColumnId(table = "t_base_types", column = "t_boolean")
    protected Boolean tBoolean;
}
