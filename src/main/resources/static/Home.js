import React, { useState, useEffect, useMemo, memo } from 'react';
import { TrendingUp, TrendingDown, Minus, Star, MessageSquare, Award, Zap, Users, BarChart3, Trophy, Target, Sparkles, ChevronUp, ShoppingBag } from 'lucide-react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import Point from './Point';
import './Home.css';

// 차트 컴포넌트를 별도로 분리하여 메모이제이션
const MonthlyChartComponent = memo(({ data, xAxisInterval }) => (
    <div className="monthly-chart">
        <div className="chart-header">
            <span className="chart-title">한달 넛지 성공 건수</span>
            <span className="chart-trend">📊 총 {data.reduce((sum, item) => sum + item.count, 0)}건</span>
        </div>
        <div className="chart-container">
            <ResponsiveContainer width="100%" height={220}>
                <LineChart data={data} margin={{ left: 10, right: 10, top: 5, bottom: 5 }}>
                    <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
                    <XAxis
                        dataKey="date"
                        stroke="#6b7280"
                        fontSize={11}
                        tickLine={false}
                        axisLine={false}
                        interval={xAxisInterval}
                    />
                    <YAxis
                        stroke="#6b7280"
                        fontSize={11}
                        tickLine={false}
                        axisLine={false}
                        domain={[0, 10]}
                        ticks={[0, 2, 4, 6, 8, 10]}
                        width={30}
                    />
                    <Tooltip
                        contentStyle={{
                            backgroundColor: '#ffffff',
                            border: '1px solid #e5e7eb',
                            borderRadius: '8px',
                            boxShadow: '0 4px 12px rgba(0, 0, 0, 0.1)'
                        }}
                        labelStyle={{ color: '#374151', fontWeight: '600' }}
                    />
                    <Line
                        type="natural"
                        dataKey="count"
                        stroke="url(#lineGradient)"
                        strokeWidth={4}
                        dot={{
                            fill: '#3b82f6',
                            stroke: '#ffffff',
                            strokeWidth: 3,
                            r: 5,
                            filter: 'drop-shadow(0 2px 4px rgba(59, 130, 246, 0.3))'
                        }}
                        activeDot={{
                            r: 8,
                            stroke: '#3b82f6',
                            strokeWidth: 3,
                            fill: '#ffffff',
                            filter: 'drop-shadow(0 4px 8px rgba(59, 130, 246, 0.4))'
                        }}
                    />
                    <defs>
                        <linearGradient id="lineGradient" x1="0" y1="0" x2="1" y2="0">
                            <stop offset="0%" stopColor="#3b82f6" />
                            <stop offset="50%" stopColor="#60a5fa" />
                            <stop offset="100%" stopColor="#3b82f6" />
                        </linearGradient>
                    </defs>
                </LineChart>
            </ResponsiveContainer>
        </div>
    </div>
));

