package sk.r3n.example.dal.domain.r3n.dto;

public record HotelDto(
        Long id,
        String name,
        String note
) {

    public static Object[] toArray(HotelDto hotelDto) {
        return new Object[]{
                hotelDto.id,
                hotelDto.name,
                hotelDto.note
        };
    }

    public static HotelDto toObject(Object[] array) {
        return new HotelDto(
                (Long) array[0],
                (String) array[1],
                (String) array[2]
        );
    }
}
