package devlava.nudgeapi.controller;

import devlava.nudgeapi.entity.user.User;
import devlava.nudgeapi.service.UserService;
import devlava.nudgeapi.service.IntegratedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final IntegratedService integratedService;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User savedUser = userService.saveUser(user);
        return ResponseEntity.ok(savedUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.findUserById(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        Optional<User> user = userService.findUserByEmail(email);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}/with-data")
    public ResponseEntity<IntegratedService.UserWithData> getUserWithData(@PathVariable Long id) {
        Optional<IntegratedService.UserWithData> userWithData = integratedService.getUserWithData(id);
        return userWithData.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/with-data/{dataType}")
    public ResponseEntity<IntegratedService.UserWithData> getUserWithDataByType(
            @PathVariable Long id, @PathVariable String dataType) {
        Optional<IntegratedService.UserWithData> userWithData = integratedService.getUserWithDataByType(id, dataType);
        return userWithData.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/with-data")
    public ResponseEntity<Void> deleteUserAndData(@PathVariable Long id) {
        integratedService.deleteUserAndData(id);
        return ResponseEntity.ok().build();
    }
}