import React, { useState, useEffect, useMemo, memo, useRef } from 'react';
import { Trophy, Target } from 'lucide-react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import './Home.css';

// 영업일 체크 함수
const isBusinessDay = (dateString) => {
    const date = new Date(dateString);
    const day = date.getDay();
    // 토요일(6)과 일요일(0)이 아닌 경우가 영업일
    return day !== 0 && day !== 6;
};

// 날짜 포맷팅 함수
const formatDate = (dateString) => {
    const date = new Date(dateString);
    return `${date.getMonth() + 1}/${date.getDate()}`;
};

// 개선된 차트 컴포넌트 - 영업일 순서별 비교
const MonthlyChartComponent = memo(({ data, chartData, xAxisInterval }) => {
    // 총 건수는 API에서 받은 데이터 사용
    const lastMonthTotal = chartData?.lastMonthTotal || 0;
    const currentMonthTotal = chartData?.currentMonthTotal || 0;

    // 커스텀 툴팁 컴포넌트
    const CustomTooltip = ({ active, payload, label }) => {
        if (active && payload && payload.length) {
            const data = payload[0].payload;
            return (
                <div className="custom-tooltip" style={{
                    backgroundColor: '#ffffff',
                    border: '1px solid #e5e7eb',
                    borderRadius: '8px',
                    padding: '8px 12px',
                    boxShadow: '0 4px 12px rgba(0, 0, 0, 0.1)',
                    fontSize: '12px'
                }}>
                    <div style={{ fontWeight: '600', color: '#374151', marginBottom: '4px' }}>
                        {label}
                    </div>
                    {payload.map((entry) => {
                        if (entry.value === null || entry.value === undefined) return null;
                        if (entry.dataKey === 'currentMonthCountFuture') return null; // 미래 라인은 툴팁에서 제외

                        const isLastMonth = entry.dataKey === 'lastMonthCount';
                        const actualDate = isLastMonth ?
                            data.tooltipInfo.lastMonthDate :
                            data.tooltipInfo.currentMonthDate;

                        const isCurrentMonth = entry.dataKey === 'currentMonthCountReal';
                        let dateLabel = actualDate;
                        if (isCurrentMonth && data.isYesterday) {
                            dateLabel = `${actualDate} (어제)`;
                        }

                        return (
                            <div key={entry.dataKey} style={{
                                color: entry.color,
                                display: 'flex',
                                alignItems: 'center',
                                gap: '6px',
                                marginBottom: '2px'
                            }}>
                                <div style={{
                                    width: '8px',
                                    height: '8px',
                                    backgroundColor: entry.color,
                                    borderRadius: '50%'
                                }}></div>
                                <span>
                                    {isLastMonth ? '저번달' : '이번달'} ({dateLabel}): {entry.value}건
                                </span>
                            </div>
                        );
                    })}
                </div>
            );
        }
        return null;
    };

    return (
        <div className="monthly-chart">
            <div className="chart-header">
                <span className="chart-title">영업일 순서별 넛지 성공 건수 비교 (어제 ±3일)</span>
            </div>
            <div className="chart-container">
                <ResponsiveContainer width="100%" height={280}>
                    <LineChart data={data} margin={{ left: 10, right: 10, top: 5, bottom: 60 }}>
                        <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
                        <XAxis
                            dataKey="businessDay"
                            stroke="#6b7280"
                            fontSize={10}
                            tickLine={false}
                            axisLine={false}
                            interval={0}
                            angle={0}
                            textAnchor="middle"
                            height={50}
                        />
                        <YAxis
                            stroke="#6b7280"
                            fontSize={11}
                            tickLine={false}
                            axisLine={false}
                            domain={[0, 'dataMax + 1']}
                            width={30}
                        />
                        <Tooltip content={<CustomTooltip />} />

                        {/* 저번달 라인 */}
                        <Line
                            type="monotone"
                            dataKey="lastMonthCount"
                            name="lastMonth"
                            stroke="#94a3b8"
                            strokeWidth={3}
                            dot={{
                                fill: '#94a3b8',
                                stroke: '#ffffff',
                                strokeWidth: 2,
                                r: 4
                            }}
                            activeDot={{
                                r: 6,
                                stroke: '#94a3b8',
                                strokeWidth: 2,
                                fill: '#ffffff'
                            }}
                            connectNulls={true}
                        />

                        {/* 이번달 라인 - 어제까지만 (실선) */}
                        <Line
                            type="monotone"
                            dataKey="currentMonthCountReal"
                            name="currentMonth"
                            stroke="url(#lineGradient)"
                            strokeWidth={4}
                            dot={(props) => {
                                if (props.payload.isFuture || props.payload.currentMonthCountReal === null || props.payload.isToday) return null;

                                const { payload } = props;
                                const isYesterday = payload?.isYesterday;

                                return (
                                    <circle
                                        cx={props.cx}
                                        cy={props.cy}
                                        r={isYesterday ? 7 : 5}
                                        fill={isYesterday ? "#f59e0b" : "#3b82f6"}
                                        stroke="#ffffff"
                                        strokeWidth={isYesterday ? 3 : 2}
                                        style={{
                                            filter: isYesterday
                                                ? 'drop-shadow(0 0 6px rgba(245, 158, 11, 0.6))'
                                                : 'drop-shadow(0 2px 4px rgba(59, 130, 246, 0.3))'
                                        }}
                                    />
                                );
                            }}
                            activeDot={{
                                r: 8,
                                stroke: '#3b82f6',
                                strokeWidth: 3,
                                fill: '#ffffff',
                                filter: 'drop-shadow(0 4px 8px rgba(59, 130, 246, 0.4))'
                            }}
                            connectNulls={false}
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
            <div className="chart-legend">
                <div className="legend-item">
                    <span className="legend-color" style={{ backgroundColor: '#94a3b8' }}></span>
                    <span>저번달: {lastMonthTotal}건</span>
                </div>
                <div className="legend-item">
                    <span className="legend-color" style={{ backgroundColor: '#3b82f6' }}></span>
                    <span>이번달: {currentMonthTotal}건 (어제까지)</span>
                </div>
                <div className="legend-item">
                    <span className="legend-color" style={{ backgroundColor: '#f59e0b', width: '8px', height: '8px', borderRadius: '50%' }}></span>
                    <span>어제</span>
                </div>
            </div>
        </div>
    );
});

