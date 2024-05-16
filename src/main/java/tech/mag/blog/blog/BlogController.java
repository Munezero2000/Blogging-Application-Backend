package tech.mag.blog.blog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    // controller for getting all the blogs
    @GetMapping(value = "/all", produces = {
            MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<?> getAllblogs() {
        List<Blog> blogs = blogService.getAllBlogs();
        Map<String, List> response = new HashMap<>();
        response.put("blogs", blogs);
        return ResponseEntity.ok(response);
    }

    // a controller for getting a single blog by id
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

    // a controller for creating a new blog with a unique title
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

    // a controller for updating blog information
    @PutMapping(value = "/update/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> updateBlog(
            @Valid @PathVariable UUID id, @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "readTime", required = false) String readTime,
            @RequestParam(value = "blogThumbnail", required = false) MultipartFile image) throws IOException {

        try {
            // Fetch the existing blog
            Optional<Blog> existingBlogOptional = blogService.getBlogById(id);
            if (!existingBlogOptional.isPresent()) {
                return new ResponseEntity<>("Blog not found", HttpStatus.NOT_FOUND);
            }

            Blog blog = existingBlogOptional.get();

            // Update fields only if they are provided
            if (title != null) {
                blog.setTitle(title);
            }
            if (content != null) {
                blog.setContent(content);
            }
            if (category != null) {
                blog.setCategory(EBlogCategory.valueOf(category));
            }
            if (readTime != null) {
                blog.setReadTime(readTime);
            }
            if (image != null && !image.isEmpty()) {
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

                blog.setBlogThumbnail(blogThumbnail);
            }

            // Save the updated blog
            Blog updatedBlog = blogService.updateBlog(id, blog);
            if (updatedBlog != null) {
                return new ResponseEntity<>("Blog updated successfully", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Blog not updated successfully", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // a controller for deleting blog by Id
    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<?> deleteBlog(@PathVariable UUID id) {
        try {
            if (blogService.getBlogById(id).isPresent()) {
                blogService.deleteBlog(id);
                return ResponseEntity.ok("Blog deleted successfully");
            } else {
                return new ResponseEntity<>("Blog not found", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.getMessage();
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

}
