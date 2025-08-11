package devlava.nudgeapi.dto;

// 포인트 등급 enum
public enum PointGrade {
    BRONZE("bronze", 0.0),
    SILVER("silver", 0.1), // 10% 추가
    GOLD("gold", 0.2),     // 20% 추가
    PLATINUM("platinum", 0.3); // 30% 추가

    private final String gradeName;
    private final double bonusRate;

    PointGrade(String gradeName, double bonusRate) {
        this.gradeName = gradeName;
        this.bonusRate = bonusRate;
    }

    public String getGradeName() {
        return gradeName;
    }

    public double getBonusRate() {
        return bonusRate;
    }

    public static PointGrade getGradeByNudgeCount(int nudgeCount) {
        if (nudgeCount <= 50) return BRONZE;
        else if (nudgeCount <= 100) return SILVER;
        else if (nudgeCount <= 150) return GOLD;
        else return PLATINUM;
    }
}