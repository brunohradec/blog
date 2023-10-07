package me.bhradec.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.bhradec.blog.domain.enumerations.AppUserRole;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppUserDto {
    private String username;
    private String firstName;
    private String lastName;
    private AppUserRole role;
}
