package tech.mag.blog.blog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import tech.mag.blog.user.User;
import tech.mag.blog.util.EBlogCategory;

@RestController
@RequestMapping("/api/blogs")
public class BlogController {

    @Autowired
    private BlogService blogService;

    @Value("${profile.picture.upload.directory}")
    private String uploadDirectory;

    @GetMapping(value = "/all", produces = {
            MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<?> getAllblogs() {
        List<Blog> blogs = blogService.getAllBlogs();
        Map<String, List> response = new HashMap<>();
        response.put("blogs", blogs);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/{id}", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<?> getAllblogs(@PathVariable UUID id) {
        try {
            Blog blog;
            if (blogService.getBlogById(id).isPresent()) {
                blog = blogService.getBlogById(id).get();
                return ResponseEntity.ok(blog);
            } else {
                return new ResponseEntity<>("Blog not found", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.getMessage();
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping(value = "/create", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> createBlog(
            @Valid @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("category") String category,
            @RequestParam("readTime") String readTime,
            @RequestParam(value = "blogThumbnail", required = true) MultipartFile image) throws IOException {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User author = (User) authentication.getPrincipal();

            String blogThumbnail = System.currentTimeMillis() + image.getOriginalFilename();

            // Create the upload directory if it doesn't exist
            File directory = new File(uploadDirectory);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Save the file to the specified directory
            String filePath = uploadDirectory + blogThumbnail;
            FileOutputStream fos = new FileOutputStream(filePath);
            fos.write(image.getBytes());
            fos.close();

            Blog blog = new Blog();
            blog.setTitle(title);
            blog.setContent(content);
            blog.setBlogThumbnail(blogThumbnail);
            blog.setCategory(EBlogCategory.valueOf(category));
            blog.setReadTime(readTime);
            blog.setAuthor(author);

            String feedback = blogService.createBlog(blog);
            if (feedback.equalsIgnoreCase("Blog with this title already exists")) {
                return new ResponseEntity<>(feedback, HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity<>(feedback, HttpStatus.OK);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