const Home = () => {
    // API 호출 중복 방지를 위한 ref
    const chartDataFetched = useRef(false);

    // 차트 데이터 상태
    const [chartData, setChartData] = useState(null);
    const [chartLoading, setChartLoading] = useState(false);
    const [chartError, setChartError] = useState(null);

    const [animatedValues, setAnimatedValues] = useState({
        nudgeRate: 0,
        nudgeCount: 0
    });

    // 기존 데이터
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



    // 차트 데이터 API 호출 - 영업일 순서별 처리
    useEffect(() => {
        // 이미 데이터를 가져왔다면 중복 호출 방지
        if (chartDataFetched.current) return;

        let isMounted = true;
        let abortController = new AbortController();

        const fetchChartData = async () => {
            try {
                console.log('차트 데이터 API 호출 시작');
                setChartLoading(true);
                const response = await fetch('http://localhost:8080/chart/data?userId=csm6_mgr01', {
                    signal: abortController.signal
                });
                const result = await response.json();

                if (isMounted && result.result && result.data) {
                    // 오늘 날짜 계산
                    const today = new Date();
                    const todayStr = today.toISOString().slice(0, 10).replace(/-/g, '');

                    console.log('오늘 날짜:', todayStr); // 디버깅용

                    // 영업일만 필터링하고 순차적으로 번호 매기기
                    const processChartData = (dataObj, isCurrentMonth = false) => {
                        const allEntries = Object.entries(dataObj);
                        console.log(isCurrentMonth ? '이번달 전체 데이터:' : '저번달 전체 데이터:', allEntries); // 디버깅용

                        const filteredData = allEntries
                            .filter(([dateStr, count]) => {
                                // 영업일 체크
                                const year = dateStr.substring(0, 4);
                                const month = dateStr.substring(4, 6);
                                const day = dateStr.substring(6, 8);

                                if (!isBusinessDay(`${year}-${month}-${day}`)) return false;

                                // 이번달인 경우 오늘까지 포함 (count가 0인 날도 포함)
                                if (isCurrentMonth) {
                                    return dateStr <= todayStr;
                                }

                                return true;
                            })
                            .map(([dateStr, count]) => {
                                const year = dateStr.substring(0, 4);
                                const month = dateStr.substring(4, 6);
                                const day = dateStr.substring(6, 8);
                                return {
                                    dateStr: dateStr,
                                    displayDate: formatDate(`${year}-${month}-${day}`),
                                    count: count,
                                    fullDate: `${year}-${month}-${day}` // 정렬용 전체 날짜
                                };
                            })
                            .sort((a, b) => {
                                // 전체 날짜로 정렬
                                return a.fullDate.localeCompare(b.fullDate);
                            })
                            .map(({ fullDate, ...rest }, index) => ({
                                ...rest,
                                businessDayIndex: index + 1 // 영업일 순서 번호
                            }));

                        console.log(isCurrentMonth ? '이번달 필터된 데이터:' : '저번달 필터된 데이터:', filteredData); // 디버깅용
                        return filteredData;
                    };

                    const lastMonthData = processChartData(result.data.lastMonthNudgeCount);
                    let currentMonthData = processChartData(result.data.currentMonthNudgeCount, true);

                    // 오늘이 영업일이고 데이터가 없다면 0으로 추가
                    const todayYear = todayStr.substring(0, 4);
                    const todayMonth = todayStr.substring(4, 6);
                    const todayDay = todayStr.substring(6, 8);

                    if (isBusinessDay(`${todayYear}-${todayMonth}-${todayDay}`)) {
                        const todayDataExists = currentMonthData.some(item => item.dateStr === todayStr);
                        if (!todayDataExists) {
                            // 오늘 데이터가 없으면 0으로 추가
                            const todayData = {
                                dateStr: todayStr,
                                displayDate: formatDate(`${todayYear}-${todayMonth}-${todayDay}`),
                                count: 0,
                                fullDate: `${todayYear}-${todayMonth}-${todayDay}`,
                                businessDayIndex: currentMonthData.length + 1
                            };
                            currentMonthData.push(todayData);
                        }
                    }

                    setChartData({
                        lastMonth: lastMonthData,
                        currentMonth: currentMonthData,
                        lastMonthTotal: result.data.lastMonthTotal,
                        currentMonthTotal: result.data.currentMonthTotal
                    });
                    chartDataFetched.current = true; // 성공적으로 데이터를 가져왔음을 표시
                } else if (isMounted) {
                    setChartError(result.errorMessage || '차트 데이터를 불러오는데 실패했습니다.');
                }
            } catch (err) {
                if (err.name === 'AbortError') {
                    return; // 요청이 취소된 경우
                }
                if (isMounted) {
                    setChartError('서버 연결에 실패했습니다.');
                    console.error('차트 데이터 로드 에러:', err);
                }
            } finally {
                if (isMounted) {
                    setChartLoading(false);
                }
            }
        };

        fetchChartData();

        return () => {
            isMounted = false;
            abortController.abort();
        };
    }, []);

    // 차트 데이터 처리 - 영업일 순서 기준으로 통합 및 필터링
    const processedChartData = useMemo(() => {
        if (!chartData) return [];

        console.log('chartData:', chartData); // 디버깅용

        const maxBusinessDays = Math.max(
            chartData.lastMonth.length,
            chartData.currentMonth.length
        );

        // 현재 이번달의 영업일 수 (실제 데이터가 있는 날짜까지)
        const currentMonthBusinessDays = chartData.currentMonth.length;

        console.log('currentMonthBusinessDays:', currentMonthBusinessDays); // 디버깅용
        console.log('maxBusinessDays:', maxBusinessDays); // 디버깅용

        // 어제를 기준으로 앞뒤 3일차 범위 계산
        const yesterdayDay = Math.max(1, currentMonthBusinessDays - 1); // 어제
        const startDay = Math.max(1, yesterdayDay - 3);
        const endDay = Math.min(yesterdayDay + 3, maxBusinessDays); // +3일까지 표시

        console.log(`범위: ${startDay}일차 ~ ${endDay}일차`); // 디버깅용

        const combinedData = [];
        for (let i = startDay; i <= endDay; i++) {
            const lastMonthItem = chartData.lastMonth.find(item => item.businessDayIndex === i);
            const currentMonthItem = chartData.currentMonth.find(item => item.businessDayIndex === i);

            const tooltipInfo = {
                lastMonthDate: lastMonthItem ? lastMonthItem.displayDate : null,
                currentMonthDate: currentMonthItem ? currentMonthItem.displayDate : null
            };

            // 실제 데이터 유무 확인
            const isYesterday = i === yesterdayDay; // 어제
            const hasCurrentData = i <= yesterdayDay; // 어제까지만 실제 데이터 있음
            const isFuture = i > yesterdayDay; // 어제 이후는 미래

            combinedData.push({
                businessDay: `${i}일차`,
                businessDayNum: i,
                lastMonthCount: lastMonthItem ? lastMonthItem.count : 0,
                currentMonthCount: hasCurrentData ? (currentMonthItem ? currentMonthItem.count : 0) : 0,
                // 실제 라인용 데이터 (어제까지만)
                currentMonthCountReal: hasCurrentData ? (currentMonthItem ? currentMonthItem.count : 0) : null,
                // 미래 예측용 데이터 (미래만)
                currentMonthCountFuture: isFuture ? 0 : null,
                tooltipInfo,
                isYesterday: isYesterday,
                isFuture: !hasCurrentData
            });
        }

        console.log('최종 combinedData:', combinedData); // 디버깅용
        return combinedData;
    }, [chartData]);

    // X축 간격 계산 - 7일 이내이므로 모든 라벨 표시
    const xAxisInterval = useMemo(() => {
        return 0; // 모든 라벨 표시 (최대 7개이므로)
    }, [processedChartData.length]);

    const MonthlyChart = useMemo(() => {
        if (chartLoading) {
            return (
                <div className="monthly-chart">
                    <div className="chart-header">
                        <span className="chart-title">영업일 순서별 넛지 성공 건수 비교</span>
                        <span className="chart-trend">📊 로딩 중...</span>
                    </div>
                    <div className="chart-container">
                        <div className="loading-chart">🔄 차트 데이터를 불러오는 중...</div>
                    </div>
                </div>
            );
        }

        if (chartError) {
            return (
                <div className="monthly-chart">
                    <div className="chart-header">
                        <span className="chart-title">영업일 순서별 넛지 성공 건수 비교</span>
                        <span className="chart-trend">📊 오류 발생</span>
                    </div>
                    <div className="chart-container">
                        <div className="error-chart">❌ {chartError}</div>
                    </div>
                </div>
            );
        }

        return (
            <MonthlyChartComponent
                data={processedChartData}
                chartData={chartData}
                xAxisInterval={xAxisInterval}
            />
        );
    }, [processedChartData, chartData, xAxisInterval, chartLoading, chartError]);

    // 숫자 카운트업 애니메이션
    useEffect(() => {
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
                nudgeCount: Math.floor(easeOut * data.monthAnalyze.nudgeCount)
            });

            if (currentStep >= steps) {
                clearInterval(timer);
                setAnimatedValues({
                    nudgeRate: data.monthAnalyze.nudgePercentage,
                    nudgeCount: data.monthAnalyze.nudgeCount
                });
            }
        }, stepTime);

        return () => clearInterval(timer);
    }, [data.monthAnalyze.nudgePercentage, data.monthAnalyze.nudgeCount]);



    return (
        <div className="dashboard">
            <div className="dashboard-container">
                {/* 이번달 넛지율 */}
                <section className="kpi-section">
                    <div className="section-title">
                        <div className="title-indicator"></div>
                        <h2>이번달 넛지율</h2>
                    </div>

                    <div className="kpi-grid">
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
                    </div>
                </section>
            </div>
        </div>
    );
};

export default Home;