package me.bhradec.blog.mapper;

import me.bhradec.blog.domain.AppUser;
import me.bhradec.blog.dto.AppUserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AppUserMapper {
    AppUserDto toDto(AppUser appUser);
}
