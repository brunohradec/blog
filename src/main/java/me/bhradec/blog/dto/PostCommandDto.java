package me.bhradec.blog.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostCommandDto {
    @NotNull
    @Size(max = 255)
    private String title;

    private String content;

    @NotNull
    private String authorUsername;
}
