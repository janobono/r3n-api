package sk.r3n.example.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValueCheckStrategy;
import sk.r3n.example.api.service.so.HotelInputSO;
import sk.r3n.example.api.service.so.HotelSO;
import sk.r3n.example.dal.domain.HotelDto;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface HotelMapper {

    HotelDto hotelSOtoHotelDto(HotelSO hotelSO);

    HotelSO hotelDtoToHotelSO(HotelDto hotelDto);

    @Mappings({
            @Mapping(target = "id", ignore = true)
    })
    HotelDto hotelInputSOtoHotelDto(HotelInputSO hotelInputSO);
}
