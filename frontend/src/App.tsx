import { useState } from "react";
import { LoginView } from "./components/LoginView";
import { DashboardView } from "./components/DashboardView";
import { MyPageView } from "./components/MyPageView";

type ViewState = "login" | "dashboard" | "mypage";

function App() {
  const [view, setView] = useState<ViewState>("login");

  const handleLoginSuccess = () => {
    setView("dashboard");
  };

  const handleNavigateToMyPage = () => {
    setView("mypage");
  };

  const handleNavigateToDashboard = () => {
    setView("dashboard");
  };

  const handleLogout = () => {
    setView("login");
  };

  return (
    <>
      {view === "login" && (
        <LoginView onLoginSuccess={handleLoginSuccess} />
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
