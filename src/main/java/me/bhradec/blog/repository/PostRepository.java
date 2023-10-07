package me.bhradec.blog.repository;

import me.bhradec.blog.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByAuthor_Id(Long authorId, Pageable pageable);
}