const Home = () => {
    const [activeTab, setActiveTab] = useState('earn');
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [feedbackTab, setFeedbackTab] = useState('my');
    const [hoveredGrade, setHoveredGrade] = useState(null); // 등급 호버 상태 추가

    // 포인트 상점 상태
    const [showStore, setShowStore] = useState(false);
    const [selectedCategory, setSelectedCategory] = useState('전체');
    const [selectedProduct, setSelectedProduct] = useState(null);
    const [showPurchaseModal, setShowPurchaseModal] = useState(false);
    const [purchaseQuantity, setPurchaseQuantity] = useState(1);

    // 포인트 데이터 상태 추가
    const [pointData, setPointData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // AI 분석 데이터 상태 추가
    const [aiAnalysisData, setAiAnalysisData] = useState({});
    const [analysisLoading, setAnalysisLoading] = useState({});
    const [analysisError, setAnalysisError] = useState({});

    const [animatedValues, setAnimatedValues] = useState({
        nudgeRate: 0,
        nudgeCount: 0,
        todayCount: 0,
        points: 0
    });

    // 상품 데이터
    const products = [
        {
            id: 1,
            name: "스타벅스 아메리카노",
            description: "깔끔하고 진한 아메리카노",
            price: 100,
            stock: 15,
            image: "☕",
            category: "음료"
        },
        {
            id: 2,
            name: "아이패드 프로 케이스",
            description: "프리미엄 실리콘 케이스",
            price: 500,
            stock: 8,
            image: "📱",
            category: "액세서리"
        },
        {
            id: 3,
            name: "에어팟 프로",
            description: "노이즈 캔슬링 무선 이어폰",
            price: 2000,
            stock: 3,
            image: "🎧",
            category: "전자기기"
        },
        {
            id: 4,
            name: "애플 워치 밴드",
            description: "스포츠 루프 밴드",
            price: 300,
            stock: 12,
            image: "⌚",
            category: "액세서리"
        },
        {
            id: 5,
            name: "맥북 에어 슬리브",
            description: "프리미엄 가죽 슬리브",
            price: 800,
            stock: 5,
            image: "💼",
            category: "액세서리"
        },
        {
            id: 6,
            name: "아이폰 충전기",
            description: "20W USB-C 충전기",
            price: 150,
            stock: 20,
            image: "🔌",
            category: "전자기기"
        }
    ];

    const categories = [...new Set(products.map(product => product.category))];
    const filteredProducts = selectedCategory === '전체'
        ? products
        : products.filter(product => product.category === selectedCategory);

    // 상점 관련 함수들
    const handleProductClick = (product) => {
        setSelectedProduct(product);
        setPurchaseQuantity(1);
        setShowPurchaseModal(true);
    };

    const handlePurchase = () => {
        const totalCost = selectedProduct.price * purchaseQuantity;
        const userPoints = animatedValues.points;

        if (totalCost > userPoints) {
            alert('포인트가 부족합니다!');
            return;
        }

        if (purchaseQuantity > selectedProduct.stock) {
            alert('재고가 부족합니다!');
            return;
        }

        // 구매 로직 (실제로는 API 호출)
        alert(`${selectedProduct.name} ${purchaseQuantity}개를 구매했습니다!`);
        setShowPurchaseModal(false);
        setSelectedProduct(null);
    };

    const handleCloseStore = () => {
        setShowStore(false);
        setSelectedProduct(null);
        setShowPurchaseModal(false);
        setPurchaseQuantity(1);
    };

    // 기존 데이터 (포인트 관련 제외)
    const data = useMemo(() => ({
        monthAnalyze: {
            totalCount: 500,
            nudgeCount: 20,
            nudgePercentage: 4.0,
            gourp1Count: 8,
            gourp2Count: 7,
            gourp3Count: 5
        },
        currentAnalyze: {
            totalCount: 25,
            nudgeCount: 3,
            nudgePercentage: 12.0,
            gourp1Count: 1,
            gourp2Count: 1,
            gourp3Count: 1,
            group1Growth: "+1",
            group2Growth: "0",
            group3Growth: "+1"
        },
        weeklyData: [
            { day: '월', count: 4 },
            { day: '화', count: 5 },
            { day: '수', count: 3 },
            { day: '목', count: 0 },
            { day: '금', count: 0 }
        ],
        monthlyData: [
            { date: '1일', count: 3 },
            { date: '2일', count: 5 },
            { date: '3일', count: 2 },
            { date: '4일', count: 6 },
            { date: '5일', count: 4 },
            { date: '6일', count: 7 },
            { date: '7일', count: 3 },
            { date: '8일', count: 5 },
            { date: '9일', count: 4 },
            { date: '10일', count: 6 },
            { date: '11일', count: 2 },
            { date: '12일', count: 5 },
            { date: '13일', count: 4 },
            { date: '14일', count: 7 },
            { date: '15일', count: 3 },
            { date: '16일', count: 6 },
            { date: '17일', count: 4 },
            { date: '18일', count: 5 },
            { date: '19일', count: 3 },
            { date: '20일', count: 6 },
            { date: '21일', count: 4 },
            { date: '22일', count: 5 },
            { date: '23일', count: 3 },
            { date: '24일', count: 7 },
            { date: '25일', count: 4 },
            { date: '26일', count: 6 },
            { date: '27일', count: 5 },
            { date: '28일', count: 3 },
            { date: '29일', count: 4 }
        ],
        curnetDatas: [
            {
                id: 101,
                consultationDate: "2025-07-29",
                marketingType: "GIGA 전환",
                marketingMessage: "GIGA 상품을 추천드립니다",
                customerConsentYn: "Y"
            },
            {
                id: 102,
                consultationDate: "2025-07-29",
                marketingType: "CRM 전환",
                marketingMessage: "CRM 시스템 전환을 제안드려요",
                customerConsentYn: "Y"
            }
        ],
        colleagueSuccessStories: [
            {
                id: 201,
                consultantName: "상담원 A",
                consultantLevel: "💎 플래티넘",
                marketingType: "GIGA 전환",
                marketingMessage: "고객님의 현재 요금제를 분석해보니 GIGA로 바꾸시면 월 2만원 절약하실 수 있어요. 데이터도 무제한으로 사용 가능하시고요!",
                customerConsentYn: "Y",
                bookmarked: false
            },
            {
                id: 202,
                consultantName: "상담원 B",
                consultantLevel: "🥇 골드",
                marketingType: "CRM 전환",
                marketingMessage: "CRM 시스템 도입하시면 고객 관리가 훨씬 체계적으로 되고, 매출도 평균 30% 증가하는 효과가 있습니다. 무료 체험부터 시작해보시는 건 어떠세요?",
                customerConsentYn: "Y",
                bookmarked: true
            }
        ]
    }), []);

    // 포인트 데이터 API 호출
    useEffect(() => {
        const fetchPointData = async () => {
            try {
                setLoading(true);
                const response = await fetch('http://localhost:8080/dash/point/csm6_mgr01');
                const result = await response.json();

                if (result.result && result.data) {
                    setPointData(result.data);
                } else {
                    setError(result.errorMessage || '데이터를 불러오는데 실패했습니다.');
                }
            } catch (err) {
                setError('서버 연결에 실패했습니다.');
                console.error('포인트 데이터 로드 에러:', err);
            } finally {
                setLoading(false);
            }
        };

        fetchPointData();
    }, []);

    // 등급 시스템 (API 기준으로 수정)
    const gradeSystem = [
        { name: 'bronze', displayName: '브론즈', min: 0, max: 49, color: 'amber', icon: '🥉' },
        { name: 'silver', displayName: '실버', min: 50, max: 99, color: 'gray', icon: '🥈' },
        { name: 'gold', displayName: '골드', min: 100, max: 149, color: 'yellow', icon: '🥇' },
        { name: 'platinum', displayName: '플래티넘', min: 150, max: 999999, color: 'purple', icon: '💎' }
    ];

    const getCurrentGrade = (nudgeCount) => {
        return gradeSystem.find(grade => nudgeCount >= grade.min && nudgeCount <= grade.max);
    };

    const getNextGrade = (nudgeCount) => {
        return gradeSystem.find(grade => nudgeCount < grade.min);
    };

    // 등급별 혜택 정의 (더 구체적이고 상세하게)
    const getGradeBenefits = (gradeName) => {
        const benefits = {
            bronze: [
                '• 기본 적립률 1% 적용',
                '• 월 1회 무료 음료 제공',
                '• 기본 상담 지원 서비스',
                '• 월간 성과 리포트 제공'
            ],
            silver: [
                '• 적립률 1.5% 적용 (50% 증가)',
                '• 월 2회 무료 음료 제공',
                '• 우선 상담 지원 서비스',
                '• 주간 성과 리포트 제공',
                '• 교육 자료 우선 접근'
            ],
            gold: [
                '• 적립률 2% 적용 (100% 증가)',
                '• 월 3회 무료 음료 제공',
                '• 전용 라운지 이용 가능',
                '• 특별 교육 프로그램 참여',
                '• 우선 배정 시스템 혜택',
                '• 분기별 성과 보너스'
            ],
            platinum: [
                '• 적립률 3% 적용 (200% 증가)',
                '• 무제한 음료 제공',
                '• VIP 라운지 무제한 이용',
                '• 1:1 전담 멘토링 서비스',
                '• 연말 특별 보너스 지급',
                '• 개인 비서 서비스 제공',
                '• 해외 연수 기회 우선권'
            ]
        };
        return benefits[gradeName] || [];
    };

    // AI 분석 API 호출 함수 (500 에러 시 재시도 로직 포함)
    const fetchAiAnalysis = async (messageId, messageText, maxRetries = 3) => {
        let retryCount = 0;

        while (retryCount < maxRetries) {
            try {
                setAnalysisLoading(prev => ({ ...prev, [messageId]: true }));
                setAnalysisError(prev => ({ ...prev, [messageId]: null }));

                // 예시 API 호출 - 실제 엔드포인트로 변경 필요
                const response = await fetch('/api/ai-analysis', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({
                        messageId: messageId,
                        message: messageText,
                        analysisType: 'feedback' // 또는 'colleague'
                    })
                });

                if (response.status === 500) {
                    throw new Error('Server Error 500');
                }

                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }

                const result = await response.json();

                if (result.success) {
                    setAiAnalysisData(prev => ({
                        ...prev,
                        [messageId]: result.data.analysis
                    }));
                    setAnalysisLoading(prev => ({ ...prev, [messageId]: false }));
                    return result.data.analysis;
                } else {
                    throw new Error(result.message || 'Analysis failed');
                }

            } catch (error) {
                retryCount++;
                console.error(`AI Analysis attempt ${retryCount} failed:`, error);

                if (error.message === 'Server Error 500' && retryCount < maxRetries) {
                    // 500 에러 시 잠시 대기 후 재시도
                    await new Promise(resolve => setTimeout(resolve, 1000 * retryCount));
                    continue;
                } else if (retryCount >= maxRetries) {
                    // 최대 재시도 횟수 초과
                    setAnalysisError(prev => ({
                        ...prev,
                        [messageId]: `분석 실패 (${retryCount}회 시도 후 포기): ${error.message}`
                    }));
                    setAnalysisLoading(prev => ({ ...prev, [messageId]: false }));
                    return null;
                } else {
                    // 500이 아닌 다른 에러
                    setAnalysisError(prev => ({
                        ...prev,
                        [messageId]: error.message
                    }));
                    setAnalysisLoading(prev => ({ ...prev, [messageId]: false }));
                    return null;
                }
            }
        }
    };

    // 여러 메시지에 대한 AI 분석을 순차적으로 호출 (동시 호출로 인한 500 에러 방지)
    const fetchMultipleAiAnalysis = async (messages) => {
        for (const message of messages) {
            const messageId = message.id;

            // 이미 분석된 데이터가 있으면 스킵
            if (aiAnalysisData[messageId]) {
                continue;
            }

            // 각 메시지마다 500ms 지연을 두어 서버 부하 방지
            await new Promise(resolve => setTimeout(resolve, 500));
            await fetchAiAnalysis(messageId, message.marketingMessage || message.message);
        }
    };

    // 컴포넌트 마운트 시 AI 분석 데이터 로드
    useEffect(() => {
        if (pointData && data.curnetDatas.length > 0) {
            // 내 피드백 데이터에 대한 AI 분석
            fetchMultipleAiAnalysis(data.curnetDatas);
        }
    }, [pointData, data.curnetDatas]);

    useEffect(() => {
        if (pointData && data.colleagueSuccessStories.length > 0) {
            // 동료 성공사례에 대한 AI 분석
            fetchMultipleAiAnalysis(data.colleagueSuccessStories);
        }
    }, [pointData, data.colleagueSuccessStories]);

    // AI 분석 결과 렌더링 함수
    const renderAiAnalysis = (messageId, defaultMessage) => {
        if (analysisLoading[messageId]) {
            return (
                <div className="ai-comment">
                    <div className="analysis-loading">
                        🤖 AI가 분석 중입니다... <span className="loading-dots">●●●</span>
                    </div>
                </div>
            );
        }

        if (analysisError[messageId]) {
            return (
                <div className="ai-comment error">
                    <div className="analysis-error">
                        ❌ 분석 실패: {analysisError[messageId]}
                        <button
                            className="retry-button"
                            onClick={() => fetchAiAnalysis(messageId, defaultMessage)}
                        >
                            다시 시도
                        </button>
                    </div>
                </div>
            );
        }

        const analysisResult = aiAnalysisData[messageId];
        if (analysisResult) {
            return (
                <div className="ai-comment">
                    {analysisResult}
                </div>
            );
        }

        // 기본 메시지 (API 호출 전 또는 실패 시)
        return (
            <div className="ai-comment">
                {defaultMessage}
            </div>
        );
    };
    const convertPointHistory = (history) => {
        return history.map(item => ({
            ...item,
            emoji: item.pointType === 'EARN' ?
                (item.pointReason.includes('넛지') ? '🎉' :
                    item.pointReason.includes('만족도') ? '⭐' :
                        item.pointReason.includes('성과') ? '🎯' :
                            item.pointReason.includes('1위') ? '🏆' :
                                item.pointReason.includes('우수상담원') ? '🎖️' : '🎁') :
                (item.pointReason.includes('카페') ? '☕' :
                    item.pointReason.includes('편의점') ? '🛍️' :
                        item.pointReason.includes('점심') ? '🍔' :
                            item.pointReason.includes('문화') ? '🎁' : '🛍️'),
            displayDate: new Date(item.createdDate).toLocaleDateString('ko-KR', {
                month: '2-digit',
                day: '2-digit'
            }).replace(/\//g, '.')
        }));
    };

    // useMemo들을 조건부 return 이전에 호출
    const xAxisInterval = useMemo(() => {
        const dataLength = data.monthlyData.length;
        if (dataLength <= 10) return 0;
        if (dataLength <= 20) return 1;
        return 2;
    }, [data.monthlyData.length]);

    const MonthlyChart = useMemo(() => (
        <MonthlyChartComponent data={data.monthlyData} xAxisInterval={xAxisInterval} />
    ), [data.monthlyData, xAxisInterval]);

    // 숫자 카운트업 애니메이션 (포인트 데이터 로드 후 실행)
    useEffect(() => {
        if (!pointData) return;

        const duration = 1500;
        const steps = 30;
        const stepTime = duration / steps;

        let currentStep = 0;
        const timer = setInterval(() => {
            currentStep++;
            const progress = currentStep / steps;
            const easeOut = 1 - Math.pow(1 - progress, 3);

            setAnimatedValues({
                nudgeRate: easeOut * data.monthAnalyze.nudgePercentage,
                nudgeCount: Math.floor(easeOut * data.monthAnalyze.nudgeCount),
                todayCount: Math.floor(easeOut * data.currentAnalyze.nudgeCount),
                points: Math.floor(easeOut * pointData.currentPoints)
            });

            if (currentStep >= steps) {
                clearInterval(timer);
                setAnimatedValues({
                    nudgeRate: data.monthAnalyze.nudgePercentage,
                    nudgeCount: data.monthAnalyze.nudgeCount,
                    todayCount: data.currentAnalyze.nudgeCount,
                    points: pointData.currentPoints
                });
            }
        }, stepTime);

        return () => clearInterval(timer);
    }, [pointData, data.monthAnalyze.nudgePercentage, data.monthAnalyze.nudgeCount, data.currentAnalyze.nudgeCount]);

    // ESC 키로 모달 닫기
    useEffect(() => {
        const handleEscape = (e) => {
            if (e.key === 'Escape' && isModalOpen) {
                setIsModalOpen(false);
            }
        };

        document.addEventListener('keydown', handleEscape);
        return () => document.removeEventListener('keydown', handleEscape);
    }, [isModalOpen]);

    // 포인트 데이터가 로드되기 전까지 로딩 처리
    if (loading) {
        return (
            <div className="dashboard">
                <div className="loading-container">
                    <div className="loading-spinner">🔄</div>
                    <div>포인트 데이터를 불러오는 중...</div>
                </div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="dashboard">
                <div className="error-container">
                    <div className="error-message">❌ {error}</div>
                    <button onClick={() => window.location.reload()}>다시 시도</button>
                </div>
            </div>
        );
    }

    const currentGrade = pointData ? getCurrentGrade(pointData.monthNudgeCount) : gradeSystem[0];
    const nextGrade = pointData ? getNextGrade(pointData.monthNudgeCount) : null;
    const gradeProgress = pointData && nextGrade ?
        ((pointData.monthNudgeCount - currentGrade.min) / (nextGrade.min - currentGrade.min)) * 100 : 100;

    const getGrowthIcon = (growth) => {
        if (growth.includes('+')) return <TrendingUp className="growth-icon up" />;
        if (growth.includes('-')) return <TrendingDown className="growth-icon down" />;
        return <Minus className="growth-icon neutral" />;
    };



    // 포인트 히스토리 데이터 변환 (pointData가 있을 때만)
    const earnHistory = pointData ? convertPointHistory(pointData.pointHistory.filter(item => item.pointType === 'EARN')) : [];
    const spendHistory = pointData ? convertPointHistory(pointData.pointHistory.filter(item => item.pointType === 'SPEND')) : [];

    const toggleBookmark = (storyId) => {
        // 북마크 토글 로직 (향후 구현)
        console.log(`북마크 토글: ${storyId}`);
    };

    return (
        <div className="dashboard">
            <div className="dashboard-container">
                {/* 상단 네비게이션 */}
                <div className="top-navigation">
                    <div className="nav-left">
                        <div className="system-brand">
                            <div className="brand-icon">🤝</div>
                            <h1 className="brand-name">하이파이브 넛지</h1>
                        </div>
                    </div>
                    <div className="nav-right">
                        <div className="user-greeting">
                            <span className="greeting-text">안녕하세요, <strong>김상담님</strong> 👋</span>
                            <span className="greeting-subtitle">오늘도 좋은 하루 되세요!</span>
                        </div>
                    </div>
                </div>

                {/* 통합된 주요 지표 */}
                <section className="kpi-section">
                    <div className="section-title">
                        <div className="title-indicator"></div>
                        <h2>주요 지표</h2>
                    </div>

                    <div className="kpi-grid">
                        {/* 넛지율 */}
                        <div className="kpi-card nudge-rate">
                            <div className="card-header">
                                <div className="card-title">
                                    <Target className="icon" />
                                    <span>이번달 넛지율</span>
                                </div>
                                {animatedValues.nudgeRate >= 4.0 && (
                                    <div className="achievement-indicator">
                                        <Trophy className="icon" />
                                    </div>
                                )}
                            </div>

                            <div className="nudge-stats">
                                <div className="main-stats">
                                    <div className="rate-value">
                                        {animatedValues.nudgeRate.toFixed(1)}<span>%</span>
                                    </div>

                                    <div className="stat-group-compact">
                                        <div className="stat-item">
                                            <span className="stat-value highlight">{animatedValues.nudgeCount}</span>
                                            <span className="stat-label">내 넛지 성공</span>
                                        </div>
                                        <div className="stat-item">
                                            <span className="stat-value">{data.monthAnalyze.totalCount}</span>
                                            <span className="stat-label">전체 통화</span>
                                        </div>
                                    </div>

                                    <div className="team-comparison">
                                        <div className="comparison-item">
                                            <span className="comparison-label">팀 평균</span>
                                            <span className="comparison-value">18건</span>
                                        </div>
                                        <div className="comparison-item">
                                            <span className="comparison-label">내 순위</span>
                                            <span className="comparison-value positive">#3</span>
                                        </div>
                                    </div>

                                    <div className="goal-progress">
                                        <div className="goal-header">
                                            <span>월 목표 달성률</span>
                                            <span>85%</span>
                                        </div>
                                        <div className="progress-bar">
                                            <div className="progress-fill" style={{ width: '85%' }}></div>
                                        </div>
                                    </div>
                                </div>

                                {MonthlyChart}
                            </div>
                        </div>

                        {/* 전환 현황 & 어제 성과 통합 */}
                        <div className="kpi-card conversion-performance">
                            <div className="card-header">
                                <BarChart3 className="icon" />
                                <span>전환 현황 & 어제 성과</span>
                            </div>

                            <div className="yesterday-summary">
                                <div className="performance-stats">
                                    <div className="stat-item">
                                        <div className="stat-value highlight">{animatedValues.todayCount}</div>
                                        <div className="stat-label">어제 넛지 성공</div>
                                    </div>
                                    <div className="stat-item">
                                        <div className="stat-value">{data.currentAnalyze.nudgePercentage}%</div>
                                        <div className="stat-label">성공률</div>
                                    </div>
                                    <div className="trend-info positive">
                                        <TrendingUp className="icon" />
                                        <span>전일 대비 +{data.currentAnalyze.nudgeCount}건</span>
                                    </div>
                                </div>
                            </div>

                            <div className="conversion-grid">
                                <div className="conversion-item">
                                    <div className="value pink">{data.currentAnalyze.gourp1Count}</div>
                                    <div className="label">GIGA</div>
                                    <div className="growth">
                                        {getGrowthIcon(data.currentAnalyze.group1Growth)}
                                        <span>{data.currentAnalyze.group1Growth}</span>
                                    </div>
                                </div>
                                <div className="conversion-item">
                                    <div className="value blue">{data.currentAnalyze.gourp2Count}</div>
                                    <div className="label">CRM</div>
                                    <div className="growth">
                                        {getGrowthIcon(data.currentAnalyze.group2Growth)}
                                        <span>유지</span>
                                    </div>
                                </div>
                                <div className="conversion-item">
                                    <div className="value green">{data.currentAnalyze.gourp3Count}</div>
                                    <div className="label">TDS</div>
                                    <div className="growth">
                                        {getGrowthIcon(data.currentAnalyze.group3Growth)}
                                        <span>{data.currentAnalyze.group3Growth}</span>
                                    </div>
                                </div>
                            </div>

                            <div className="ai-encouragement">
                                <div className="ai-avatar">🤖</div>
                                <div className="encouragement-content">
                                    <div className="encouragement-text">
                                        "어제보다 더 좋은 성과를 보이고 계시네요! 이런 추세라면 이번 달 목표 달성도 충분히 가능해 보입니다. 파이팅! 💪"
                                    </div>
                                    <div className="ai-signature">- AI 어시스턴트</div>
                                </div>
                            </div>
                        </div>

                        {/* Vimeo 영상 */}
                        <div className="kpi-card video-section">
                            <div className="card-header">
                                <div className="card-title">
                                    <MessageSquare className="icon" />
                                    <span>교육 영상</span>
                                </div>
                                <button
                                    className="more-button"
                                    onClick={() => window.open('https://www.google.com', '_blank')}
                                >
                                    더보기 →
                                </button>
                            </div>
                            <div className="video-container">
                                <iframe
                                    src="https://player.vimeo.com/video/998263129?badge=0&autopause=0&player_id=0&app_id=58479"
                                    width="100%"
                                    height="260"
                                    frameBorder="0"
                                    allow="autoplay; fullscreen; picture-in-picture"
                                    allowFullScreen
                                    title="교육 영상"
                                ></iframe>
                            </div>
                            <div className="video-description">
                                📚 고객 상담 스킬 향상을 위한 실전 교육 영상 - 넛지 기법 활용법
                            </div>
                        </div>
                    </div>
                </section>

                {/* 하단 상세 정보 */}
                <div className="detail-grid">
                    {/* 등급 시스템 & 포인트 통합 (API 데이터 사용) */}
                    <div className="points-section">
                        <div className="section-header">
                            <div className="title-group">
                                <div className="title-indicator amber"></div>
                                <h2>등급 시스템 & 포인트</h2>
                            </div>
                            <div className="points-badge">이번주 +{pointData ? earnHistory.slice(0, 3).reduce((sum, item) => sum + item.pointAmount, 0) : 0}P ✨</div>
                        </div>

                        <div className="points-cards">
                            <div className="integrated-points-grade-card">
                                <div className="points-grade-content">
                                    {/* 포인트 정보 섹션 */}
                                    <div className="points-section-content">
                                        <div className="current-points">{pointData ? animatedValues.points.toLocaleString() : '0'}</div>
                                        <div className="points-label">현재 보유 포인트</div>

                                        <div className="grade-progress">
                                            <div className="progress-bar">
                                                <div
                                                    className={`progress-fill ${currentGrade.color}`}
                                                    style={{ width: `${gradeProgress}%` }}
                                                ></div>
                                            </div>
                                            <div className="progress-label">
                                                {pointData && nextGrade ? `${nextGrade.displayName}까지 ${nextGrade.min - pointData.monthNudgeCount}건` : '최고 등급!'}
                                            </div>
                                        </div>

                                        <div className={`grade-badge ${currentGrade.color}`}>
                                            <span>{currentGrade.icon}</span>
                                            <span>{currentGrade.displayName} 등급</span>
                                        </div>

                                        <div className="team-rank-info">
                                            이달 넛지 건수: {pointData ? pointData.monthNudgeCount : 0}건
                                        </div>

                                        <button
                                            className="points-history-button-inline"
                                            onClick={() => setIsModalOpen(true)}
                                        >
                                            <div className="button-content-inline">
                                                <div className="button-icon">📊</div>
                                                <div className="button-text">
                                                    <div className="button-title">포인트 내역 보기</div>
                                                </div>
                                                <div className="button-arrow">→</div>
                                            </div>
                                        </button>

                                        <button
                                            className="points-shop-button-inline"
                                            onClick={() => setShowStore(true)}
                                        >
                                            <div className="button-content-inline">
                                                <div className="button-icon">🛍️</div>
                                                <div className="button-text">
                                                    <div className="button-title">포인트 상점</div>
                                                </div>
                                                <div className="button-arrow">→</div>
                                            </div>
                                        </button>
                                    </div>

                                    {/* 등급 시스템 섹션 */}
                                    <div className="grade-system-content">
                                        <h3 className="grade-system-title">
                                            <Trophy className="icon" />
                                            <span>등급 시스템</span>
                                        </h3>

                                        <div className="grade-list">
                                            {gradeSystem.map((grade, index) => (
                                                <div
                                                    key={grade.name}
                                                    className={`grade-item ${grade.name === currentGrade.name ? 'active' : ''} ${grade.color}`}
                                                    onMouseEnter={() => {
                                                        console.log('Mouse enter:', grade.name); // 디버깅용
                                                        setHoveredGrade(grade.name);
                                                    }}
                                                    onMouseLeave={() => {
                                                        console.log('Mouse leave:', grade.name); // 디버깅용
                                                        setHoveredGrade(null);
                                                    }}
                                                >
                                                    <div className="grade-info">
                                                        <span className="grade-icon">{grade.icon}</span>
                                                        <span className="grade-name">{grade.displayName}</span>
                                                        {grade.name === currentGrade.name && (
                                                            <span className="current-badge">현재</span>
                                                        )}
                                                    </div>
                                                    <span className="grade-points">
                                                        {grade.max === 999999 ? `${grade.min}건+` : `${grade.min}-${grade.max}건`}
                                                    </span>

                                                    {/* 호버 시 표시되는 혜택 툴팁 */}
                                                    {hoveredGrade === grade.name && (
                                                        <div className={`grade-hover-benefits ${grade.color}`}>
                                                            <div className="benefits-header-tooltip">
                                                                <span>{grade.icon} {grade.displayName} 등급 혜택</span>
                                                            </div>
                                                            <div className="benefits-list-tooltip">
                                                                {getGradeBenefits(grade.name).map((benefit, idx) => (
                                                                    <div key={idx} className="benefit-item">{benefit}</div>
                                                                ))}
                                                            </div>
                                                        </div>
                                                    )}
                                                </div>
                                            ))}
                                        </div>

                                        {/* 현재 등급 혜택 */}
                                        <div className={`grade-benefits ${currentGrade.color}`}>
                                            <div className="benefits-header">
                                                <span>{currentGrade.icon} {currentGrade.displayName} 등급 혜택</span>
                                            </div>
                                            <div className="benefits-list">
                                                {currentGrade.name === 'bronze' && (
                                                    <>
                                                        <div>• 기본 적립률 1%</div>
                                                        <div>• 월 1회 무료 음료</div>
                                                    </>
                                                )}
                                                {currentGrade.name === 'silver' && (
                                                    <>
                                                        <div>• 적립률 1.5%</div>
                                                        <div>• 월 2회 무료 음료</div>
                                                        <div>• 우선 상담 지원</div>
                                                    </>
                                                )}
                                                {currentGrade.name === 'gold' && (
                                                    <>
                                                        <div>• 적립률 2%</div>
                                                        <div>• 월 3회 무료 음료</div>
                                                        <div>• 전용 라운지 이용</div>
                                                        <div>• 특별 교육 프로그램</div>
                                                    </>
                                                )}
                                                {currentGrade.name === 'platinum' && (
                                                    <>
                                                        <div>• 적립률 3%</div>
                                                        <div>• 무제한 음료</div>
                                                        <div>• VIP 라운지 이용</div>
                                                        <div>• 1:1 멘토링</div>
                                                        <div>• 연말 특별 보너스</div>
                                                    </>
                                                )}
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    {/* AI 인사이트 */}
                    <div className="ai-insights">
                        <div className="insights-nav">
                            <div className="nav-tabs">
                                <button
                                    onClick={() => setFeedbackTab('my')}
                                    className={`nav-tab ${feedbackTab === 'my' ? 'active' : ''}`}
                                >
                                    <span className="tab-dot"></span>
                                    내 피드백
                                </button>
                                <button
                                    onClick={() => setFeedbackTab('colleagues')}
                                    className={`nav-tab ${feedbackTab === 'colleagues' ? 'active' : ''}`}
                                >
                                    <span className="tab-dot"></span>
                                    동료 사례
                                </button>
                            </div>
                            <div className="ai-indicator">
                                <div className="ai-pulse"></div>
                                <span>AI 분석</span>
                            </div>
                        </div>

                        <div className="insights-stream">
                            {feedbackTab === 'my' ? (
                                data.curnetDatas.length > 0 ? (
                                    <div className="feedback-stream">
                                        {data.curnetDatas.map((item, index) => (
                                            <div key={item.id} className="feedback-item">
                                                <div className="item-meta">
                                                    <div className={`status-indicator ${item.customerConsentYn === 'Y' ? 'success' : 'improve'}`}></div>
                                                    <span className="item-type">{item.marketingType}</span>
                                                    <span className="item-time">방금 전</span>
                                                </div>

                                                <div className="item-content">
                                                    {/* AI 추천멘트 세션 */}
                                                    <div className="recommendation-section">
                                                        <div className="section-label">
                                                            <span className="label-icon">💬</span>
                                                            <span className="label-text">AI 추천멘트</span>
                                                        </div>
                                                        <div className="message-preview">
                                                            "{item.marketingMessage}"
                                                        </div>
                                                    </div>

                                                    {/* 피드백 세션 */}
                                                    <div className="feedback-section">
                                                        <div className="section-label">
                                                            <span className="label-icon">💡</span>
                                                            <span className="label-text">피드백</span>
                                                        </div>
                                                        <div className="ai-feedback">
                                                            <div className="feedback-icon">✨</div>
                                                            <div className="feedback-text">
                                                                {item.customerConsentYn === 'Y'
                                                                    ? "완벽한 접근! 고객 니즈 파악이 정확했고 타이밍도 좋았어요."
                                                                    : "다음엔 구체적 이유를 제시해보세요."
                                                                }
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        ))}
                                    </div>
                                ) : (
                                    <div className="empty-state">
                                        <div className="empty-icon">✨</div>
                                        <h3>첫 번째 피드백을 받아보세요</h3>
                                        <p>상담을 진행하면 AI가 실시간으로 분석해드립니다</p>
                                    </div>
                                )
                            ) : (
                                <div className="colleague-stream">
                                    {data.colleagueSuccessStories.map((story, index) => (
                                        <div key={story.id} className="colleague-item">
                                            <div className="colleague-header">
                                                <div className="colleague-avatar">
                                                    <span>{story.consultantName.charAt(0)}</span>
                                                </div>
                                                <div className="colleague-info">
                                                    <div className="colleague-name">{story.consultantName}</div>
                                                    <div className="colleague-level">{story.consultantLevel}</div>
                                                </div>
                                                <button
                                                    className={`bookmark ${story.bookmarked ? 'active' : ''}`}
                                                    onClick={() => toggleBookmark(story.id)}
                                                >
                                                    {story.bookmarked ? '★' : '☆'}
                                                </button>
                                            </div>

                                            <div className="colleague-content">
                                                <div className="content-type">{story.marketingType}</div>
                                                <div className="content-message">
                                                    "{story.marketingMessage}"
                                                </div>
                                                <div className="content-insight">
                                                    <span className="insight-label">성공 포인트</span>
                                                    <span className="insight-value">구체적인 수치와 고객 맞춤형 혜택 강조</span>
                                                </div>
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            )}
                        </div>
                    </div>
                </div>
            </div>

            {/* 포인트 내역 모달 (API 데이터 사용) */}
            {isModalOpen && (
                <div className="modal-overlay" onClick={() => setIsModalOpen(false)}>
                    <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                        <div className="modal-header">
                            <h3>포인트 내역</h3>
                            <button
                                className="modal-close"
                                onClick={() => setIsModalOpen(false)}
                            >
                                ✕
                            </button>
                        </div>

                        <div className="modal-body">
                            <div className="tab-buttons">
                                <button
                                    onClick={() => setActiveTab('earn')}
                                    className={`tab-button ${activeTab === 'earn' ? 'active' : ''}`}
                                >
                                    적립 🎯
                                </button>
                                <button
                                    onClick={() => setActiveTab('use')}
                                    className={`tab-button ${activeTab === 'use' ? 'active' : ''}`}
                                >
                                    사용 🛍️
                                </button>
                            </div>

                            <div className="history-list">
                                {activeTab === 'earn' ? (
                                    earnHistory.length > 0 ? (
                                        earnHistory.map((item, index) => (
                                            <div key={index} className="history-item earn">
                                                <div className="item-info">
                                                    <div className="emoji">{item.emoji}</div>
                                                    <div>
                                                        <div className="item-title">{item.pointReason}</div>
                                                        <div className="item-date">{item.displayDate}</div>
                                                        {item.gradeBonusRate > 0 && (
                                                            <div className="bonus-info">
                                                                {item.grade} 등급 보너스 +{(item.gradeBonusRate * 100).toFixed(1)}%
                                                            </div>
                                                        )}
                                                    </div>
                                                </div>
                                                <div className="item-points">+{item.pointAmount}</div>
                                            </div>
                                        ))
                                    ) : (
                                        <div className="no-history-message">
                                            <div className="no-history-icon">📊</div>
                                            <div className="no-history-text">적립 내역이 없습니다</div>
                                        </div>
                                    )
                                ) : (
                                    spendHistory.length > 0 ? (
                                        spendHistory.map((item, index) => (
                                            <div key={index} className="history-item use">
                                                <div className="item-info">
                                                    <div className="emoji">{item.emoji}</div>
                                                    <div>
                                                        <div className="item-title">{item.pointReason}</div>
                                                        <div className="item-date">{item.displayDate}</div>
                                                    </div>
                                                </div>
                                                <div className="item-points">-{Math.abs(item.pointAmount)}</div>
                                            </div>
                                        ))
                                    ) : (
                                        <div className="no-history-message">
                                            <div className="no-history-icon">🛍️</div>
                                            <div className="no-history-text">사용 내역이 없습니다</div>
                                        </div>
                                    )
                                )}
                            </div>
                        </div>
                    </div>
                </div>
            )}

            {/* 시스템 멘트 */}
            <div className="system-footer">
                <div className="system-message">
                    <div className="message-icon">💡</div>
                    <div className="message-content">
                        <h3>하이파이브 넛지와 함께</h3>
                        <p>고객의 니즈를 정확히 파악하고, 최적의 솔루션을 제안하여<br />
                            진정한 가치를 전달하는 상담사가 되어보세요.</p>
                        <div className="message-tags">
                            <span className="tag">스마트 상담</span>
                            <span className="tag">고객 중심</span>
                            <span className="tag">데이터 기반</span>
                        </div>
                    </div>
                </div>
            </div>

            {/* 포인트 상점 모달 */}
            {showStore && (
                <div className="store-overlay" onClick={handleCloseStore}>
                    <div className="store-modal" onClick={(e) => e.stopPropagation()}>
                        {/* 헤더 */}
                        <div className="store-header">
                            <div className="store-header-content">
                                <h2>포인트 상점</h2>
                                <div className="user-points">
                                    <span className="points-icon">💎</span>
                                    <span className="points-text">{animatedValues.points.toLocaleString()}P</span>
                                </div>
                            </div>
                            <button className="store-close" onClick={handleCloseStore}>×</button>
                        </div>

                        {/* 카테고리 필터 */}
                        <div className="store-categories">
                            <button
                                className={`category-btn ${selectedCategory === '전체' ? 'active' : ''}`}
                                onClick={() => setSelectedCategory('전체')}
                            >
                                전체
                            </button>
                            {categories.map(category => (
                                <button
                                    key={category}
                                    className={`category-btn ${selectedCategory === category ? 'active' : ''}`}
                                    onClick={() => setSelectedCategory(category)}
                                >
                                    {category}
                                </button>
                            ))}
                        </div>

                        {/* 상품 목록 */}
                        <div className="store-content">
                            <div className="products-grid">
                                {filteredProducts.map(product => (
                                    <div
                                        key={product.id}
                                        className="product-card"
                                        onClick={() => handleProductClick(product)}
                                    >
                                        <div className="product-image">{product.image}</div>
                                        <div className="product-info">
                                            <h3 className="product-name">{product.name}</h3>
                                            <p className="product-description">{product.description}</p>
                                            <div className="product-meta">
                                                <span className="product-price">{product.price.toLocaleString()}P</span>
                                                <span className={`product-stock ${product.stock < 5 ? 'low' : ''}`}>
                                                    재고: {product.stock}개
                                                </span>
                                            </div>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </div>

                        {/* 구매 모달 */}
                        {showPurchaseModal && selectedProduct && (
                            <div className="purchase-overlay" onClick={() => setShowPurchaseModal(false)}>
                                <div className="purchase-modal" onClick={(e) => e.stopPropagation()}>
                                    <div className="purchase-header">
                                        <h3>상품 구매</h3>
                                        <button
                                            className="purchase-close"
                                            onClick={() => setShowPurchaseModal(false)}
                                        >
                                            ×
                                        </button>
                                    </div>

                                    <div className="purchase-content">
                                        <div className="purchase-product">
                                            <div className="purchase-image">{selectedProduct.image}</div>
                                            <div className="purchase-info">
                                                <h4>{selectedProduct.name}</h4>
                                                <p>{selectedProduct.description}</p>
                                                <span className="purchase-price">{selectedProduct.price.toLocaleString()}P</span>
                                            </div>
                                        </div>

                                        <div className="purchase-details">
                                            <div className="quantity-selector">
                                                <label>수량</label>
                                                <div className="quantity-controls">
                                                    <button
                                                        className="quantity-btn"
                                                        onClick={() => setPurchaseQuantity(Math.max(1, purchaseQuantity - 1))}
                                                        disabled={purchaseQuantity <= 1}
                                                    >
                                                        -
                                                    </button>
                                                    <span className="quantity-value">{purchaseQuantity}</span>
                                                    <button
                                                        className="quantity-btn"
                                                        onClick={() => setPurchaseQuantity(Math.min(selectedProduct.stock, purchaseQuantity + 1))}
                                                        disabled={purchaseQuantity >= selectedProduct.stock}
                                                    >
                                                        +
                                                    </button>
                                                </div>
                                            </div>

                                            <div className="purchase-summary">
                                                <div className="summary-row">
                                                    <span>상품 가격</span>
                                                    <span>{selectedProduct.price.toLocaleString()}P</span>
                                                </div>
                                                <div className="summary-row">
                                                    <span>수량</span>
                                                    <span>{purchaseQuantity}개</span>
                                                </div>
                                                <div className="summary-row total">
                                                    <span>총 결제 금액</span>
                                                    <span>{(selectedProduct.price * purchaseQuantity).toLocaleString()}P</span>
                                                </div>
                                                <div className="summary-row">
                                                    <span>보유 포인트</span>
                                                    <span>{animatedValues.points.toLocaleString()}P</span>
                                                </div>
                                                <div className="summary-row remaining">
                                                    <span>잔여 포인트</span>
                                                    <span>{(animatedValues.points - selectedProduct.price * purchaseQuantity).toLocaleString()}P</span>
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <div className="purchase-actions">
                                        <button
                                            className="purchase-btn"
                                            onClick={handlePurchase}
                                            disabled={selectedProduct.price * purchaseQuantity > animatedValues.points || purchaseQuantity > selectedProduct.stock}
                                        >
                                            구매하기
                                        </button>
                                    </div>
                                </div>
                            </div>
                        )}
                    </div>
                </div>
            )}
        </div>
    );
};

export default Home;