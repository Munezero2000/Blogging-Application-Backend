package tech.mag.blog.blog;

import java.util.Optional;
import java.util.UUID;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import tech.mag.blog.user.User;

@Service
public class BlogService {

    @Autowired
    private BlogRepository blogRepository;

    public Page<Blog> getAllBlogs(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return blogRepository.findAll(pageable);
    }

    public Optional<Blog> getBlogById(UUID id) {
        return blogRepository.findById(id);
    }

    public String createBlog(Blog blog) {
        if (blogRepository.findByTitle(blog.getTitle()).isPresent()) {
            return "Blog with this title already exists";
        } else {
            blogRepository.save(blog);
            return "Blog created successfully";
        }
    }

    public Blog updateBlog(UUID id, Blog updatedBlog) {
        Optional<Blog> optionalBlog = blogRepository.findById(id);
        if (optionalBlog.isPresent()) {
            return blogRepository.save(updatedBlog);
        } else {
            throw new RuntimeException("Blog not found with id: " + id);
        }
    }

    public void deleteBlog(UUID id) {
        blogRepository.deleteById(id);
    }
}
