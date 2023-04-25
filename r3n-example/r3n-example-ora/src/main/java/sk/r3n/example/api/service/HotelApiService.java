package sk.r3n.example.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sk.r3n.example.api.service.so.HotelInputSO;
import sk.r3n.example.api.service.so.HotelSO;
import sk.r3n.example.dal.domain.r3n.dto.HotelDto;
import sk.r3n.example.dal.repository.HotelRepository;

@Service
public class HotelApiService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HotelApiService.class);

    private HotelRepository hotelRepository;

    @Autowired
    public void setHotelRepository(final HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    public Page<HotelSO> getHotels(final Pageable pageable) {
        LOGGER.debug("getHotels({})", pageable);
        return hotelRepository.getHotels(pageable)
                .map(hotelDto -> new HotelSO(hotelDto.id(), hotelDto.name(), hotelDto.note()));
    }

    public HotelSO getHotel(final Long id) {
        LOGGER.debug("getHotel({})", id);
        final HotelDto hotelDto = hotelRepository.getHotel(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Hotel not found."));
        return new HotelSO(hotelDto.id(), hotelDto.name(), hotelDto.note());
    }

    public HotelSO insertHotel(final HotelInputSO hotelInputSO) {
        LOGGER.debug("insertHotel({})", hotelInputSO);
        HotelDto hotelDto = new HotelDto(-1L, hotelInputSO.name(), hotelInputSO.note());
        hotelDto = hotelRepository.insertHotel(hotelDto);
        return new HotelSO(hotelDto.id(), hotelDto.name(), hotelDto.note());
    }

    public HotelSO updateHotel(final HotelSO hotelSO) {
        LOGGER.debug("updateHotel({})", hotelSO);
        if (!hotelRepository.exists(hotelSO.id())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Hotel not found.");
        }
        HotelDto hotelDto = new HotelDto(hotelSO.id(), hotelSO.name(), hotelSO.note());
        hotelDto = hotelRepository.updateHotel(hotelDto);
        return new HotelSO(hotelDto.id(), hotelDto.name(), hotelDto.note());
    }

    public void deleteHotel(final Long id) {
        LOGGER.debug("deleteHotel({})", id);
        if (!hotelRepository.exists(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Hotel not found.");
        }
        hotelRepository.deleteHotel(id);
    }
}
