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
import sk.r3n.jdbc.PostgreSqlBuilder;
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

    public HotelRepository(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Page<HotelDto> getHotels(final Pageable pageable) {
        LOGGER.debug("getHotels({})", pageable);
        final Page<HotelDto> result;
        try (final Connection connection = dataSource.getConnection()) {
            final int totalRows = (Integer) new PostgreSqlBuilder().select(
                    connection,
                    Query
                            .SELECT(MetaColumnHotel.ID.column()).COUNT()
                            .FROM(MetaTable.HOTEL.table())
            ).get(0)[0];
            final List<Object[]> rows;
            if (pageable.isPaged()) {
                rows = new PostgreSqlBuilder().select(
                        connection,
                        Query
                                .SELECT(MetaColumnHotel.columns()).page(pageable.getPageNumber(), pageable.getPageSize())
                                .FROM(MetaTable.HOTEL.table())
                );
            } else {
                rows = new PostgreSqlBuilder().select(
                        connection,
                        Query
                                .SELECT(MetaColumnHotel.columns())
                                .FROM(MetaTable.HOTEL.table())
                );
            }
            result = new PageImpl<>(rows.stream().map(HotelDto::toObject).collect(Collectors.toList()), pageable, totalRows);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        LOGGER.debug("getHotel({})={}", pageable, result);
        return result;
    }

    public boolean exists(final Long id) {
        LOGGER.debug("exists({})", id);
        final boolean result;
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = new PostgreSqlBuilder().select(
                    connection,
                    Query
                            .SELECT(MetaColumnHotel.ID.column()).COUNT()
                            .FROM(MetaTable.HOTEL.table())
                            .WHERE(MetaColumnHotel.ID.column(), Condition.EQUALS, id)
            );
            result = ((Integer) rows.get(0)[0]) > 0;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        LOGGER.debug("exists({})={}", id, result);
        return result;
    }

    public Optional<HotelDto> getHotel(final Long id) {
        LOGGER.debug("getHotel({})", id);
        Optional<HotelDto> result = Optional.empty();
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = new PostgreSqlBuilder().select(
                    connection,
                    Query
                            .SELECT(MetaColumnHotel.columns())
                            .FROM(MetaTable.HOTEL.table())
                            .WHERE(MetaColumnHotel.ID.column(), Condition.EQUALS, id)
            );
            if (rows.size() == 1) {
                result = Optional.of(HotelDto.toObject(rows.get(0)));
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        LOGGER.debug("getHotel({})={}", id, result);
        return result;
    }

    public HotelDto insertHotel(final HotelDto hotelDto) {
        LOGGER.debug("insertHotel({})", hotelDto);
        final HotelDto result;
        try (final Connection connection = dataSource.getConnection()) {
            final Object[] row = HotelDto.toArray(hotelDto);
            row[0] = MetaSequence.SQ_HOTEL.sequence();
            final Long id = (Long) new PostgreSqlBuilder().insert(
                    connection,
                    Query
                            .INSERT()
                            .INTO(MetaTable.HOTEL.table(), MetaColumnHotel.columns())
                            .VALUES(row)
                            .RETURNING(MetaColumnHotel.ID.column())
            );
            row[0] = id;
            result = HotelDto.toObject(row);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        LOGGER.debug("insertHotel({})={}", hotelDto, result);
        return result;
    }

    public HotelDto updateHotel(final HotelDto hotelDto) {
        LOGGER.debug("updateHotel({})", hotelDto);
        final HotelDto result;
        try (final Connection connection = dataSource.getConnection()) {
            new PostgreSqlBuilder().update(
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
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        LOGGER.debug("updateHotel({})={}", hotelDto, result);
        return result;
    }

    public void deleteHotel(final Long id) {
        LOGGER.debug("deleteHotel({})", id);
        try (final Connection connection = dataSource.getConnection()) {
            new PostgreSqlBuilder().delete(
                    connection,
                    Query
                            .DELETE()
                            .FROM(MetaTable.HOTEL.table())
                            .WHERE(MetaColumnHotel.ID.column(), Condition.EQUALS, id)
            );
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
