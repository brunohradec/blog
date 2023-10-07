package me.bhradec.blog.service;

import me.bhradec.blog.domain.Post;
import me.bhradec.blog.exception.NotFoundException;
import me.bhradec.blog.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PostService {
    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Post save(Post post) {
        return postRepository.save(post);
    }

    public Optional<Post> findById(Long id) {
        return postRepository.findById(id);
    }

    public Page<Post> findByAuthorUsername(String username, Pageable pageable) {
        return postRepository.findByAuthor_Username(username, pageable);
    }

    public Page<Post> findAll(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    public Post updateById(Long id, Post updatedPost) {
        updatedPost.setId(id);
        return postRepository.save(updatedPost);
    }

    public void deleteById(Long id) throws NotFoundException {
        // Check if post with that ID exists
        Post post = postRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Post with the id " + id + " not found."));

        postRepository.delete(post);
    }
}
