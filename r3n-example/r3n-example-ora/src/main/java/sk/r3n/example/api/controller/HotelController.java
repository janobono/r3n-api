package sk.r3n.example.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sk.r3n.example.api.service.HotelApiService;
import sk.r3n.example.api.service.so.HotelInputSO;
import sk.r3n.example.api.service.so.HotelSO;

@RestController
@RequestMapping(path = "/hotels")
public class HotelController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HotelController.class);

    private final HotelApiService hotelApiService;

    public HotelController(final HotelApiService hotelApiService) {
        this.hotelApiService = hotelApiService;
    }

    @GetMapping
    public ResponseEntity<Page<HotelSO>> getHotels(final Pageable pageable) {
        LOGGER.debug("getHotels({})", pageable);
        return new ResponseEntity<>(hotelApiService.getHotels(pageable), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HotelSO> getHotel(@PathVariable("id") final Long id) {
        LOGGER.debug("getHotel({})", id);
        return new ResponseEntity<>(hotelApiService.getHotel(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<HotelSO> addHotel(@RequestBody final HotelInputSO hotel) {
        LOGGER.debug("addHotel({})", hotel);
        return new ResponseEntity<>(hotelApiService.insertHotel(hotel), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<HotelSO> setHotel(@RequestBody final HotelSO hotel) {
        LOGGER.debug("setHotel({})", hotel);
        return new ResponseEntity<>(hotelApiService.updateHotel(hotel), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public void deleteHotel(@PathVariable("id") final long id) {
        LOGGER.debug("deleteHotel({})", id);
        hotelApiService.deleteHotel(id);
    }
}
