package devlava.nudgeapi.repository.data;

import devlava.nudgeapi.entity.data.SuccessStory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SuccessStoryRepository extends JpaRepository<SuccessStory, Long> {

    // 성공한 사례만 조회 (고객 동의가 Y인 경우)
    List<SuccessStory> findByCustomerConsentYnOrderByCreatedAtDesc(String customerConsentYn);

    // 특정 마케팅 타입의 성공 사례 조회
    List<SuccessStory> findByMarketingTypeAndCustomerConsentYnOrderByCreatedAtDesc(String marketingType, String customerConsentYn);

    // 특정 상담원의 성공 사례 조회
    List<SuccessStory> findByConsultantNameAndCustomerConsentYnOrderByCreatedAtDesc(String consultantName, String customerConsentYn);

    // 최근 성공 사례 조회 (최신순)
    List<SuccessStory> findTop10ByCustomerConsentYnOrderByCreatedAtDesc(String customerConsentYn);
} 