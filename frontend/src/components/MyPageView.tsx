import React, { useState, useEffect } from "react";
import { api } from "../utils/api";

interface DiscordConfig {
  webhookUrl: string;
  isActive: boolean;
  alertTime: string;
}

interface MyPageViewProps {
  onNavigateToDashboard: () => void;
}

export const MyPageView: React.FC<MyPageViewProps> = ({ onNavigateToDashboard }) => {
  const [config, setConfig] = useState<DiscordConfig>({
    webhookUrl: "",
    isActive: false,
    alertTime: "22:00",
  });

  const [message, setMessage] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    fetchDiscordConfig();
  }, []);

  const fetchDiscordConfig = async () => {
    try {
      const data = await api.get<{ webhookUrl: string | null; alertTime: string; isActive: boolean }>(
        "/api/v1/users/me/discord"
      );
      setConfig({
        webhookUrl: data.webhookUrl || "",
        isActive: data.isActive,
        alertTime: data.alertTime,
      });
    } catch (err: any) {
      setError("설정을 로드하지 못했습니다: " + err.message);
    }
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setConfig((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleToggleActive = () => {
    setConfig((prev) => ({
      ...prev,
      isActive: !prev.isActive,
    }));
  };

  const validateWebhookUrl = (url: string): boolean => {
    if (!url) return false;
    return url.startsWith("https://discord.com/api/webhooks/");
  };

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    setMessage(null);
    setError(null);

    if (config.isActive && !validateWebhookUrl(config.webhookUrl)) {
      setError("올바른 Discord Webhook URL 형식이 아닙니다.");
      return;
    }

    try {
      await api.put("/api/v1/users/me/discord", config);
      setMessage("설정이 성공적으로 저장되었습니다. 🌿");
      setTimeout(() => setMessage(null), 3000);
    } catch (err: any) {
      setError("설정 저장에 실패했습니다: " + err.message);
    }
  };

  const handleUnlink = async () => {
    setMessage(null);
    setError(null);
    try {
      await api.delete("/api/v1/users/me/discord");
      setConfig({
        webhookUrl: "",
        isActive: false,
        alertTime: "22:00",
      });
      setMessage("디스코드 연동이 해제되었습니다.");
      setTimeout(() => setMessage(null), 3000);
    } catch (err: any) {
      setError("연동 해제에 실패했습니다: " + err.message);
    }
  };

  return (
    <div className="min-h-screen bg-darkBg text-white px-4 md:px-8 py-6 relative flex items-center justify-center">
      {/* 네온 배경 효과 */}
      <div className="absolute bottom-0 left-0 w-80 h-80 bg-indigo-600 rounded-full filter blur-[180px] opacity-10" />

      <div className="w-full max-w-lg glass rounded-3xl p-6 md:p-8 relative z-10 shadow-2xl">
        {/* 뒤로 가기 버튼 */}
        <button
          onClick={onNavigateToDashboard}
          className="mb-6 flex items-center gap-2 text-sm text-gray-400 hover:text-white transition group"
        >
          <svg className="w-5 h-5 transform group-hover:-translate-x-1 transition" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
            <path strokeLinecap="round" strokeLinejoin="round" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
          </svg>
          대시보드로 돌아가기
        </button>

        {/* 타이틀 및 헤더 */}
        <div className="flex items-center gap-4 mb-6">
          <div className="w-12 h-12 bg-indigo-500/10 border border-indigo-500/30 rounded-xl flex items-center justify-center text-indigo-400">
            <svg className="w-7 h-7 fill-current" viewBox="0 0 127.14 96.36">
              <path d="M107.7,8.07A105.15,105.15,0,0,0,77.26,0a77.19,77.19,0,0,0-3.3,6.83A96.67,96.67,0,0,0,53.22,6.83,77.19,77.19,0,0,0,49.88,0,105.15,105.15,0,0,0,19.44,8.07C3.66,31.58-1.95,54.65,1,77.53a107.53,107.53,0,0,0,32,16.29,80.82,80.82,0,0,0,6.72-11A69.3,69.3,0,0,1,28.26,77.22a50.81,50.81,0,0,0,4.24-3.3C53.45,83.47,73.82,83.47,94.64,73.92a50.81,50.81,0,0,0,4.24,3.3,69.3,69.3,0,0,1-11.45,5.65,80.82,80.82,0,0,0,6.72,11,107.53,107.53,0,0,0,32-16.29C129.78,48.43,123.63,25.64,107.7,8.07ZM42.45,65.69C36.18,65.69,31,60,31,53S36.18,40.36,42.45,40.36,53.86,46,53.86,53,48.72,65.69,42.45,65.69Zm42.24,0C78.41,65.69,73.24,60,73.24,53S78.41,40.36,84.69,40.36,96.1,46,96.1,53,91,65.69,84.69,65.69Z" />
            </svg>
          </div>
          <div>
            <h2 className="text-xl font-bold">Discord 알림 연동 (선택)</h2>
            <p className="text-xs text-gray-400 mt-1">지정한 마감 시간까지 커밋이 없는 날 리마인드를 보내줍니다.</p>
          </div>
        </div>

        {/* 상태 메시지 배너 */}
        {message && (
          <div className="mb-6 p-4 bg-emerald-950/20 border border-emerald-500/30 text-emerald-300 rounded-xl text-sm font-medium animate-fade-in">
            {message}
          </div>
        )}
        {error && (
          <div className="mb-6 p-4 bg-red-950/20 border border-red-500/30 text-red-300 rounded-xl text-sm font-medium animate-fade-in">
            {error}
          </div>
        )}

        {/* 연동/해제 폼 */}
        <form onSubmit={handleSave} className="space-y-6">
          <div className="flex items-center justify-between p-4 bg-gray-800/40 rounded-2xl border border-gray-800">
            <div>
              <p className="text-sm font-semibold">디스코드 알림 켜기</p>
              <p className="text-xs text-gray-500 mt-0.5">매일 설정된 시간에 커밋 미완료 시 전송</p>
            </div>
            <button
              type="button"
              onClick={handleToggleActive}
              className={`relative inline-flex h-6 w-11 shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-none ${
                config.isActive ? "bg-indigo-500" : "bg-gray-700"
              }`}
            >
              <span
                className={`pointer-events-none inline-block h-5 w-5 transform rounded-full bg-white shadow ring-0 transition duration-200 ease-in-out ${
                  config.isActive ? "translate-x-5" : "translate-x-0"
                }`}
              />
            </button>
          </div>

          {config.isActive && (
            <>
              <div>
                <label className="block text-xs font-semibold uppercase text-gray-400 mb-2">Discord Webhook URL</label>
                <input
                  type="text"
                  name="webhookUrl"
                  value={config.webhookUrl}
                  onChange={handleInputChange}
                  placeholder="https://discord.com/api/webhooks/..."
                  className="w-full bg-gray-900 border border-gray-800 focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500 rounded-xl px-4 py-3 text-sm outline-none transition"
                  required
                />
              </div>

              <div>
                <label className="block text-xs font-semibold uppercase text-gray-400 mb-2">알림 마감 시간</label>
                <select
                  name="alertTime"
                  value={config.alertTime}
                  onChange={handleInputChange}
                  className="w-full bg-gray-900 border border-gray-800 focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500 rounded-xl px-4 py-3 text-sm outline-none transition"
                >
                  <option value="18:00">오후 6:00 (18:00)</option>
                  <option value="20:00">오후 8:00 (20:00)</option>
                  <option value="21:00">오후 9:00 (21:00)</option>
                  <option value="22:00">오후 10:00 (22:00)</option>
                  <option value="23:00">오후 11:00 (23:00)</option>
                </select>
              </div>
            </>
          )}

          <div className="flex gap-4 pt-4 border-t border-gray-800">
            <button
              type="submit"
              className="flex-1 py-3 px-4 bg-indigo-600 hover:bg-indigo-500 text-white font-bold rounded-xl transition shadow-lg hover:shadow-indigo-500/20 active:scale-[0.98]"
            >
              설정 저장
            </button>
            {config.webhookUrl && (
              <button
                type="button"
                onClick={handleUnlink}
                className="py-3 px-4 bg-transparent hover:bg-red-500/10 border border-red-500/30 hover:border-red-500/50 text-red-400 font-bold rounded-xl transition"
              >
                연동 해제
              </button>
            )}
          </div>
        </form>
      </div>
    </div>
  );
};
