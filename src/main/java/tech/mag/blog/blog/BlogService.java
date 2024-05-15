package tech.mag.blog.blog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class BlogService {

    @Autowired
    private BlogRepository blogRepository;

    public List<Blog> getAllBlogs() {
        return blogRepository.findAll();
    }

    public Optional<Blog> getBlogById(UUID id) {
        return blogRepository.findById(id);
    }

    public String createBlog(Blog blog) {
        if (blogRepository.findByTitle(blog.getTitle()).isPresent()) {

            return "Blog with this title already exists";
        } else {
            Blog theBlog = blogRepository.save(blog);
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
