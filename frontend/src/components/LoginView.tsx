import React from "react";

interface LoginViewProps {
  onLoginSuccess: () => void;
}

export const LoginView: React.FC<LoginViewProps> = ({ onLoginSuccess }) => {
  const handleGithubLogin = () => {
    // 실제 운영 시에는 백엔드의 OAuth2 엔드포인트로 이동합니다.
    // window.location.href = "http://localhost:8080/oauth2/authorization/github";
    
    // MVP 화면 데모 검증을 위해 프론트 임시 로그인 처리
    onLoginSuccess();
  };

  return (
    <div className="min-h-screen flex flex-col items-center justify-center bg-darkBg relative overflow-hidden px-4">
      {/* 백그라운드 데코레이션 네온 원 */}
      <div className="absolute top-1/4 left-1/4 w-96 h-96 bg-grassGreen-200 rounded-full filter blur-[150px] opacity-15 animate-pulse" />
      <div className="absolute bottom-1/4 right-1/4 w-96 h-96 bg-grassGreen-400 rounded-full filter blur-[150px] opacity-10 animate-pulse" />

      {/* 로그인 메인 카드 */}
      <div className="w-full max-w-md glass rounded-3xl p-8 md:p-10 flex flex-col items-center relative z-10 shadow-2xl">
        {/* 서비스 로고 */}
        <div className="w-20 h-20 bg-grassGreen-300/10 border border-grassGreen-300/30 rounded-2xl flex items-center justify-center mb-6">
          <svg
            className="w-12 h-12 text-grassGreen-400 animate-pulse"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
            strokeWidth={1.5}
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              d="M12 3v18M3 12h18m-9-9l9 9-9 9-9-9 9-9z"
            />
          </svg>
        </div>

        <h1 className="text-3xl font-extrabold tracking-tight text-white mb-2">
          Git<span className="text-grassGreen-400">Grass</span>
        </h1>
        <p className="text-gray-400 text-sm text-center mb-8 max-w-xs">
          잊혀가는 깃허브 리포지토리를 모니터링하고, 완벽한 일일 잔디를 유지하세요.
        </p>

        {/* 깃허브로 시작하기 버튼 */}
        <button
          onClick={handleGithubLogin}
          className="w-full py-4 px-6 bg-white hover:bg-gray-100 text-gray-900 font-bold rounded-xl flex items-center justify-center gap-3 transition duration-300 shadow-lg hover:shadow-white/5 active:scale-[0.98]"
        >
          <svg className="w-5 h-5 fill-current" viewBox="0 0 24 24">
            <path fillRule="evenodd" clipRule="evenodd" d="M12 2C6.477 2 2 6.477 2 12c0 4.42 2.87 8.17 6.84 9.5.5.08.66-.23.66-.5v-1.69c-2.77.6-3.36-1.34-3.36-1.34-.46-1.16-1.11-1.47-1.11-1.47-.9-.62.07-.6.07-.6 1 .07 1.53 1.03 1.53 1.03.9 1.52 2.34 1.07 2.91.83.1-.65.35-1.09.63-1.34-2.22-.25-4.55-1.11-4.55-4.92 0-1.11.38-2 1.03-2.71-.1-.25-.45-1.29.1-2.64 0 0 .84-.27 2.75 1.02.79-.22 1.65-.33 2.5-.33.85 0 1.71.11 2.5.33 1.91-1.29 2.75-1.02 2.75-1.02.55 1.35.2 2.39.1 2.64.65.71 1.03 1.6 1.03 2.71 0 3.82-2.34 4.66-4.57 4.91.36.31.69.92.69 1.85V21c0 .27.16.59.67.5C19.14 20.16 22 16.42 22 12A10 10 0 0012 2z" />
          </svg>
          GitHub로 시작하기
        </button>

        <div className="mt-8 flex items-center justify-between w-full text-xs text-gray-500 border-t border-gray-800 pt-6">
          <span>Version 1.0.0 (MVP)</span>
          <span className="flex items-center gap-1">
            <span className="w-2 h-2 rounded-full bg-emerald-500 animate-ping" />
            System Live
          </span>
        </div>
      </div>
    </div>
  );
};
