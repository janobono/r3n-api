package sk.r3n.example.dal.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import sk.r3n.example.dal.domain.r3n.dto.HotelDto;
import sk.r3n.example.dal.domain.r3n.meta.MetaColumnHotel;
import sk.r3n.example.dal.domain.r3n.meta.MetaSequence;
import sk.r3n.example.dal.domain.r3n.meta.MetaTable;
import sk.r3n.jdbc.OraSqlBuilder;
import sk.r3n.sql.Column;
import sk.r3n.sql.Condition;
import sk.r3n.sql.Query;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class HotelRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(HotelRepository.class);

    private final DataSource dataSource;

    public HotelRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Page<HotelDto> getHotels(Pageable pageable) {
        LOGGER.debug("getHotels({})", pageable);
        Page<HotelDto> result;
        try (Connection connection = dataSource.getConnection()) {
            int totalRows = (Integer) new OraSqlBuilder().select(
                    connection,
                    Query
                            .SELECT(MetaColumnHotel.ID.column()).COUNT()
                            .FROM(MetaTable.HOTEL.table())
            ).get(0)[0];
            List<Object[]> rows;
            if (pageable.isPaged()) {
                rows = new OraSqlBuilder().select(
                        connection,
                        Query
                                .SELECT(MetaColumnHotel.columns()).page(pageable.getPageNumber(), pageable.getPageSize())
                                .FROM(MetaTable.HOTEL.table())
                );
            } else {
                rows = new OraSqlBuilder().select(
                        connection,
                        Query
                                .SELECT(MetaColumnHotel.columns())
                                .FROM(MetaTable.HOTEL.table())
                );
            }
            result = new PageImpl<>(rows.stream().map(HotelDto::toObject).collect(Collectors.toList()), pageable, totalRows);
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
            List<Object[]> rows = new OraSqlBuilder().select(
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
            List<Object[]> rows = new OraSqlBuilder().select(
                    connection,
                    Query
                            .SELECT(MetaColumnHotel.columns())
                            .FROM(MetaTable.HOTEL.table())
                            .WHERE(MetaColumnHotel.ID.column(), Condition.EQUALS, id)
            );
            if (rows.size() == 1) {
                result = Optional.of(HotelDto.toObject(rows.get(0)));
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
            Object[] row = HotelDto.toArray(hotelDto);
            row[0] = MetaSequence.SQ_HOTEL.sequence();
            Long id = (Long) new OraSqlBuilder().insert(
                    connection,
                    Query
                            .INSERT()
                            .INTO(MetaTable.HOTEL.table(), MetaColumnHotel.columns())
                            .VALUES(row)
                            .RETURNING(MetaColumnHotel.ID.column())
            );
            row[0] = id;
            result = HotelDto.toObject(row);
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
            new OraSqlBuilder().update(
                    connection,
                    Query
                            .UPDATE(MetaTable.HOTEL.table())
                            .SET(
                                    new Column[]{MetaColumnHotel.NAME.column(), MetaColumnHotel.NOTE.column()},
                                    new Object[]{hotelDto.name(), hotelDto.note()}
                            )
                            .WHERE(MetaColumnHotel.ID.column(), Condition.EQUALS, hotelDto.id())
            );
            result = hotelDto;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        LOGGER.debug("updateHotel({})={}", hotelDto, result);
        return result;
    }

    public void deleteHotel(Long id) {
        LOGGER.debug("deleteHotel({})", id);
        try (Connection connection = dataSource.getConnection()) {
            new OraSqlBuilder().delete(
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
