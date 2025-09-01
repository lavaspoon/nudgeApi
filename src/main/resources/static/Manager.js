import React, { useState, useEffect } from 'react';
import { TrendingUp, TrendingDown, Minus, Star, MessageSquare, Award, Zap, Users, BarChart3, Trophy, Target, Sparkles, ChevronUp, User, Crown, Download, Calendar } from 'lucide-react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, BarChart, Bar, ComposedChart, Legend } from 'recharts';
import './Manager.css';

const Manager = () => {
    const [apiData, setApiData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [sortConfig, setSortConfig] = useState({
        key: null,
        direction: 'asc'
    });
    const [selectedTeamForMembers, setSelectedTeamForMembers] = useState(null);
    const [selectedUserDetail, setSelectedUserDetail] = useState(null);
    const [userDetailData, setUserDetailData] = useState(null);
    const [showUserModal, setShowUserModal] = useState(false);
    const [expandedDays, setExpandedDays] = useState(new Set());
    const [chartFilter, setChartFilter] = useState('work'); // 'work' 또는 'rate'
    const [isAnimating, setIsAnimating] = useState(false);
    const [selectedMonth, setSelectedMonth] = useState('');
    const [excelLoading, setExcelLoading] = useState(false);

    // 현재 년도와 월을 기준으로 월 선택 옵션 생성
    const generateMonthOptions = () => {
        const options = [];
        const currentDate = new Date();
        const currentYear = currentDate.getFullYear();
        const currentMonth = currentDate.getMonth() + 1;

        // 올해 1월부터 현재 달까지 생성
        for (let month = 1; month <= currentMonth; month++) {
            const monthStr = month.toString().padStart(2, '0');
            const yearStr = currentYear.toString();
            const value = `${yearStr}-${monthStr}`;
            const label = `${yearStr}년 ${monthStr}월`;

            options.push({ value, label });
        }

        return options;
    };

    const monthOptions = generateMonthOptions();

    // 엑셀 다운로드 함수
    const downloadExcel = async () => {
        if (!selectedMonth) {
            alert('월을 선택해주세요.');
            return;
        }

        setExcelLoading(true);
        try {
            const response = await fetch(`http://localhost:8080/api/excel/statistics/${selectedMonth}`);

            if (!response.ok) {
                throw new Error('엑셀 데이터를 가져오는데 실패했습니다.');
            }

            const data = await response.json();

            // CSV 파일 생성 및 다운로드
            const csvContent = generateExcelContent(data);
            const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });

            const url = window.URL.createObjectURL(blob);
            const link = document.createElement('a');
            link.href = url;
            link.download = `통계데이터_${selectedMonth}.csv`;
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            window.URL.revokeObjectURL(url);

            alert('통계 데이터 파일이 다운로드되었습니다.');
        } catch (error) {
            console.error('파일 다운로드 실패:', error);
            alert('파일 다운로드에 실패했습니다. 서버 연결을 확인해주세요.');
        } finally {
            setExcelLoading(false);
        }
    };

    // 엑셀 내용 생성 함수 (간단한 CSV 형태로 생성)
    const generateExcelContent = (data) => {
        let content = '\uFEFF'; // UTF-8 BOM

        // 부서별 통계 헤더
        content += '부서별 통계\n';
        content += '부서명,총건수,넛지건수,긍정건수,GIGA건수,CRM건수,TDS건수,넛지율,긍정율,GIGA율,CRM율,TDS율\n';

        // 부서별 통계 데이터
        if (data.deptStatistics) {
            data.deptStatistics.forEach(dept => {
                content += `${dept.deptName},${dept.totalCount},${dept.nudgeCount},${dept.positiveCount},${dept.gigaCount},${dept.crmCount},${dept.tdsCount},${dept.nudgeRate?.toFixed(2) || 0},${dept.positiveRate?.toFixed(2) || 0},${dept.gigaRate?.toFixed(2) || 0},${dept.crmRate?.toFixed(2) || 0},${dept.tdsRate?.toFixed(2) || 0}\n`;
            });
        }

        content += '\n';

        // 구성원별 통계 헤더
        content += '구성원별 통계\n';
        content += '부서명,사용자ID,이름,총건수,넛지건수,긍정건수,GIGA건수,CRM건수,TDS건수,넛지율,긍정율,GIGA율,CRM율,TDS율\n';

        // 구성원별 통계 데이터
        if (data.memberStatistics) {
            data.memberStatistics.forEach(member => {
                content += `${member.deptName},${member.userId},${member.mbName},${member.totalCount},${member.nudgeCount},${member.positiveCount},${member.gigaCount},${member.crmCount},${member.tdsCount},${member.nudgeRate?.toFixed(2) || 0},${member.positiveRate?.toFixed(2) || 0},${member.gigaRate?.toFixed(2) || 0},${member.crmRate?.toFixed(2) || 0},${member.tdsRate?.toFixed(2) || 0}\n`;
            });
        }

        return content;
    };

    // API 데이터 가져오기
    useEffect(() => {
        const fetchData = async () => {
            try {
                const response = await fetch('http://localhost:8080/api/admin/dashboard/center001'); //csm1_chief01
                const result = await response.json();
                if (result.result) {
                    setApiData(result.data);
                }
            } catch (error) {
                console.error('API 데이터 가져오기 실패:', error);
                // 에러 시 더미 데이터 사용
                setApiData({
                    deptStats: [
                        {
                            deptIdx: 4,
                            deptName: "CS 마케팅 1실",
                            totalMembers: 6,
                            totalCount: 14,
                            totalNudgeCount: 10,
                            totalSuccessCount: 8,
                            nudgeSuccessRate: 80.0,
                            monthlyComparison: {
                                prevMonthNudgeCount: 0,
                                currentMonthNudgeCount: 10,
                                nudgeCountDiff: 10,
                                nudgeCountChangeRate: 0.00,
                                prevMonthSuccessRate: 0.00,
                                currentMonthSuccessRate: 80.00,
                                successRateDiff: 80.00,
                                successRateChangeRate: 0.00,
                                prevMonthAverageNudgeRate: 83.33,
                                currentMonthAverageNudgeRate: 82.98,
                                averageNudgeRateDiff: -0.35,
                                averageNudgeRateChangeRate: -0.43,
                                nudgeCountTrend: "UP",
                                successRateTrend: "UP",
                                averageNudgeRateTrend: "DOWN"
                            },
                            userStats: []
                        },
                        {
                            deptIdx: 5,
                            deptName: "CS 마케팅 2실",
                            totalMembers: 6,
                            totalCount: 14,
                            totalNudgeCount: 12,
                            totalSuccessCount: 8,
                            nudgeSuccessRate: 66.7,
                            monthlyComparison: {
                                prevMonthNudgeCount: 8,
                                currentMonthNudgeCount: 12,
                                nudgeCountDiff: 4,
                                nudgeCountChangeRate: 50.00,
                                prevMonthSuccessRate: 75.00,
                                currentMonthSuccessRate: 66.70,
                                successRateDiff: -8.30,
                                successRateChangeRate: -11.07,
                                prevMonthAverageNudgeRate: 85.50,
                                currentMonthAverageNudgeRate: 87.20,
                                averageNudgeRateDiff: 1.70,
                                averageNudgeRateChangeRate: 1.99,
                                nudgeCountTrend: "UP",
                                successRateTrend: "DOWN",
                                averageNudgeRateTrend: "UP"
                            },
                            userStats: []
                        }
                    ],
                    rankings: {
                        nudgeRanking: [],
                        gigaRanking: [],
                        tdsRanking: [],
                        crmRanking: []
                    },
                    deptMonthlyStats: []
                });
            } finally {
                setLoading(false);
            }
        };

        fetchData();

        // 컴포넌트 언마운트 시 body 스크롤 복원
        return () => {
            document.body.style.overflow = 'auto';
        };
    }, []);

    // 전체 통계 계산
    const totalStats = apiData ? {
        totalMembers: apiData.deptStats.reduce((sum, dept) => sum + dept.totalMembers, 0),
        totalNudgeCount: apiData.deptStats.reduce((sum, dept) => sum + dept.totalNudgeCount, 0),
        totalSuccessCount: apiData.deptStats.reduce((sum, dept) => sum + dept.totalSuccessCount, 0),
        averageRate: apiData.deptStats.length > 0
            ? (apiData.deptStats.reduce((sum, dept) => sum + dept.totalNudgeCount, 0) /
                apiData.deptStats.reduce((sum, dept) => sum + dept.totalCount, 0) * 100).toFixed(1)
            : 0
    } : { totalMembers: 0, totalNudgeCount: 0, totalSuccessCount: 0, averageRate: 0 };

    // 평균 넛지 성공률 계산 (API에서 제공하는 값 사용)
    const averageSuccessRate = apiData ?
        (apiData.deptStats.reduce((sum, dept) => sum + (dept.nudgeSuccessRate || 0), 0) /
            apiData.deptStats.length).toFixed(1) : 0;

    // 넛지 건수에 따른 등급 결정
    function getGradeByNudgeCount(nudgeCount) {
        if (nudgeCount >= 15) return '플래티넘';
        if (nudgeCount >= 10) return '골드';
        if (nudgeCount >= 5) return '실버';
        return '브론즈';
    }

    // 정렬 함수
    const handleSort = (key) => {
        let direction = 'asc';
        if (sortConfig.key === key && sortConfig.direction === 'asc') {
            direction = 'desc';
        }
        setSortConfig({ key, direction });
    };

    // 정렬된 부서 데이터
    const sortedDeptStats = apiData?.deptStats ? [...apiData.deptStats].sort((a, b) => {
        if (!sortConfig.key) return 0;

        let aValue, bValue;

        switch (sortConfig.key) {
            case 'deptName':
                aValue = a.deptName;
                bValue = b.deptName;
                break;
            case 'totalMembers':
                aValue = a.totalMembers;
                bValue = b.totalMembers;
                break;
            case 'totalNudgeCount':
                aValue = a.totalNudgeCount;
                bValue = b.totalNudgeCount;
                break;
            case 'avgPerPerson':
                aValue = a.totalMembers > 0 ? a.totalNudgeCount / a.totalMembers : 0;
                bValue = b.totalMembers > 0 ? b.totalNudgeCount / b.totalMembers : 0;
                break;
            case 'nudgeRate':
                aValue = a.totalCount > 0 ? a.totalNudgeCount / a.totalCount : 0;
                bValue = b.totalCount > 0 ? b.totalNudgeCount / b.totalCount : 0;
                break;
            case 'successRate':
                aValue = a.totalNudgeCount > 0 ? a.totalSuccessCount / a.totalNudgeCount : 0;
                bValue = b.totalNudgeCount > 0 ? b.totalSuccessCount / b.totalNudgeCount : 0;
                break;
            default:
                return 0;
        }

        if (typeof aValue === 'string') {
            aValue = aValue.toLowerCase();
            bValue = bValue.toLowerCase();
        }

        if (aValue < bValue) {
            return sortConfig.direction === 'asc' ? -1 : 1;
        }
        if (aValue > bValue) {
            return sortConfig.direction === 'asc' ? 1 : -1;
        }
        return 0;
    }) : [];

    // 선택된 팀의 월별 차트 데이터 처리
    const getMonthlyChartData = () => {
        if (!selectedTeamForMembers || !apiData?.deptMonthlyStats) return [];

        const selectedDept = apiData.deptMonthlyStats.find(dept => dept.deptIdx.toString() === selectedTeamForMembers);
        if (!selectedDept?.monthlyStats) return [];

        // 1월부터 12월까지 기본 데이터 생성
        const allMonths = Array.from({ length: 12 }, (_, i) => ({
            month: `${String(i + 1).padStart(2, '0')}월`,
            gigaCount: 0,
            tdsCount: 0,
            crmCount: 0
        }));

        // 실제 데이터로 업데이트
        selectedDept.monthlyStats.forEach(stat => {
            const monthIndex = parseInt(stat.month.substring(4, 6)) - 1; // 1-based to 0-based
            if (monthIndex >= 0 && monthIndex < 12) {
                allMonths[monthIndex] = {
                    month: `${String(monthIndex + 1).padStart(2, '0')}월`,
                    gigaCount: stat.gigaCount,
                    tdsCount: stat.tdsCount,
                    crmCount: stat.crmCount
                };
            }
        });

        return allMonths;
    };

    // 월별 넛지율 및 긍정율 차트 데이터 처리
    const getMonthlyRateChartData = () => {
        if (!selectedTeamForMembers || !apiData?.deptMonthlyStats) return [];

        const selectedDept = apiData.deptMonthlyStats.find(dept => dept.deptIdx.toString() === selectedTeamForMembers);
        if (!selectedDept?.monthlyStats) return [];

        // 1월부터 12월까지 기본 데이터 생성
        const allMonths = Array.from({ length: 12 }, (_, i) => ({
            month: `${String(i + 1).padStart(2, '0')}월`,
            nudgeRate: 0,
            positiveRate: 0
        }));

        // 실제 데이터로 업데이트
        selectedDept.monthlyStats.forEach(stat => {
            const monthIndex = parseInt(stat.month.substring(4, 6)) - 1;
            if (monthIndex >= 0 && monthIndex < 12) {
                // 실제 API 데이터를 기반으로 넛지율과 긍정율 계산
                const nudgeRate = stat.totalCount > 0 ? (stat.nudgeCount / stat.totalCount * 100) : 0;
                const positiveRate = stat.nudgeCount > 0 ? (stat.successCount / stat.nudgeCount * 100) : 0;

                allMonths[monthIndex] = {
                    month: `${String(monthIndex + 1).padStart(2, '0')}월`,
                    nudgeRate: Math.round(nudgeRate * 10) / 10, // 소수점 첫째자리까지
                    positiveRate: Math.round(positiveRate * 10) / 10 // 소수점 첫째자리까지
                };
            }
        });

        return allMonths;
    };

    // 사용자 상세 정보 가져오기
    const fetchUserDetail = async (userId) => {
        try {
            const response = await fetch(`http://localhost:8080/api/admin/user-detail/${userId}`);
            const result = await response.json();
            if (result.result) {
                setUserDetailData(result.data);
                setShowUserModal(true);
                // 모달 열 때 body 스크롤 방지
                document.body.style.overflow = 'hidden';
            }
        } catch (error) {
            console.error('사용자 상세 정보 가져오기 실패:', error);
        }
    };

    // 사용자 카드 클릭 핸들러
    const handleUserCardClick = (userId) => {
        setSelectedUserDetail(userId);
        fetchUserDetail(userId);
        setExpandedDays(new Set()); // 모달 열 때 모든 날짜 접기
    };

    // 날짜별 펼치기/접기 토글
    const toggleDayExpansion = (dayIndex) => {
        const newExpandedDays = new Set(expandedDays);
        if (newExpandedDays.has(dayIndex)) {
            newExpandedDays.delete(dayIndex);
        } else {
            newExpandedDays.add(dayIndex);
        }
        setExpandedDays(newExpandedDays);
    };

    // 모달 닫기 함수
    const closeModal = () => {
        setShowUserModal(false);
        setUserDetailData(null);
        // 모달 닫을 때 body 스크롤 복원
        document.body.style.overflow = 'auto';
    };

    const getGrowthIcon = (growth) => {
        if (growth.includes('+')) return <TrendingUp className="growth-icon up" />;
        if (growth.includes('-')) return <TrendingDown className="growth-icon down" />;
        return <Minus className="growth-icon neutral" />;
    };

    // 증감 표시를 위한 헬퍼 함수들
    const getGrowthDisplay = (diff, trend) => {
        if (diff === 0) return { text: '0', icon: <Minus className="growth-icon neutral" />, className: 'neutral' };
        const isPositive = diff > 0;
        const sign = isPositive ? '+' : '';
        return {
            text: `${sign}${diff}`,
            icon: isPositive ? <TrendingUp className="growth-icon up" /> : <TrendingDown className="growth-icon down" />,
            className: isPositive ? 'up' : 'down'
        };
    };

    const getPercentageGrowthDisplay = (diff, trend) => {
        if (diff === 0) return { text: '0%', icon: <Minus className="growth-icon neutral" />, className: 'neutral' };
        const isPositive = diff > 0;
        const sign = isPositive ? '+' : '';
        return {
            text: `${sign}${diff.toFixed(1)}%`,
            icon: isPositive ? <TrendingUp className="growth-icon up" /> : <TrendingDown className="growth-icon down" />,
            className: isPositive ? 'up' : 'down'
        };
    };

    // 툴팁 텍스트 생성 함수
    const getTooltipText = (type, diff, prevValue, currentValue) => {
        const diffText = diff > 0 ? `+${diff}` : diff < 0 ? `${diff}` : '0';
        const diffPercent = type.includes('Rate') ? '%' : '건';

        return `전월: ${prevValue}${diffPercent} → 이번월: ${currentValue}${diffPercent}\n변화: ${diffText}${diffPercent}`;
    };

    // 전체 부서의 월별 비교 데이터 계산
    const getOverallMonthlyComparison = () => {
        if (!apiData?.deptStats) return null;

        const totalPrevNudgeCount = apiData.deptStats.reduce((sum, dept) =>
            sum + (dept.monthlyComparison?.prevMonthNudgeCount || 0), 0);
        const totalCurrentNudgeCount = apiData.deptStats.reduce((sum, dept) =>
            sum + (dept.monthlyComparison?.currentMonthNudgeCount || 0), 0);

        const totalPrevSuccessRate = apiData.deptStats.reduce((sum, dept) =>
            sum + (dept.monthlyComparison?.prevMonthSuccessRate || 0), 0) / apiData.deptStats.length;
        const totalCurrentSuccessRate = apiData.deptStats.reduce((sum, dept) =>
            sum + (dept.monthlyComparison?.currentMonthSuccessRate || 0), 0) / apiData.deptStats.length;

        const totalPrevAverageNudgeRate = apiData.deptStats.reduce((sum, dept) =>
            sum + (dept.monthlyComparison?.prevMonthAverageNudgeRate || 0), 0) / apiData.deptStats.length;
        const totalCurrentAverageNudgeRate = apiData.deptStats.reduce((sum, dept) =>
            sum + (dept.monthlyComparison?.currentMonthAverageNudgeRate || 0), 0) / apiData.deptStats.length;

        const nudgeCountDiff = totalCurrentNudgeCount - totalPrevNudgeCount;
        const successRateDiff = totalCurrentSuccessRate - totalPrevSuccessRate;
        const averageNudgeRateDiff = totalCurrentAverageNudgeRate - totalPrevAverageNudgeRate;

        return {
            nudgeCountDiff,
            successRateDiff,
            averageNudgeRateDiff,
            nudgeCountTrend: nudgeCountDiff > 0 ? 'UP' : nudgeCountDiff < 0 ? 'DOWN' : 'NEUTRAL',
            successRateTrend: successRateDiff > 0 ? 'UP' : successRateDiff < 0 ? 'DOWN' : 'NEUTRAL',
            averageNudgeRateTrend: averageNudgeRateDiff > 0 ? 'UP' : averageNudgeRateDiff < 0 ? 'DOWN' : 'NEUTRAL'
        };
    };

    const getGradeIcon = (grade) => {
        switch (grade) {
            case '플래티넘': return '💎';
            case '골드': return '🥇';
            case '실버': return '🥈';
            case '브론즈': return '🥉';
            default: return '⭐';
        }
    };

    if (loading) {
        return (
            <div className="manager-dashboard">
                <div className="manager-container">
                    <div className="loading">데이터를 불러오는 중...</div>
                </div>
            </div>
        );
    }

    return (
        <div className="manager-dashboard">
            <div className="manager-container">
                {/* 헤더 */}
                <div className="manager-header">
                    <div className="header-content">
                        <div className="header-left">
                            <h1>팀 관리 대시보드</h1>
                            <p>전체 팀 구성원의 성과를 한눈에 확인하세요</p>
                        </div>
                        <div className="header-right">
                            <div className="manager-excel-download-section">
                                <div className="manager-month-selector">
                                    <Calendar className="manager-calendar-icon" />
                                    <select
                                        value={selectedMonth}
                                        onChange={(e) => setSelectedMonth(e.target.value)}
                                        className="manager-month-select"
                                    >
                                        <option value="">월 선택</option>
                                        {monthOptions.map((option) => (
                                            <option key={option.value} value={option.value}>
                                                {option.label}
                                            </option>
                                        ))}
                                    </select>
                                </div>
                                <button
                                    className={`manager-excel-download-btn ${excelLoading ? 'loading' : ''}`}
                                    onClick={downloadExcel}
                                    disabled={!selectedMonth || excelLoading}
                                >
                                    <Download className="manager-download-icon" />
                                    <span className="manager-btn-text">
                                        {excelLoading ? '다운로드 중...' : '통계 다운로드'}
                                    </span>
                                </button>
                            </div>
                        </div>
                    </div>
                </div>

                {/* 전체 실 현황 */}
                <section className="overview-section">
                    <div className="section-title">
                        <div className="title-indicator"></div>
                        <div>
                            <h2>전체 실 현황</h2>
                            <span className="section-subtitle">증감 표시는 전월 대비 변화율입니다</span>
                        </div>
                    </div>

                    <div className="overview-grid">
                        <div className="overview-card">
                            <div className="card-header">
                                <Users className="icon" />
                                <span>전체 구성원</span>
                            </div>
                            <div className="card-value">
                                {totalStats.totalMembers}명
                            </div>
                        </div>

                        <div className="overview-card">
                            <div className="card-header">
                                <Target className="icon" />
                                <span>이달 넛지 건수</span>
                            </div>
                            <div className="card-value">
                                {totalStats.totalNudgeCount}건
                                {getOverallMonthlyComparison() && (
                                    <div
                                        className={`growth-indicator ${getGrowthDisplay(getOverallMonthlyComparison().nudgeCountDiff, getOverallMonthlyComparison().nudgeCountTrend).className}`}
                                        data-tooltip="전월 대비 증감"
                                    >
                                        {getGrowthDisplay(getOverallMonthlyComparison().nudgeCountDiff, getOverallMonthlyComparison().nudgeCountTrend).icon}
                                        <span>{getGrowthDisplay(getOverallMonthlyComparison().nudgeCountDiff, getOverallMonthlyComparison().nudgeCountTrend).text}</span>
                                    </div>
                                )}
                            </div>
                        </div>

                        <div className="overview-card">
                            <div className="card-header">
                                <BarChart3 className="icon" />
                                <span>평균 넛지율</span>
                            </div>
                            <div className="card-value">
                                {totalStats.averageRate}%
                                {getOverallMonthlyComparison() && (
                                    <div
                                        className={`growth-indicator ${getPercentageGrowthDisplay(getOverallMonthlyComparison().averageNudgeRateDiff, getOverallMonthlyComparison().averageNudgeRateTrend).className}`}
                                        data-tooltip="전월 대비 증감"
                                    >
                                        {getPercentageGrowthDisplay(getOverallMonthlyComparison().averageNudgeRateDiff, getOverallMonthlyComparison().averageNudgeRateTrend).icon}
                                        <span>{getPercentageGrowthDisplay(getOverallMonthlyComparison().averageNudgeRateDiff, getOverallMonthlyComparison().averageNudgeRateTrend).text}</span>
                                    </div>
                                )}
                            </div>
                        </div>

                        <div className="overview-card">
                            <div className="card-header">
                                <Crown className="icon" />
                                <span>이달 넛지 성공율</span>
                            </div>
                            <div className="card-value">
                                {averageSuccessRate}%
                                {getOverallMonthlyComparison() && (
                                    <div
                                        className={`growth-indicator ${getPercentageGrowthDisplay(getOverallMonthlyComparison().successRateDiff, getOverallMonthlyComparison().successRateTrend).className}`}
                                        data-tooltip="전월 대비 증감"
                                    >
                                        {getPercentageGrowthDisplay(getOverallMonthlyComparison().successRateDiff, getOverallMonthlyComparison().successRateTrend).icon}
                                        <span>{getPercentageGrowthDisplay(getOverallMonthlyComparison().successRateDiff, getOverallMonthlyComparison().successRateTrend).text}</span>
                                    </div>
                                )}
                            </div>
                        </div>
                    </div>

                    {/* 랭킹 섹션 */}
                    <div className="rankings-grid">
                        {/* 넛지 랭킹 */}
                        <div className="ranking-card">
                            <div className="ranking-badge nudge-badge">넛지</div>
                            <div className="ranking-list">
                                {apiData?.rankings?.nudgeRanking?.slice(0, 3).map((user, index) => (
                                    <div key={user.userId} className="ranking-item">
                                        <div className="ranking-position">
                                            <span className="position-number">{index + 1}</span>
                                        </div>
                                        <div className="ranking-info">
                                            <div className="user-name">{user.userName}</div>
                                            <div className="user-dept">{user.deptName}</div>
                                        </div>
                                        <div className="ranking-score">
                                            {user.totalNudgeCount}건
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </div>

                        {/* GIGA 랭킹 */}
                        <div className="ranking-card">
                            <div className="ranking-badge giga-badge">GIGA</div>
                            <div className="ranking-list">
                                {apiData?.rankings?.gigaRanking?.slice(0, 3).map((user, index) => (
                                    <div key={user.userId} className="ranking-item">
                                        <div className="ranking-position">
                                            <span className="position-number">{index + 1}</span>
                                        </div>
                                        <div className="ranking-info">
                                            <div className="user-name">{user.userName}</div>
                                            <div className="user-dept">{user.deptName}</div>
                                        </div>
                                        <div className="ranking-score">
                                            {user.totalNudgeCount}건
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </div>

                        {/* TDS 랭킹 */}
                        <div className="ranking-card">
                            <div className="ranking-badge tds-badge">TDS</div>
                            <div className="ranking-list">
                                {apiData?.rankings?.tdsRanking?.slice(0, 3).map((user, index) => (
                                    <div key={user.userId} className="ranking-item">
                                        <div className="ranking-position">
                                            <span className="position-number">{index + 1}</span>
                                        </div>
                                        <div className="ranking-info">
                                            <div className="user-name">{user.userName}</div>
                                            <div className="user-dept">{user.deptName}</div>
                                        </div>
                                        <div className="ranking-score">
                                            {user.totalNudgeCount}건
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </div>

                        {/* CRM 랭킹 */}
                        <div className="ranking-card">
                            <div className="ranking-badge crm-badge">CRM</div>
                            <div className="ranking-list">
                                {apiData?.rankings?.crmRanking?.slice(0, 3).map((user, index) => (
                                    <div key={user.userId} className="ranking-item">
                                        <div className="ranking-position">
                                            <span className="position-number">{index + 1}</span>
                                        </div>
                                        <div className="ranking-info">
                                            <div className="user-name">{user.userName}</div>
                                            <div className="user-dept">{user.deptName}</div>
                                        </div>
                                        <div className="ranking-score">
                                            {user.totalNudgeCount}건
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </div>
                    </div>
                </section>

                {/* 팀별 성과 */}
                <section className="team-performance-section">
                    <div className="section-title">
                        <div className="title-indicator"></div>
                        <div>
                            <h2>팀별 성과</h2>
                            <span className="section-subtitle">증감 표시는 전월 대비 변화율입니다</span>
                        </div>
                    </div>

                    <div className="team-performance-table">
                        <table>
                            <thead>
                                <tr>
                                    <th
                                        className={`sortable ${sortConfig.key === 'deptName' ? 'sorted' : ''}`}
                                        onClick={() => handleSort('deptName')}
                                    >
                                        팀명
                                        {sortConfig.key === 'deptName' && (
                                            <span className="sort-icon">
                                                {sortConfig.direction === 'asc' ? '↑' : '↓'}
                                            </span>
                                        )}
                                    </th>
                                    <th
                                        className={`sortable ${sortConfig.key === 'totalNudgeCount' ? 'sorted' : ''}`}
                                        onClick={() => handleSort('totalNudgeCount')}
                                    >
                                        이달 총 넛지건수
                                        {sortConfig.key === 'totalNudgeCount' && (
                                            <span className="sort-icon">
                                                {sortConfig.direction === 'asc' ? '↑' : '↓'}
                                            </span>
                                        )}
                                    </th>
                                    <th
                                        className={`sortable ${sortConfig.key === 'avgPerPerson' ? 'sorted' : ''}`}
                                        onClick={() => handleSort('avgPerPerson')}
                                    >
                                        인당 평균 건수
                                        {sortConfig.key === 'avgPerPerson' && (
                                            <span className="sort-icon">
                                                {sortConfig.direction === 'asc' ? '↑' : '↓'}
                                            </span>
                                        )}
                                    </th>
                                    <th
                                        className={`sortable ${sortConfig.key === 'nudgeRate' ? 'sorted' : ''}`}
                                        onClick={() => handleSort('nudgeRate')}
                                    >
                                        이달 평균 넛지율
                                        {sortConfig.key === 'nudgeRate' && (
                                            <span className="sort-icon">
                                                {sortConfig.direction === 'asc' ? '↑' : '↓'}
                                            </span>
                                        )}
                                    </th>
                                    <th
                                        className={`sortable ${sortConfig.key === 'successRate' ? 'sorted' : ''}`}
                                        onClick={() => handleSort('successRate')}
                                    >
                                        이달 평균 넛지성공률
                                        {sortConfig.key === 'successRate' && (
                                            <span className="sort-icon">
                                                {sortConfig.direction === 'asc' ? '↑' : '↓'}
                                            </span>
                                        )}
                                    </th>
                                </tr>
                            </thead>
                            <tbody>
                                {sortedDeptStats.map((dept) => (
                                    <tr
                                        key={dept.deptIdx}
                                        className={`team-row ${selectedTeamForMembers === dept.deptIdx.toString() ? 'selected' : ''}`}
                                        onClick={() => setSelectedTeamForMembers(dept.deptIdx.toString())}
                                    >
                                        <td className="team-name">
                                            {dept.deptName}
                                            <span className="member-badge">{dept.totalMembers}명</span>
                                        </td>
                                        <td className="total-nudge">
                                            {dept.totalNudgeCount}건
                                            {dept.monthlyComparison && (
                                                <div
                                                    className={`growth-indicator ${getGrowthDisplay(dept.monthlyComparison.nudgeCountDiff, dept.monthlyComparison.nudgeCountTrend).className}`}
                                                    data-tooltip={`전월: ${dept.monthlyComparison.prevMonthNudgeCount}건 → 이번월: ${dept.monthlyComparison.currentMonthNudgeCount}건`}
                                                >
                                                    {getGrowthDisplay(dept.monthlyComparison.nudgeCountDiff, dept.monthlyComparison.nudgeCountTrend).icon}
                                                    <span>{getGrowthDisplay(dept.monthlyComparison.nudgeCountDiff, dept.monthlyComparison.nudgeCountTrend).text}</span>
                                                </div>
                                            )}
                                        </td>
                                        <td className="avg-per-person">
                                            {dept.totalMembers > 0 ? (dept.totalNudgeCount / dept.totalMembers).toFixed(1) : 0}건
                                        </td>
                                        <td className="nudge-rate">
                                            {dept.totalCount > 0 ? (dept.totalNudgeCount / dept.totalCount * 100).toFixed(1) : 0}%
                                            {dept.monthlyComparison && (
                                                <div
                                                    className={`growth-indicator ${getPercentageGrowthDisplay(dept.monthlyComparison.averageNudgeRateDiff, dept.monthlyComparison.averageNudgeRateTrend).className}`}
                                                    data-tooltip={`전월: ${dept.monthlyComparison.prevMonthAverageNudgeRate?.toFixed(1) || 0}% → 이번월: ${dept.monthlyComparison.currentMonthAverageNudgeRate?.toFixed(1) || 0}%`}
                                                >
                                                    {getPercentageGrowthDisplay(dept.monthlyComparison.averageNudgeRateDiff, dept.monthlyComparison.averageNudgeRateTrend).icon}
                                                    <span>{getPercentageGrowthDisplay(dept.monthlyComparison.averageNudgeRateDiff, dept.monthlyComparison.averageNudgeRateTrend).text}</span>
                                                </div>
                                            )}
                                        </td>
                                        <td className="success-rate">
                                            {(dept.nudgeSuccessRate || 0).toFixed(1)}%
                                            {dept.monthlyComparison && (
                                                <div
                                                    className={`growth-indicator ${getPercentageGrowthDisplay(dept.monthlyComparison.successRateDiff, dept.monthlyComparison.successRateTrend).className}`}
                                                    data-tooltip={`전월: ${dept.monthlyComparison.prevMonthSuccessRate?.toFixed(1) || 0}% → 이번월: ${dept.monthlyComparison.currentMonthSuccessRate?.toFixed(1) || 0}%`}
                                                >
                                                    {getPercentageGrowthDisplay(dept.monthlyComparison.successRateDiff, dept.monthlyComparison.successRateTrend).icon}
                                                    <span>{getPercentageGrowthDisplay(dept.monthlyComparison.successRateDiff, dept.monthlyComparison.successRateTrend).text}</span>
                                                </div>
                                            )}
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </section>



                {/* 구성원 목록 */}
                <section className="members-section">
                    <div className="section-title">
                        <div className="title-indicator"></div>
                        <h2>구성원 상세 현황</h2>
                        {selectedTeamForMembers && (
                            <span className="member-count">
                                총 {apiData?.deptStats.find(dept => dept.deptIdx.toString() === selectedTeamForMembers)?.userStats.length || 0}명
                            </span>
                        )}
                    </div>

                    {!selectedTeamForMembers ? (
                        <div className="select-team-message">
                            <div className="message-content">
                                <Users className="message-icon" />
                                <h3>팀을 선택해주세요</h3>
                                <p>위의 팀별 성과 테이블에서 팀을 클릭하면 해당 팀의 구성원 목록을 확인할 수 있습니다.</p>
                            </div>
                        </div>
                    ) : (
                        <>
                            {/* 월별 차트 섹션 */}
                            <div className="monthly-chart-section">
                                <div className="chart-container">
                                    {/* 차트 헤더와 필터 버튼 */}
                                    <div className="chart-header">
                                        <h3 className="chart-title">
                                            {chartFilter === 'work' ? '월별 업무 현황' : '월별 넛지율 & 긍정율'}
                                        </h3>
                                        <div className={`chart-filters ${isAnimating ? 'animating' : ''}`}>
                                            <button
                                                className={`filter-btn ${chartFilter === 'work' ? 'active' : ''}`}
                                                onClick={() => {
                                                    setIsAnimating(true);
                                                    setTimeout(() => {
                                                        setChartFilter('work');
                                                        setIsAnimating(false);
                                                    }, 400);
                                                }}
                                            >
                                                업무 현황
                                            </button>
                                            <button
                                                className={`filter-btn ${chartFilter === 'rate' ? 'active' : ''}`}
                                                onClick={() => {
                                                    setIsAnimating(true);
                                                    setTimeout(() => {
                                                        setChartFilter('rate');
                                                        setIsAnimating(false);
                                                    }, 400);
                                                }}
                                            >
                                                넛지율 & 긍정율
                                            </button>
                                        </div>
                                    </div>

                                    {/* 차트 내용 */}
                                    <div className="chart-content">
                                        {chartFilter === 'work' ? (
                                            <ResponsiveContainer width="100%" height={320}>
                                                <ComposedChart data={getMonthlyChartData()} margin={{ top: 20, right: 30, left: 20, bottom: 40 }}>
                                                    <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" />
                                                    <XAxis
                                                        dataKey="month"
                                                        axisLine={false}
                                                        tickLine={false}
                                                        tick={{ fontSize: 12, fill: '#64748b' }}
                                                        tickMargin={10}
                                                    />
                                                    <YAxis
                                                        axisLine={false}
                                                        tickLine={false}
                                                        tick={{ fontSize: 12, fill: '#64748b' }}
                                                        tickMargin={10}
                                                        label={{ value: '건수', angle: -90, position: 'insideLeft', style: { textAnchor: 'middle', fill: '#64748b', fontSize: 12 } }}
                                                    />
                                                    <Tooltip
                                                        contentStyle={{
                                                            backgroundColor: 'rgba(255, 255, 255, 0.95)',
                                                            border: '1px solid #e2e8f0',
                                                            borderRadius: '8px',
                                                            boxShadow: '0 4px 12px rgba(0, 0, 0, 0.1)'
                                                        }}
                                                        labelStyle={{ color: '#1e293b', fontWeight: '600' }}
                                                    />
                                                    <Legend
                                                        verticalAlign="bottom"
                                                        height={36}
                                                        wrapperStyle={{
                                                            paddingTop: '20px'
                                                        }}
                                                        iconType="circle"
                                                        iconSize={8}
                                                    />
                                                    <Line
                                                        type="monotone"
                                                        dataKey="gigaCount"
                                                        stroke="#10b981"
                                                        strokeWidth={3}
                                                        name="GIGA"
                                                        dot={{ fill: '#10b981', strokeWidth: 2, r: 4 }}
                                                        activeDot={{ r: 6, stroke: '#10b981', strokeWidth: 2, fill: '#fff' }}
                                                    />
                                                    <Line
                                                        type="monotone"
                                                        dataKey="tdsCount"
                                                        stroke="#f59e0b"
                                                        strokeWidth={3}
                                                        name="TDS"
                                                        dot={{ fill: '#f59e0b', strokeWidth: 2, r: 4 }}
                                                        activeDot={{ r: 6, stroke: '#f59e0b', strokeWidth: 2, fill: '#fff' }}
                                                    />
                                                    <Line
                                                        type="monotone"
                                                        dataKey="crmCount"
                                                        stroke="#ef4444"
                                                        strokeWidth={3}
                                                        name="CRM"
                                                        dot={{ fill: '#ef4444', strokeWidth: 2, r: 4 }}
                                                        activeDot={{ r: 6, stroke: '#ef4444', strokeWidth: 2, fill: '#fff' }}
                                                    />
                                                </ComposedChart>
                                            </ResponsiveContainer>
                                        ) : (
                                            <ResponsiveContainer width="100%" height={320}>
                                                <ComposedChart data={getMonthlyRateChartData()} margin={{ top: 20, right: 30, left: 20, bottom: 40 }}>
                                                    <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" />
                                                    <XAxis
                                                        dataKey="month"
                                                        axisLine={false}
                                                        tickLine={false}
                                                        tick={{ fontSize: 12, fill: '#64748b' }}
                                                        tickMargin={10}
                                                    />
                                                    <YAxis
                                                        axisLine={false}
                                                        tickLine={false}
                                                        tick={{ fontSize: 12, fill: '#64748b' }}
                                                        tickMargin={10}
                                                        domain={[0, 100]}
                                                        label={{ value: '비율 (%)', angle: -90, position: 'insideLeft', style: { textAnchor: 'middle', fill: '#64748b', fontSize: 12 } }}
                                                    />
                                                    <Tooltip
                                                        contentStyle={{
                                                            backgroundColor: 'rgba(255, 255, 255, 0.95)',
                                                            border: '1px solid #e2e8f0',
                                                            borderRadius: '8px',
                                                            boxShadow: '0 4px 12px rgba(0, 0, 0, 0.1)'
                                                        }}
                                                        labelStyle={{ color: '#1e293b', fontWeight: '600' }}
                                                        formatter={(value, name) => {
                                                            const label = name === 'nudgeRate' ? '넛지율' : name === 'positiveRate' ? '긍정율' : name;
                                                            return [`${value.toFixed(1)}%`, label];
                                                        }}
                                                    />
                                                    <Legend
                                                        verticalAlign="bottom"
                                                        height={36}
                                                        wrapperStyle={{
                                                            paddingTop: '20px'
                                                        }}
                                                        iconType="circle"
                                                        iconSize={8}
                                                    />
                                                    <Line
                                                        type="monotone"
                                                        dataKey="nudgeRate"
                                                        stroke="#3b82f6"
                                                        strokeWidth={3}
                                                        name="넛지율"
                                                        dot={{ fill: '#3b82f6', strokeWidth: 2, r: 4 }}
                                                        activeDot={{ r: 6, stroke: '#3b82f6', strokeWidth: 2, fill: '#fff' }}
                                                    />
                                                    <Line
                                                        type="monotone"
                                                        dataKey="positiveRate"
                                                        stroke="#10b981"
                                                        strokeWidth={3}
                                                        name="긍정율"
                                                        dot={{ fill: '#10b981', strokeWidth: 2, r: 4 }}
                                                        activeDot={{ r: 6, stroke: '#10b981', strokeWidth: 2, fill: '#fff' }}
                                                    />
                                                </ComposedChart>
                                            </ResponsiveContainer>
                                        )}
                                    </div>
                                </div>
                            </div>

                            {/* 구성원 목록 */}
                            <div className="members-grid">
                                {apiData?.deptStats
                                    .find(dept => dept.deptIdx.toString() === selectedTeamForMembers)
                                    ?.userStats.map((user, index) => ({
                                        ...user,
                                        team: apiData.deptStats.find(dept => dept.deptIdx.toString() === selectedTeamForMembers)?.deptName,
                                        grade: getGradeByNudgeCount(user.nudgeCount),
                                        currentPoints: user.nudgeCount * 100,
                                        monthlyNudgeCount: user.nudgeCount,
                                        monthlyRate: user.totalCount > 0 ? (user.nudgeCount / user.totalCount * 100).toFixed(1) : 0,
                                        weeklyGrowth: '+12%',
                                        status: 'active'
                                    })).map((member, index) => (
                                        <div
                                            key={`${member.userId}-${index}`}
                                            className="member-card"
                                            onClick={() => handleUserCardClick(member.userId)}
                                        >
                                            <div className="member-header">
                                                <div className="member-info">
                                                    <div className="member-avatar">
                                                        <User className="icon" />
                                                    </div>
                                                    <div className="member-details">
                                                        <h3 className="member-name">{member.userName}</h3>
                                                        <span className="member-position">{member.mbPositionName}</span>
                                                    </div>
                                                </div>
                                                <div className="member-grade">
                                                    <span className="grade-badge bronze">브론즈</span>
                                                </div>
                                            </div>

                                            <div className="member-stats">
                                                <div className="member-stats-header">
                                                    <div className="monthly-indicator">이달 성과</div>
                                                </div>
                                                <div className="member-stats-grid">
                                                    <div className="stat-item">
                                                        <span className="stat-label">전체건수</span>
                                                        <span className="stat-value">{member.totalCount}건</span>
                                                    </div>
                                                    <div className="stat-item">
                                                        <span className="stat-label">넛지건수</span>
                                                        <span className="stat-value">{member.nudgeCount}건</span>
                                                    </div>
                                                    <div className="stat-item">
                                                        <span className="stat-label">넛지율</span>
                                                        <span className="stat-value">{member.monthlyRate}%</span>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    ))}
                            </div>
                        </>
                    )}
                </section>

                {/* 사용자 상세 정보 모달 */}
                {showUserModal && userDetailData && (
                    <div className="manager-modal-overlay" onClick={closeModal}>
                        <div className="manager-user-detail-modal" onClick={(e) => e.stopPropagation()}>
                            <div className="manager-modal-header">
                                <h2>구성원 상세 정보</h2>
                                <button
                                    className="manager-modal-close"
                                    onClick={closeModal}
                                >
                                    ×
                                </button>
                            </div>

                            <div className="manager-modal-content">
                                {/* 사용자 기본 정보 */}
                                <div className="manager-user-info-section">
                                    <div className="manager-user-avatar">
                                        <User className="icon" />
                                    </div>
                                    <div className="manager-user-details">
                                        <h3>{userDetailData.userName}</h3>
                                        <p>{userDetailData.mbPositionName} • {userDetailData.deptName}</p>
                                    </div>
                                </div>

                                {/* 요약 통계 */}
                                <div className="manager-summary-stats">
                                    <div className="manager-stat-card">
                                        <span className="manager-stat-label">이달 넛지율</span>
                                        <span className="manager-stat-value">{userDetailData.summary.totalNudgeRate.toFixed(1)}%</span>
                                    </div>
                                    <div className="manager-stat-card">
                                        <span className="manager-stat-label">GIGA 건수</span>
                                        <span className="manager-stat-value">{userDetailData.summary.totalGigaCount}건</span>
                                    </div>
                                    <div className="manager-stat-card">
                                        <span className="manager-stat-label">TDS 건수</span>
                                        <span className="manager-stat-value">{userDetailData.summary.totalTdsCount}건</span>
                                    </div>
                                    <div className="manager-stat-card">
                                        <span className="manager-stat-label">CRM 건수</span>
                                        <span className="manager-stat-value">{userDetailData.summary.totalCrmCount}건</span>
                                    </div>
                                    {/* 추가 통계 항목들 (테스트용) */}
                                    <div className="manager-stat-card">
                                        <span className="manager-stat-label">월간 목표</span>
                                        <span className="manager-stat-value">85%</span>
                                    </div>
                                    <div className="manager-stat-card">
                                        <span className="manager-stat-label">평균 응답시간</span>
                                        <span className="manager-stat-value">2.3분</span>
                                    </div>
                                    <div className="manager-stat-card">
                                        <span className="manager-stat-label">고객 만족도</span>
                                        <span className="manager-stat-value">4.8/5</span>
                                    </div>
                                    <div className="manager-stat-card">
                                        <span className="manager-stat-label">이번 주 성과</span>
                                        <span className="manager-stat-value">92%</span>
                                    </div>
                                </div>

                                {/* 최근 5일 활동 내역 */}
                                <div className="manager-recent-activity">
                                    <h4>최근 5일 활동 내역</h4>
                                    <div className="manager-activity-list">
                                        {userDetailData.dailyData.slice(0, 5).map((day, index) => (
                                            <div key={index} className="manager-activity-day">
                                                <div className="manager-day-header">
                                                    <div className="manager-day-info">
                                                        <span className="manager-day-date">
                                                            {day.date.substring(4, 6)}/{day.date.substring(6, 8)}
                                                        </span>
                                                        <span className="manager-day-stats">
                                                            총 {day.totalCount}건 (넛지 {day.nudgeCount}건)
                                                        </span>
                                                    </div>
                                                    {day.nudgeDetails.length > 0 && (
                                                        <button
                                                            className="manager-day-toggle-btn"
                                                            onClick={() => toggleDayExpansion(index)}
                                                        >
                                                            {expandedDays.has(index) ? '접기' : '펼치기'}
                                                        </button>
                                                    )}
                                                </div>
                                                {expandedDays.has(index) && day.nudgeDetails.length > 0 && (
                                                    <div className="manager-nudge-details">
                                                        {day.nudgeDetails.map((detail, detailIndex) => (
                                                            <div key={detailIndex} className="manager-nudge-item">
                                                                <div className="manager-nudge-header">
                                                                    <span className="manager-marketing-type">{detail.marketingType}</span>
                                                                    <span className={`manager-consent-status ${detail.customerConsentYn === 'Y' ? 'agreed' : 'declined'}`}>
                                                                        {detail.customerConsentYn === 'Y' ? '동의' : '거부'}
                                                                    </span>
                                                                </div>
                                                                <p className="manager-marketing-message">{detail.marketingMessage}</p>
                                                            </div>
                                                        ))}
                                                    </div>
                                                )}
                                            </div>
                                        ))}
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
};

export default Manager;