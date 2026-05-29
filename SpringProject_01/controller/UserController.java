package in.sigma.SpringProject_01.controller;


import in.sigma.SpringProject_01.config.JwtConfig;
import in.sigma.SpringProject_01.dto.ErrorDto;
import in.sigma.SpringProject_01.dto.UserDto;
import in.sigma.SpringProject_01.dto.UserResponse;
import in.sigma.SpringProject_01.entity.User;
import in.sigma.SpringProject_01.repository.UserRepo;
import in.sigma.SpringProject_01.response.ApiResponse;
import in.sigma.SpringProject_01.response.ErrorResponse;
import in.sigma.SpringProject_01.service.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
//@RequestMapping("/")
public class UserController {

    @Autowired
    private final UserService service;
    @Autowired
    private final UserRepo repository;
    @Autowired
    private final BCryptPasswordEncoder encoder;
    @Autowired
    private final JwtConfig jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {

        Object response = service.register(user);

        if (response instanceof ErrorResponse) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDto request) {

        List<ErrorDto> errors = new ArrayList<>();

        User user = repository.findByEmail(request.getEmail());

        if (user == null) {

            errors.add(new ErrorDto(
                    "email",
                    "user_not_found",
                    "No account found with this email address."
            ));

            return new ResponseEntity<>(

                    new ErrorResponse(
                            "error",
                            "LOGIN_FAILED",
                            "Login request failed.",
                            errors
                    ),

                    HttpStatus.BAD_REQUEST
            );
        }

        // Invalid Password
        if (!encoder.matches(request.getPassword(), user.getPassword())) {

            errors.add(new ErrorDto(
                    "password",
                    "invalid_password",
                    "Incorrect password."
            ));

            return new ResponseEntity<>(

                    new ErrorResponse(
                            "error",
                            "LOGIN_FAILED",
                            "Login request failed.",
                            errors
                    ),

                    HttpStatus.BAD_REQUEST
            );
        }

        // Generate JWT Token
        String token = jwtUtil.generateToken(user.getEmail());

        Map<String, Object> data = new HashMap<>();

        data.put("token", token);
        data.put("email", user.getEmail());
        data.put("name", user.getName());

        ApiResponse<Map<String, Object>> response =

                new ApiResponse<>(
                        "success",
                        "Login successful.",
                        data
                );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/getUser")
    public ResponseEntity<?> getAllUsers() {

        List<User> users = service.getAllUsers();

        return new ResponseEntity<>(

                new ApiResponse<>(
                        "success",
                        "Users fetched successfully.",
                        users
                ),

                HttpStatus.OK
        );
    }

    @PutMapping("/updateUser/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Integer id,
            @RequestBody User updatedUser
    ) {

        Object response = service.updateUser(id, updatedUser);

        if (response instanceof ErrorResponse) {

            return new ResponseEntity<>(
                    response,
                    HttpStatus.BAD_REQUEST
            );
        }

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    @DeleteMapping("/deleteUser/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {

        Object response = service.deleteUser(id);

        if (response instanceof ErrorResponse) {

            return new ResponseEntity<>(
                    response,
                    HttpStatus.BAD_REQUEST
            );
        }

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }


//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody UserDto request) {
//
//        User user = repository.findByEmail(request.getEmail());     // null needs to handle
//
//        if (user == null) {
//            return ResponseEntity.badRequest().body("User not found");
//        }
//
//        if (!encoder.matches(request.getPassword(), user.getPassword())) {
//            return ResponseEntity.badRequest().body("Invalid password");
//        }
//
//        String token = jwtUtil.generateToken(user.getEmail());
//        return ResponseEntity.ok(new UserResponse(token));
//    }
//
//    @GetMapping("/getUser")
//    public List<User> getAllUsers() {
//        return service.getAllUsers();
//    }
//
//    @PutMapping("/updateUser/{id}")
//    public User updateUser(
//            @PathVariable Integer id,
//            @RequestBody User user
//    ) {
//        return service.updateUser(id, user);
//    }
//
//    @DeleteMapping("/deleteUser/{id}")
//    public String deleteUser(@PathVariable Integer id) {
//
//        service.deleteUser(id);
//        return "User deleted successfully";
//    }

}