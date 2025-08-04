package devlava.nudgeapi.service;

import devlava.nudgeapi.entity.data.UserData;
import devlava.nudgeapi.repository.data.UserDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(transactionManager = "dataTransactionManager")
public class UserDataService {

    private final UserDataRepository userDataRepository;

    public UserData saveUserData(UserData userData) {
        return userDataRepository.save(userData);
    }

    public Optional<UserData> findUserDataById(Long id) {
        return userDataRepository.findById(id);
    }

    public List<UserData> findUserDataByUserId(Long userId) {
        return userDataRepository.findByUserId(userId);
    }

    public List<UserData> findUserDataByUserIdAndDataType(Long userId, String dataType) {
        return userDataRepository.findByUserIdAndDataType(userId, dataType);
    }

    public List<UserData> findAllUserData() {
        return userDataRepository.findAll();
    }

    public void deleteUserData(Long id) {
        userDataRepository.deleteById(id);
    }
}