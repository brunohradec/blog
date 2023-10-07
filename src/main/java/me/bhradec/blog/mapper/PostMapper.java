package me.bhradec.blog.mapper;

import me.bhradec.blog.domain.AppUser;
import me.bhradec.blog.domain.Post;
import me.bhradec.blog.dto.PostCommandDto;
import me.bhradec.blog.dto.PostDto;
import me.bhradec.blog.exception.NotFoundException;
import me.bhradec.blog.service.AppUserService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class PostMapper {
    @Autowired
    AppUserService appUserService;

    @Autowired
    AppUserMapper appUserMapper;

    public AppUser getAuthorByUsername(String username) throws NotFoundException {
        return appUserService
                .findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User with username " + username + " not found"));
    }

    @Mapping(target = "author", expression = "java(appUserMapper.toDto(post.getAuthor()))")
    public abstract PostDto toDto(Post post);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", expression = "java(getAuthorByUsername(postCommandDto.getAuthorUsername()))")
    @Mapping(target = "timestamp", ignore = true)
    public abstract Post toEntity(PostCommandDto postCommandDto) throws NotFoundException;
}
