package sk.r3n.example.dal.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import sk.r3n.dto.Dto;
import sk.r3n.example.dal.domain.HotelDto;
import sk.r3n.example.dal.domain.r3n.MetaColumnHotel;
import sk.r3n.example.dal.domain.r3n.MetaSequence;
import sk.r3n.example.dal.domain.r3n.MetaTable;
import sk.r3n.jdbc.PostgreSqlBuilder;
import sk.r3n.jdbc.SqlBuilder;
import sk.r3n.sql.Column;
import sk.r3n.sql.Condition;
import sk.r3n.sql.Query;
import sk.r3n.util.CloneSerializable;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class HotelRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(HotelRepository.class);

    private DataSource dataSource;

    public HotelRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Page<HotelDto> getHotels(Pageable pageable) {
        LOGGER.debug("getHotels({})", pageable);
        Page<HotelDto> result;
        try (Connection connection = dataSource.getConnection()) {
            int totalRows = (Integer) new PostgreSqlBuilder().select(
                    connection,
                    Query
                            .SELECT(MetaColumnHotel.ID.column()).COUNT()
                            .FROM(MetaTable.HOTEL.table())
            ).get(0)[0];
            List<HotelDto> rows;
            if (pageable.isPaged()) {
                rows = new PostgreSqlBuilder().select(
                        connection,
                        Query
                                .SELECT(MetaColumnHotel.columns()).page(pageable.getPageNumber(), pageable.getPageSize())
                                .FROM(MetaTable.HOTEL.table()),
                        HotelDto.class
                );
            } else {
                rows = new PostgreSqlBuilder().select(
                        connection,
                        Query
                                .SELECT(MetaColumnHotel.columns())
                                .FROM(MetaTable.HOTEL.table()),
                        HotelDto.class
                );
            }
            result = new PageImpl(rows, pageable, totalRows);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        LOGGER.debug("getHotel({})={}", pageable, result);
        return result;
    }

    public boolean exists(Long id) {
        LOGGER.debug("exists({})", id);
        boolean result;
        try (Connection connection = dataSource.getConnection()) {
            List<Object[]> rows = new PostgreSqlBuilder().select(
                    connection,
                    Query
                            .SELECT(MetaColumnHotel.ID.column()).COUNT()
                            .FROM(MetaTable.HOTEL.table())
                            .WHERE(MetaColumnHotel.ID.column(), Condition.EQUALS, id)
            );
            result = ((Integer) rows.get(0)[0]) > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        LOGGER.debug("exists({})={}", id, result);
        return result;
    }

    public Optional<HotelDto> getHotel(Long id) {
        LOGGER.debug("getHotel({})", id);
        Optional<HotelDto> result = Optional.empty();
        try (Connection connection = dataSource.getConnection()) {
            List<HotelDto> rows = new PostgreSqlBuilder().select(
                    connection,
                    Query
                            .SELECT(MetaColumnHotel.columns())
                            .FROM(MetaTable.HOTEL.table())
                            .WHERE(MetaColumnHotel.ID.column(), Condition.EQUALS, id),
                    HotelDto.class
            );
            if (rows.size() == 1) {
                result = Optional.of(rows.get(0));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        LOGGER.debug("getHotel({})={}", id, result);
        return result;
    }

    public HotelDto insertHotel(HotelDto hotelDto) {
        LOGGER.debug("insertHotel({})", hotelDto);
        HotelDto result;
        try (Connection connection = dataSource.getConnection()) {
            Object[] row = new Dto().toArray(hotelDto, MetaColumnHotel.columns());
            row[0] = MetaSequence.SQ_HOTEL.sequence();
            Long id = (Long) new PostgreSqlBuilder().insert(
                    connection,
                    Query
                            .INSERT()
                            .INTO(MetaTable.HOTEL.table(), MetaColumnHotel.columns())
                            .VALUES(row)
                            .RETURNING(MetaColumnHotel.ID.column())
            );
            result = new HotelDto();
            row[0] = id;
            new Dto().fill(result, row, MetaColumnHotel.columns());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        LOGGER.debug("insertHotel({})={}", hotelDto, result);
        return result;
    }

    public HotelDto updateHotel(HotelDto hotelDto) {
        LOGGER.debug("updateHotel({})", hotelDto);
        HotelDto result;
        try (Connection connection = dataSource.getConnection()) {
            new PostgreSqlBuilder().update(
                    connection,
                    Query
                            .UPDATE(MetaTable.HOTEL.table())
                            .SET(
                                    new Column[]{MetaColumnHotel.NAME.column(), MetaColumnHotel.NOTE.column()},
                                    new Object[]{hotelDto.getName(), hotelDto.getNote()}
                            )
                            .WHERE(MetaColumnHotel.ID.column(), Condition.EQUALS, hotelDto.getId())
            );
            result = (HotelDto) CloneSerializable.clone(hotelDto);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        LOGGER.debug("updateHotel({})={}", hotelDto, result);
        return result;
    }

    public void deleteHotel(Long id) {
        LOGGER.debug("deleteHotel({})", id);
        try (Connection connection = dataSource.getConnection()) {
            new PostgreSqlBuilder().delete(
                    connection,
                    Query
                            .DELETE()
                            .FROM(MetaTable.HOTEL.table())
                            .WHERE(MetaColumnHotel.ID.column(), Condition.EQUALS, id)
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
