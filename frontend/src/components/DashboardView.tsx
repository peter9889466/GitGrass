import React, { useState, useEffect } from "react";
import { api } from "../utils/api";

interface Repository {
  id: number;
  name: string;
  owner: string;
  lastCommitAt: string | null;
  isMonitored: boolean;
  branchName?: string;
}

interface DashboardViewProps {
  onNavigateToMyPage: () => void;
  onLogout: () => void;
}

export const DashboardView: React.FC<DashboardViewProps> = ({ onNavigateToMyPage, onLogout }) => {
  const [repositories, setRepositories] = useState<Repository[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [apiError, setApiError] = useState<string | null>(null);

  useEffect(() => {
    fetchRepositories();
  }, []);

  const fetchRepositories = async () => {
    setLoading(true);
    setApiError(null);
    try {
      const data = await api.get<Repository[]>("/api/v1/repositories");
      setRepositories(data);
    } catch (err: any) {
      setApiError(err.message || "리포지토리 정보를 가져오지 못했습니다.");
    } finally {
      setLoading(false);
    }
  };

  const toggleMonitoring = async (id: number) => {
    const target = repositories.find((repo) => repo.id === id);
    if (!target) return;

    const originalMonitored = target.isMonitored;

    // UI 즉시 반영 (낙관적 업데이트)
    setRepositories((prev) =>
      prev.map((repo) =>
        repo.id === id ? { ...repo, isMonitored: !originalMonitored } : repo
      )
    );

    try {
      await api.patch<Repository>(`/api/v1/repositories/${id}/monitor`, {
        isMonitored: !originalMonitored,
      });
    } catch (err: any) {
      // API 실패 시 롤백
      setRepositories((prev) =>
        prev.map((repo) =>
          repo.id === id ? { ...repo, isMonitored: originalMonitored } : repo
        )
      );
      alert("모니터링 상태 변경에 실패했습니다: " + err.message);
    }
  };

  // 3개월(90일) 경과 기준일 계산
  const checkIfNeedsCleanup = (dateString: string | null): boolean => {
    if (!dateString) return true; // 커밋 내역이 아예 없는 경우도 정리 대상으로 권장
    const lastCommit = new Date(dateString);
    const threeMonthsAgo = new Date();
    threeMonthsAgo.setMonth(threeMonthsAgo.getMonth() - 3);
    return lastCommit < threeMonthsAgo;
  };

  // 정리 대상(3개월 미갱신 및 모니터링 중) 리포지토리 필터
  const cleanupRequiredRepos = repositories.filter(
    (repo) => checkIfNeedsCleanup(repo.lastCommitAt) && repo.isMonitored
  );

  return (
    <div className="min-h-screen bg-darkBg text-white px-4 md:px-8 py-6 relative">
      {/* 네온 배경 효과 */}
      <div className="absolute top-0 right-0 w-80 h-80 bg-grassGreen-300 rounded-full filter blur-[180px] opacity-10" />

      {/* 내비게이션 헤더 */}
      <header className="max-w-6xl mx-auto flex items-center justify-between mb-8 pb-6 border-b border-gray-800 relative z-10">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 bg-grassGreen-300/10 border border-grassGreen-300/30 rounded-xl flex items-center justify-center">
            <span className="text-grassGreen-400 font-extrabold text-xl">G</span>
          </div>
          <span className="text-xl font-bold tracking-tight">GitGrass</span>
        </div>

        <div className="flex items-center gap-4">
          <button
            onClick={onNavigateToMyPage}
            className="px-4 py-2 text-sm font-semibold rounded-lg bg-gray-800 hover:bg-gray-700 transition"
          >
            🔔 알림 설정 (Discord)
          </button>
          <button
            onClick={onLogout}
            className="text-sm font-medium text-gray-400 hover:text-white transition"
          >
            로그아웃
          </button>
        </div>
      </header>

      <main className="max-w-6xl mx-auto relative z-10">
        {/* API 오류 발생 시 배너 */}
        {apiError && (
          <div className="mb-6 p-4 bg-red-950/20 border border-red-500/30 text-red-300 rounded-xl text-sm font-medium">
            {apiError}
          </div>
        )}

        {/* 로딩 화면 처리 */}
        {loading ? (
          <div className="flex flex-col items-center justify-center py-20">
            <div className="w-10 h-10 border-4 border-grassGreen-400 border-t-transparent rounded-full animate-spin mb-4" />
            <p className="text-gray-400 text-sm">깃허브 저장소를 동기화하는 중입니다...</p>
          </div>
        ) : (
          <>
            {/* 리포지토리 헬스 경고 배너 */}
            {cleanupRequiredRepos.length > 0 && (
              <div className="mb-8 p-6 bg-red-950/20 border border-red-500/30 rounded-2xl flex flex-col md:flex-row items-start md:items-center justify-between gap-4 shadow-xl">
                <div className="flex items-start gap-4">
                  <div className="p-3 bg-red-500/10 rounded-xl text-red-400 shrink-0">
                    <svg className="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                      <path strokeLinecap="round" strokeLinejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
                    </svg>
                  </div>
                  <div>
                    <h2 className="text-lg font-bold text-red-200">오래된 리포지토리/브랜치 경고</h2>
                    <p className="text-gray-400 text-sm mt-1">
                      최근 3개월 동안 커밋이 발생하지 않은 리포지토리 또는 브랜치가 <strong>{cleanupRequiredRepos.length}개</strong> 존재합니다. 모니터링을 취소하거나 브랜치를 삭제해 리소스를 확보하세요.
                    </p>
                  </div>
                </div>
              </div>
            )}

            {/* 상단 통계 영역 */}
            <section className="grid grid-cols-1 sm:grid-cols-3 gap-6 mb-8">
              <div className="glass p-6 rounded-2xl">
                <p className="text-xs text-gray-400 uppercase font-semibold">총 리포지토리</p>
                <p className="text-3xl font-bold mt-2">{repositories.length}개</p>
              </div>
              <div className="glass p-6 rounded-2xl">
                <p className="text-xs text-gray-400 uppercase font-semibold">모니터링 중</p>
                <p className="text-3xl font-bold mt-2 text-grassGreen-400">
                  {repositories.filter((r) => r.isMonitored).length}개
                </p>
              </div>
              <div className="glass p-6 rounded-2xl">
                <p className="text-xs text-gray-400 uppercase font-semibold">정리 권장 (3개월 미갱신)</p>
                <p className="text-3xl font-bold mt-2 text-red-400">
                  {repositories.filter((r) => checkIfNeedsCleanup(r.lastCommitAt)).length}개
                </p>
              </div>
            </section>

            {/* 리포지토리 리스트 헤더 */}
            <div className="flex items-center justify-between mb-6">
              <h2 className="text-xl font-bold">리포지토리 모니터링 관리</h2>
              <span className="text-xs text-gray-400">마지막 업데이트 기준 정렬</span>
            </div>

            {/* 리포지토리 그리드 */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {repositories.map((repo) => {
                const isOld = checkIfNeedsCleanup(repo.lastCommitAt);
                return (
                  <div key={repo.id} className="glass glass-interactive rounded-2xl p-6 flex flex-col justify-between">
                    <div>
                      <div className="flex items-center justify-between gap-2 mb-3">
                        <span className="text-xs text-gray-500 font-mono">{repo.owner}</span>
                        {isOld && (
                          <span className="px-2 py-0.5 text-[10px] font-bold bg-red-500/10 border border-red-500/20 text-red-400 rounded-full">
                            ⚠️ 3개월 미갱신
                          </span>
                        )}
                      </div>
                      <h3 className="text-lg font-bold text-white tracking-tight">{repo.name}</h3>
                      <div className="flex items-center gap-2 mt-2">
                        <span className="text-xs px-2 py-0.5 bg-gray-800 text-gray-300 rounded font-mono">
                          🌿 {repo.branchName || "main"}
                        </span>
                        <span className="text-xs text-gray-400">
                          마지막 커밋: {repo.lastCommitAt ? new Date(repo.lastCommitAt).toLocaleDateString() : "기록 없음"}
                        </span>
                      </div>
                    </div>

                    <div className="flex items-center justify-between mt-6 pt-4 border-t border-gray-800">
                      <span className="text-sm font-medium text-gray-400">모니터링 활성화</span>
                      <button
                        onClick={() => toggleMonitoring(repo.id)}
                        className={`relative inline-flex h-6 w-11 shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-none ${
                          repo.isMonitored ? "bg-grassGreen-400" : "bg-gray-700"
                        }`}
                      >
                        <span
                          className={`pointer-events-none inline-block h-5 w-5 transform rounded-full bg-white shadow ring-0 transition duration-200 ease-in-out ${
                            repo.isMonitored ? "translate-x-5" : "translate-x-0"
                          }`}
                        />
                      </button>
                    </div>
                  </div>
                );
              })}
            </div>
          </>
        )}
      </main>
    </div>
  );
};
