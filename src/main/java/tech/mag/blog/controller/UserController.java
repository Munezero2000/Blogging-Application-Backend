package tech.mag.blog.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import tech.mag.blog.entity.User;
import tech.mag.blog.services.UserService;

@RestController
@RequestMapping("/users")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user) {
        try {

            User theUser = userService.registerUser(user);
            // checking if the user object is not null
            if (theUser == null) {
                return new ResponseEntity<>("User with email: " + user.getEmail() + " already exists",
                        HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity<>(theUser, HttpStatus.OK);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable UUID id) {
        try {
            User user = userService.getUserById(id);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/")
    public ResponseEntity<?> updateUser(@RequestBody User user) {
        try {
            String feedback = userService.updateUser(user);
            if (feedback.equalsIgnoreCase("User updated successfully.")) {
                return new ResponseEntity<>(feedback, HttpStatus.OK);
            } else if (feedback.equalsIgnoreCase("User not found")) {
                return new ResponseEntity<>(feedback, HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity<>("Unkown error occured", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
        try {
            String feedback = userService.deleteUser(id);
            if (feedback.equalsIgnoreCase("User deleted successfully")) {
                return new ResponseEntity<>(feedback, HttpStatus.OK);
            } else if (feedback.equalsIgnoreCase("User not found")) {
                return new ResponseEntity<>(feedback, HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity<>("Unkown error occured", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
