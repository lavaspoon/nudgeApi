package devlava.nudgeapi.service;

import devlava.nudgeapi.entity.user.User;
import devlava.nudgeapi.entity.data.UserData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IntegratedService {

    private final UserService userService;
    private final UserDataService userDataService;

    /**
     * 유저와 유저 데이터를 함께 조회
     */
    public Optional<UserWithData> getUserWithData(Long userId) {
        Optional<User> user = userService.findUserById(userId);
        if (user.isPresent()) {
            List<UserData> userDataList = userDataService.findUserDataByUserId(userId);
            return Optional.of(new UserWithData(user.get(), userDataList));
        }
        return Optional.empty();
    }

    /**
     * 유저와 특정 타입의 데이터를 함께 조회
     */
    public Optional<UserWithData> getUserWithDataByType(Long userId, String dataType) {
        Optional<User> user = userService.findUserById(userId);
        if (user.isPresent()) {
            List<UserData> userDataList = userDataService.findUserDataByUserIdAndDataType(userId, dataType);
            return Optional.of(new UserWithData(user.get(), userDataList));
        }
        return Optional.empty();
    }

    /**
     * 유저 데이터 저장 (유저가 존재하는지 확인 후)
     */
    @Transactional(transactionManager = "dataTransactionManager")
    public Optional<UserData> saveUserDataWithUserCheck(Long userId, UserData userData) {
        // 유저가 존재하는지 확인
        if (userService.findUserById(userId).isPresent()) {
            userData.setUserId(userId);
            return Optional.of(userDataService.saveUserData(userData));
        }
        return Optional.empty();
    }

    /**
     * 유저와 데이터를 함께 삭제
     */
    public void deleteUserAndData(Long userId) {
        // 먼저 유저 데이터 삭제
        List<UserData> userDataList = userDataService.findUserDataByUserId(userId);
        for (UserData userData : userDataList) {
            userDataService.deleteUserData(userData.getId());
        }

        // 그 다음 유저 삭제
        userService.deleteUser(userId);
    }

    /**
     * 유저와 데이터를 포함하는 DTO 클래스
     */
    public static class UserWithData {
        private User user;
        private List<UserData> userDataList;

        public UserWithData(User user, List<UserData> userDataList) {
            this.user = user;
            this.userDataList = userDataList;
        }

        // Getters and Setters
        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public List<UserData> getUserDataList() {
            return userDataList;
        }

        public void setUserDataList(List<UserData> userDataList) {
            this.userDataList = userDataList;
        }
    }
}