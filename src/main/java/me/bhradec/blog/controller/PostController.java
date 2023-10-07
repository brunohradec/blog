package me.bhradec.blog.controller;

import jakarta.validation.Valid;
import me.bhradec.blog.domain.AppUser;
import me.bhradec.blog.domain.Post;
import me.bhradec.blog.dto.PostCommandDto;
import me.bhradec.blog.dto.PostDto;
import me.bhradec.blog.exception.NotFoundException;
import me.bhradec.blog.mapper.PostMapper;
import me.bhradec.blog.service.AuthService;
import me.bhradec.blog.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;
    private final AuthService authService;
    private final PostMapper postMapper;

    public PostController(PostService postService, AuthService authService, PostMapper postMapper) {
        this.postService = postService;
        this.authService = authService;
        this.postMapper = postMapper;
    }

    @PostMapping
    public ResponseEntity<PostDto> save(@RequestBody @Valid PostCommandDto postCommandDto) {
        try {
            Post post = postMapper.toEntity(postCommandDto);
            Post savedProduct = postService.save(post);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(postMapper.toDto(savedProduct));
        } catch (NotFoundException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<Page<PostDto>> findAll(Pageable pageable) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(postService
                        .findAll(pageable)
                        .map(postMapper::toDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> findById(@PathVariable Long id) {
        return postService
                .findById(id)
                .map(postMapper::toDto)
                .map(productDto -> ResponseEntity.status(HttpStatus.OK).body(productDto))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post with the given id not found."));
    }

    @GetMapping(params = "authorUsername")
    public ResponseEntity<Page<PostDto>> findByAuthorUsername(@RequestParam String authorUsername, Pageable pageable) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(postService
                        .findByAuthorUsername(authorUsername, pageable)
                        .map(postMapper::toDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDto> updateById(@PathVariable Long id, @RequestBody @Valid PostCommandDto postCommandDto) {
        AppUser currentUser = authService.getCurrentUser()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Current user not found."));

        String postUsername = postService
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post with the id " + id + " not found"))
                .getAuthor()
                .getUsername();

        if (!currentUser.getUsername().equals(postUsername)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Current user is not the author of the post.");
        }

        Post post = postMapper.toEntity(postCommandDto);
        Post updatedPost = postService.updateById(id, post);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(postMapper.toDto(updatedPost));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        AppUser currentUser = authService.getCurrentUser()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Current user not found."));

        String postUsername = postService
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post with the id " + id + " not found"))
                .getAuthor()
                .getUsername();

        if (!currentUser.getUsername().equals(postUsername)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Current user is not the author of the post.");
        }

        postService.deleteById(id);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
