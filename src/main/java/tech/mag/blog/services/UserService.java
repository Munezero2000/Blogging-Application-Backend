package tech.mag.blog.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tech.mag.blog.entity.User;
import tech.mag.blog.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // a function to get all users
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users;
    }

    // a function to get a user by id
    public User getUserById(UUID id) {

        Optional<User> user = userRepository.findById(id);

        User thUser = null;
        if (user.isPresent()) {
            thUser = user.get();
            return thUser;
        }
        return null;
    }

    // a function to save new user
    public User registerUser(User user) {
        Optional<User> theUser = userRepository.findByEmail(user.getEmail());
        if (theUser.isPresent()) {
            return null;
        } else {
            user = userRepository.save(user);
            return user;
        }
    }

    // a function to update user
    public String updateUser(User user) {
        Optional<User> optionalUser = userRepository.findById(user.getId());
        if (optionalUser.isPresent()) {
            userRepository.save(user);
            return "User updated successfully.";
        } else {
            return "User not found.";
        }
    }

    // a function to delete user
    public String deleteUser(UUID id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            userRepository.deleteById(id);
            return "User deleted successfully";
        } else {
            return "User not found.";
        }
    }

}
