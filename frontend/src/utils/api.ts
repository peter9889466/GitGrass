const BASE_URL = ""; // Vite 개발 서버 프록시를 경유하므로 상대 경로로 요청합니다.

async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
  const token = localStorage.getItem("token");
  
  const headers = new Headers(options.headers);
  if (token) {
    headers.set("Authorization", `Bearer ${token}`);
  }
  
  // JSON body가 포함된 전송일 때 Content-Type을 자동으로 명시합니다.
  if (options.body && !(options.body instanceof FormData)) {
    headers.set("Content-Type", "application/json");
  }

  const response = await fetch(`${BASE_URL}${path}`, {
    ...options,
    headers,
  });

  if (!response.ok) {
    if (response.status === 401) {
      // 세션 만료 및 인증 헤더가 유효하지 않은 경우 스토리지 초기화 후 새로고침
      localStorage.removeItem("token");
      window.location.reload();
    }
    const errorText = await response.text();
    throw new Error(errorText || `요청 실패 (HTTP ${response.status})`);
  }

  const contentType = response.headers.get("content-type");
  if (contentType && contentType.includes("application/json")) {
    return response.json() as Promise<T>;
  }
  return {} as T;
}

export const api = {
  get: <T>(path: string, options?: RequestInit) => request<T>(path, { ...options, method: "GET" }),
  post: <T>(path: string, body?: any, options?: RequestInit) => request<T>(path, {
    ...options,
    method: "POST",
    body: body ? JSON.stringify(body) : undefined
  }),
  put: <T>(path: string, body?: any, options?: RequestInit) => request<T>(path, {
    ...options,
    method: "PUT",
    body: body ? JSON.stringify(body) : undefined
  }),
  patch: <T>(path: string, body?: any, options?: RequestInit) => request<T>(path, {
    ...options,
    method: "PATCH",
    body: body ? JSON.stringify(body) : undefined
  }),
  delete: <T>(path: string, options?: RequestInit) => request<T>(path, { ...options, method: "DELETE" }),
};
