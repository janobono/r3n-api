package sk.r3n.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.junit.jupiter.api.Test;
import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;
import sk.r3n.sql.Table;

import static org.assertj.core.api.Assertions.assertThat;

public class DtoTest {

    @Getter
    @Setter
    @ToString
    public static class Dto01 {

        @ColumnId(table = "test", column = "field01")
        protected Long number;

        @ColumnId(table = "test", column = "field02")
        protected String string;
    }

    @Getter
    @Setter
    @ToString
    public static class Dto02 {

        @ColumnId(table = "test", column = "field01")
        protected Long otherNumber;

        @ColumnId(table = "test", column = "field02")
        protected String otherString;

        @ColumnId(table = "test", column = "field03")
        protected Boolean otherBoolean;
    }

    @Test
    public void test() {
        // Create Dto instance
        Dto dto = new Dto();

        // Create Dto01
        Dto01 dto01 = new Dto01();
        dto01.setNumber(10l);
        dto01.setString("test");

        // dto -> array
        Object[] array = dto.toArray(
                dto01,
                new Column("field01", new Table("test", "t1"), DataType.LONG),
                new Column("field02", new Table("test", "t1"), DataType.STRING)
        );
        assertThat(array.length).isEqualTo(2);
        assertThat(array[0]).isEqualTo(10l);
        assertThat(array[1]).isEqualTo("test");

        // array to dto
        dto.fill(
                dto01,
                new Object[]{20l, "test2"},
                new Column("field01", new Table("test", "t1"), DataType.LONG),
                new Column("field02", new Table("test", "t1"), DataType.STRING)
        );
        assertThat(dto01.getNumber()).isEqualTo(20l);
        assertThat(dto01.getString()).isEqualTo("test2");

        // dto to dto
        Dto02 dto02 = new Dto02();
        dto.objToObj(dto01, dto02);
        assertThat(dto02.getOtherNumber()).isEqualTo(dto01.getNumber());
        assertThat(dto02.getOtherString()).isEqualTo(dto01.getString());
        assertThat(dto02.getOtherBoolean()).isNull();
    }
}
