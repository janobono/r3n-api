package sk.r3n.example.dal.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sk.r3n.example.dal.domain.r3n.dto.HotelDto;
import sk.r3n.example.dal.domain.r3n.meta.MetaColumnHotel;
import sk.r3n.example.dal.domain.r3n.meta.MetaSequence;
import sk.r3n.example.dal.domain.r3n.meta.MetaTable;
import sk.r3n.jdbc.Sql;
import sk.r3n.jdbc.SqlBuilder;
import sk.r3n.jdbc.SqlParam;
import sk.r3n.sql.Column;
import sk.r3n.sql.Condition;
import sk.r3n.sql.DataType;
import sk.r3n.sql.Query;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public class HotelRepository {

    private static class HotelMapper implements RowMapper<HotelDto> {

        private final SqlBuilder sqlBuilder;

        private final DataType[] dataTypes;

        public HotelMapper(final SqlBuilder sqlBuilder) {
            this.sqlBuilder = sqlBuilder;
            dataTypes = Stream.of(MetaColumnHotel.columns()).map(Column::dataType).toArray(DataType[]::new);
        }

        @Override
        public HotelDto mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
            final Object[] row = sqlBuilder.getRow(resultSet, dataTypes);
            return HotelDto.toObject(row);
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(HotelRepository.class);

    private final JdbcTemplate jdbcTemplate;
    private final SqlBuilder sqlBuilder;

    public HotelRepository(final JdbcTemplate jdbcTemplate, final SqlBuilder sqlBuilder) {
        this.jdbcTemplate = jdbcTemplate;
        this.sqlBuilder = sqlBuilder;
    }

    public Page<HotelDto> getHotels(final Pageable pageable) {
        LOGGER.debug("getHotels({})", pageable);
        final Sql totalRowsSql = sqlBuilder.select(Query
                .SELECT(MetaColumnHotel.ID.column()).COUNT()
                .FROM(MetaTable.HOTEL.table())
        );

        final Integer totalRows = jdbcTemplate.query(totalRowsSql.toSql(),
                (resultSet) -> {
                    if (resultSet.next()) {
                        return (Integer) sqlBuilder.getColumn(resultSet, 1, DataType.INTEGER);
                    }
                    return 0;
                }
        );
        assert totalRows != null;

        final Sql sql;
        if (pageable.isPaged()) {
            sql = sqlBuilder.select(Query
                    .SELECT(MetaColumnHotel.columns()).page(pageable.getPageNumber(), pageable.getPageSize())
                    .FROM(MetaTable.HOTEL.table())
            );
        } else {
            sql = sqlBuilder.select(Query
                    .SELECT(MetaColumnHotel.columns())
                    .FROM(MetaTable.HOTEL.table())
            );
        }
        final List<HotelDto> hotels = jdbcTemplate.query(sql.toSql(), new HotelMapper(sqlBuilder), sql.getParamsObjects());

        final Page<HotelDto> result = new PageImpl<>(hotels, pageable, totalRows);
        LOGGER.debug("getHotels({})={}", pageable, result);
        return result;
    }

    public boolean exists(final Long id) {
        LOGGER.debug("exists({})", id);

        final Sql sql = sqlBuilder.select(Query
                .SELECT(MetaColumnHotel.ID.column()).COUNT()
                .FROM(MetaTable.HOTEL.table())
                .WHERE(MetaColumnHotel.ID.column(), Condition.EQUALS, id)
        );

        final Boolean result = jdbcTemplate.query(sql.toSql(),
                (resultSet) -> {
                    if (resultSet.next()) {
                        return (Long) sqlBuilder.getColumn(resultSet, 1, DataType.LONG) > 0;
                    }
                    return false;
                },
                sql.getParamsObjects()
        );
        assert result != null;

        LOGGER.debug("exists({})={}", id, result);
        return result;
    }

    public Optional<HotelDto> getHotel(final Long id) {
        LOGGER.debug("getHotel({})", id);
        final Sql sql = sqlBuilder.select(Query
                .SELECT(MetaColumnHotel.columns())
                .FROM(MetaTable.HOTEL.table())
                .WHERE(MetaColumnHotel.ID.column(), Condition.EQUALS, id)
        );
        final Optional<HotelDto> result = jdbcTemplate.query(sql.toSql(), new HotelMapper(sqlBuilder), sql.getParamsObjects())
                .stream().findFirst();
        LOGGER.debug("getHotel({})={}", id, result);
        return result;
    }

    @Transactional
    public HotelDto insertHotel(final HotelDto hotelDto) {
        LOGGER.debug("insertHotel({})", hotelDto);

        final Object[] row = HotelDto.toArray(hotelDto);
        row[0] = MetaSequence.SQ_HOTEL.sequence();

        final Sql sql = sqlBuilder.insert(Query
                .INSERT()
                .INTO(MetaTable.HOTEL.table(), MetaColumnHotel.columns())
                .VALUES(row)
                .RETURNING(MetaColumnHotel.ID.column())
        );

        final KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            final PreparedStatement ps = connection.prepareStatement(sql.toSql(), Statement.RETURN_GENERATED_KEYS);
            sqlBuilder.setParams(ps, sql.getParams().toArray(new SqlParam[0]));
            return ps;
        }, keyHolder);
        final Long id = (Long) keyHolder.getKey();
        row[0] = id;
        final HotelDto result = HotelDto.toObject(row);

        LOGGER.debug("insertHotel({})={}", hotelDto, result);
        return result;
    }

    @Transactional
    public HotelDto updateHotel(final HotelDto hotelDto) {
        LOGGER.debug("updateHotel({})", hotelDto);
        final Sql sql = sqlBuilder.update(Query
                .UPDATE(MetaTable.HOTEL.table())
                .SET(
                        new Column[]{MetaColumnHotel.NAME.column(), MetaColumnHotel.NOTE.column()},
                        new Object[]{hotelDto.name(), hotelDto.note()}
                )
                .WHERE(MetaColumnHotel.ID.column(), Condition.EQUALS, hotelDto.id())
        );
        jdbcTemplate.update(sql.toSql(), sql.getParamsObjects());

        LOGGER.debug("updateHotel({})={}", hotelDto, hotelDto);
        return hotelDto;
    }

    @Transactional
    public void deleteHotel(final Long id) {
        LOGGER.debug("deleteHotel({})", id);
        final Sql sql = sqlBuilder.delete(Query
                .DELETE()
                .FROM(MetaTable.HOTEL.table())
                .WHERE(MetaColumnHotel.ID.column(), Condition.EQUALS, id)
        );
        jdbcTemplate.update(sql.toSql(), sql.getParamsObjects());
    }
}
