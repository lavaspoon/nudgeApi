package devlava.nudgeapi.controller;

import devlava.nudgeapi.entity.data.UserData;
import devlava.nudgeapi.service.UserDataService;
import devlava.nudgeapi.service.IntegratedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user-data")
@RequiredArgsConstructor
public class UserDataController {

    private final UserDataService userDataService;
    private final IntegratedService integratedService;

    @PostMapping("/{userId}")
    public ResponseEntity<UserData> createUserData(@PathVariable Long userId, @RequestBody UserData userData) {
        Optional<UserData> savedUserData = integratedService.saveUserDataWithUserCheck(userId, userData);
        return savedUserData.map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserData> getUserDataById(@PathVariable Long id) {
        Optional<UserData> userData = userDataService.findUserDataById(id);
        return userData.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserData>> getUserDataByUserId(@PathVariable Long userId) {
        List<UserData> userDataList = userDataService.findUserDataByUserId(userId);
        return ResponseEntity.ok(userDataList);
    }

    @GetMapping("/user/{userId}/type/{dataType}")
    public ResponseEntity<List<UserData>> getUserDataByUserIdAndType(
            @PathVariable Long userId, @PathVariable String dataType) {
        List<UserData> userDataList = userDataService.findUserDataByUserIdAndDataType(userId, dataType);
        return ResponseEntity.ok(userDataList);
    }

    @GetMapping
    public ResponseEntity<List<UserData>> getAllUserData() {
        List<UserData> userDataList = userDataService.findAllUserData();
        return ResponseEntity.ok(userDataList);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserData(@PathVariable Long id) {
        userDataService.deleteUserData(id);
        return ResponseEntity.ok().build();
    }
}