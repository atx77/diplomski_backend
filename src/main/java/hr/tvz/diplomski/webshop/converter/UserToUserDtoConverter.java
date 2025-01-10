package hr.tvz.diplomski.webshop.converter;

import hr.tvz.diplomski.webshop.domain.User;
import hr.tvz.diplomski.webshop.dto.UserDto;
import org.springframework.core.convert.converter.Converter;

public class UserToUserDtoConverter implements Converter<User, UserDto> {

    @Override
    public UserDto convert(User source) {
        UserDto userDto = new UserDto();
        userDto.setEmail(source.getEmail());
        userDto.setFirstName(source.getFirstName());
        userDto.setLastName(source.getLastName());
        return userDto;
    }
}
