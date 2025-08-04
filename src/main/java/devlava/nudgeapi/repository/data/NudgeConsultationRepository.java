package devlava.nudgeapi.repository.data;

import devlava.nudgeapi.entity.data.NudgeConsultation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NudgeConsultationRepository extends JpaRepository<NudgeConsultation, Long> {

    // 특정 상담원의 상담 데이터 조회
    List<NudgeConsultation> findBySkid(String skid);

    // 특정 날짜의 상담 데이터 조회
    List<NudgeConsultation> findByConsultationDate(String consultationDate);

    // 특정 상담원의 특정 날짜 상담 데이터 조회
    List<NudgeConsultation> findBySkidAndConsultationDate(String skid, String consultationDate);

    // 넛지 성공한 상담 데이터 조회
    List<NudgeConsultation> findByNudgeYnAndCustomerConsentYn(String nudgeYn, String customerConsentYn);

    // 특정 마케팅 타입의 상담 데이터 조회
    List<NudgeConsultation> findByMarketingType(String marketingType);

    // 월별 통계를 위한 쿼리
    @Query("SELECT COUNT(n) FROM NudgeConsultation n WHERE n.skid = :skid AND n.consultationDate LIKE :monthPattern")
    Long countBySkidAndMonth(@Param("skid") String skid, @Param("monthPattern") String monthPattern);

    // 월별 넛지 성공 건수
    @Query("SELECT COUNT(n) FROM NudgeConsultation n WHERE n.skid = :skid AND n.consultationDate LIKE :monthPattern AND n.nudgeYn = 'Y' AND n.customerConsentYn = 'Y'")
    Long countNudgeSuccessBySkidAndMonth(@Param("skid") String skid, @Param("monthPattern") String monthPattern);

    // 월별 마케팅 타입별 성공 건수
    @Query("SELECT COUNT(n) FROM NudgeConsultation n WHERE n.skid = :skid AND n.consultationDate LIKE :monthPattern AND n.marketingType = :marketingType AND n.customerConsentYn = 'Y'")
    Long countBySkidAndMonthAndMarketingType(@Param("skid") String skid, @Param("monthPattern") String monthPattern,
            @Param("marketingType") String marketingType);

    // 전체 구성원의 넛지 통합 건수 조회 (어제 날짜 기준)
    @Query("SELECT n.skid, COUNT(n) as count FROM NudgeConsultation n WHERE n.consultationDate = :date AND n.nudgeYn = 'Y' AND n.customerConsentYn = 'Y' GROUP BY n.skid")
    List<Object[]> findNudgeSuccessCountByDateGroupBySkid(@Param("date") String date);

    // 전체 구성원 목록 조회
    @Query("SELECT DISTINCT n.skid FROM NudgeConsultation n")
    List<String> findAllSkids();

    // 특정 날짜의 넛지 성공 건수 조회
    @Query("SELECT COUNT(n) FROM NudgeConsultation n WHERE n.skid = :skid AND n.consultationDate = :date AND n.nudgeYn = 'Y' AND n.customerConsentYn = 'Y'")
    Long countNudgeSuccessBySkidAndDate(@Param("skid") String skid, @Param("date") String date);
}