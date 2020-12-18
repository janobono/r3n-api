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
import sk.r3n.example.dal.domain.HotelDto;
import sk.r3n.example.dal.repository.HotelRepository;
import sk.r3n.example.mapper.HotelMapper;

@Service
public class HotelApiService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HotelApiService.class);

    private HotelMapper hotelMapper;

    private HotelRepository hotelRepository;

    @Autowired
    public void setHotelMapper(HotelMapper hotelMapper) {
        this.hotelMapper = hotelMapper;
    }

    @Autowired
    public void setHotelRepository(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    public Page<HotelSO> getHotels(Pageable pageable) {
        LOGGER.debug("getHotels({})", pageable);
        return hotelRepository.getHotels(pageable).map(hotelMapper::hotelDtoToHotelSO);
    }

    public HotelSO getHotel(Long id) {
        LOGGER.debug("getHotel({})", id);
        HotelDto hotelDto = hotelRepository.getHotel(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Hotel not found."));
        return hotelMapper.hotelDtoToHotelSO(hotelDto);
    }

    public HotelSO insertHotel(HotelInputSO hotelInputSO) {
        LOGGER.debug("insertHotel({})", hotelInputSO);
        HotelDto hotelDto = hotelMapper.hotelInputSOtoHotelDto(hotelInputSO);
        return hotelMapper.hotelDtoToHotelSO(hotelRepository.insertHotel(hotelDto));
    }

    public HotelSO updateHotel(HotelSO hotelSO) {
        LOGGER.debug("updateHotel({})", hotelSO);
        if (!hotelRepository.exists(hotelSO.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Hotel not found.");
        }
        HotelDto hotelDto = hotelMapper.hotelSOtoHotelDto(hotelSO);
        return hotelMapper.hotelDtoToHotelSO(hotelRepository.updateHotel(hotelDto));
    }

    public void deleteHotel(Long id) {
        LOGGER.debug("deleteHotel({})", id);
        if (!hotelRepository.exists(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Hotel not found.");
        }
        hotelRepository.deleteHotel(id);
    }
}
