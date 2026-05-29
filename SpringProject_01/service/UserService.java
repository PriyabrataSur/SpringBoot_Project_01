package in.sigma.SpringProject_01.service;

import in.sigma.SpringProject_01.dto.ErrorDto;
import in.sigma.SpringProject_01.dto.UserDto;
import in.sigma.SpringProject_01.entity.User;
import in.sigma.SpringProject_01.repository.UserRepo;
import in.sigma.SpringProject_01.response.ApiResponse;
import in.sigma.SpringProject_01.response.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    @Autowired
    private final UserRepo userRepo;
    @Autowired
    private final BCryptPasswordEncoder encoder;

//    public User register(User user) {
//
//        user.setPassword(encoder.encode(user.getPassword()));
//        return userRepo.save(user);
//    }

    public Object register(User user) {

        List<ErrorDto> errors = new ArrayList<>();

        User existingUser = userRepo.findByEmail(user.getEmail());

        if (existingUser != null) {
            errors.add(new ErrorDto(
                    "email",
                    "duplicate_email",
                    "An account with this email address already exists."
            ));
        }

        if (user.getPassword().length() < 12) {
            errors.add(new ErrorDto(
                    "password",
                    "weak_password",
                    "Password must be at least 12 characters long."
            ));
        }

        if (!errors.isEmpty()) {

            return new ErrorResponse(
                    "error",
                    "VALIDATION_ERROR",
                    "The request payload contains invalid fields.",
                    errors
            );
        }

        user.setPassword(encoder.encode(user.getPassword()));

        User savedUser = userRepo.save(user);

        UserDto dto = new UserDto();

        dto.setId(String.valueOf(savedUser.getId()));
        dto.setEmail(savedUser.getEmail());
        dto.setName(savedUser.getName());
        dto.setPhNo(savedUser.getPhNo());

        return new ApiResponse<>(
                "success",
                "User registered successfully.",
                dto
        );
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

//    public User updateUser(Integer id, User updatedUser) {
//
//        User user = userRepo.findById(id).orElseThrow();
//
//        user.setName(updatedUser.getName());
//        user.setEmail(updatedUser.getEmail());
//
//        return userRepo.save(user);
//    }
//
//    public void deleteUser(Integer id) {
//        userRepo.deleteById(id);
//    }

    public Object updateUser(Integer id, User updatedUser) {

        Optional<User> optionalUser = userRepo.findById(id);

        if (optionalUser.isEmpty()) {
            List<ErrorDto> errors = new ArrayList<>();
            errors.add(new ErrorDto(
                    "id",
                    "user_not_found",
                    "User not found with given id."
            ));
            return new ErrorResponse(
                    "error",
                    "UPDATE_FAILED",
                    "User update failed.",
                    errors
            );
        }

        User user = optionalUser.get();

        user.setName(updatedUser.getName());
        user.setEmail(updatedUser.getEmail());

        User savedUser = userRepo.save(user);

        return new ApiResponse<>(
                "success",
                "User updated successfully.",
                savedUser
        );
    }

    public Object deleteUser(Integer id) {

        Optional<User> optionalUser = userRepo.findById(id);

        // User Not Found
        if (optionalUser.isEmpty()) {

            List<ErrorDto> errors = new ArrayList<>();

            errors.add(new ErrorDto(
                    "id",
                    "user_not_found",
                    "User not found with given id."
            ));

            return new ErrorResponse(
                    "error",
                    "DELETE_FAILED",
                    "User deletion failed.",
                    errors
            );
        }

        userRepo.deleteById(id);

        return new ApiResponse<>(
                "success",
                "User deleted successfully.",
                null
        );
    }

    public User login(String email) {
        return userRepo.findByEmail(email);     // null needs to handle
    }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepo.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities("USER")
                .build();
    }
}
