import { useEffect, useState } from "react";
import { LoginView } from "./components/LoginView";
import { DashboardView } from "./components/DashboardView";
import { MyPageView } from "./components/MyPageView";

type ViewState = "login" | "dashboard" | "mypage";

function App() {
  const [view, setView] = useState<ViewState>("login");

  // 컴포넌트 마운트 시 URL 토큰 여부 확인 및 자동 로그인 처리
  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    const token = params.get("token");
    if (token) {
      localStorage.setItem("token", token);
      // 브라우저 주소 표시줄에서 지저분한 ?token=... 파라미터를 안 보이도록 정리
      window.history.replaceState({}, document.title, window.location.pathname);
      setView("dashboard");
    } else {
      const existingToken = localStorage.getItem("token");
      if (existingToken) {
        setView("dashboard");
      }
    }
  }, []);


  const handleNavigateToMyPage = () => {
    setView("mypage");
  };

  const handleNavigateToDashboard = () => {
    setView("dashboard");
  };

  const handleLogout = () => {
    localStorage.removeItem("token");
    setView("login");
  };

  return (
    <>
      {view === "login" && (
        <LoginView />
      )}
      {view === "dashboard" && (
        <DashboardView
          onNavigateToMyPage={handleNavigateToMyPage}
          onLogout={handleLogout}
        />
      )}
      {view === "mypage" && (
        <MyPageView onNavigateToDashboard={handleNavigateToDashboard} />
      )}
    </>
  );
}

export default App;
