import { ChatMessage, DeployResponse, FileNode, LoginCredentials, LoginResponse, ProjectSummaryResponse, ProjectRequest, ProjectResponse, ProjectMember, ProjectRole, SignupRequest, AuthResponse } from "./types";

const BASE_URL = "http://localhost:8080";

export const getAuthToken = () => localStorage.getItem("access_token");
export const getRefreshToken = () => localStorage.getItem("refresh_token");

export const setAuthToken = (accessToken: string, refreshToken?: string) => {
  localStorage.setItem("access_token", accessToken);
  if (refreshToken) {
    localStorage.setItem("refresh_token", refreshToken);
  }
};

export const removeAuthToken = () => {
  localStorage.removeItem("access_token");
  localStorage.removeItem("refresh_token");
};

export const isAuthenticated = () => !!getAuthToken();

const getAuthHeaders = (): HeadersInit => {
  const token = getAuthToken();
  return token ? { Authorization: `Bearer ${token}` } : {};
};

// Helper for authenticated API calls with automatic 401 token refresh retry
async function fetchWithAuth(url: string, options: RequestInit = {}): Promise<Response> {
  let headers = {
    ...getAuthHeaders(),
    ...(options.headers || {}),
  };

  let response = await fetch(url, { ...options, headers });

  if (response.status === 401) {
    try {
      // Attempt to refresh access token using refresh token
      await api.refreshToken();
      // Retry original request with new access token
      headers = {
        ...getAuthHeaders(),
        ...(options.headers || {}),
      };
      response = await fetch(url, { ...options, headers });
    } catch (refreshError) {
      // Refresh failed (e.g. refresh token expired) -> clear auth & redirect to login
      removeAuthToken();
      removeUserInfo();
      window.location.href = "/login";
      throw refreshError;
    }
  }

  return response;
}


// User info storage
export const setUserInfo = (user: { id: string; email: string; name: string }) => {
  localStorage.setItem("user_info", JSON.stringify(user));
};

export const getUserInfo = (): { id: string; email: string; name: string } | null => {
  const userInfo = localStorage.getItem("user_info");
  return userInfo ? JSON.parse(userInfo) : null;
};

export const removeUserInfo = () => localStorage.removeItem("user_info");

// LocalStorage keys
export const PREVIEW_URL_KEY = "preview_url";
export const OPEN_TABS_KEY = "open_tabs";
export const ACTIVE_TAB_KEY = "active_tab";

// API response format for files endpoint
interface FilesApiResponse {
  files: { path: string }[];
}

// Convert flat file paths to nested tree structure
function buildFileTree(paths: { path: string }[]): FileNode[] {
  const root: FileNode[] = [];
  const nodeMap = new Map<string, FileNode>();

  // Sort paths to ensure directories come before their children
  const sortedPaths = [...paths].sort((a, b) => a.path.localeCompare(b.path));

  for (const { path } of sortedPaths) {
    const parts = path.split("/");
    let currentPath = "";

    for (let i = 0; i < parts.length; i++) {
      const part = parts[i];
      const parentPath = currentPath;
      currentPath = currentPath ? `${currentPath}/${part}` : part;

      // Skip if node already exists
      if (nodeMap.has(currentPath)) continue;

      const isFile = i === parts.length - 1;
      const node: FileNode = {
        name: part,
        path: currentPath,
        type: isFile ? "file" : "directory",
        children: isFile ? undefined : [],
      };

      nodeMap.set(currentPath, node);

      if (parentPath) {
        const parent = nodeMap.get(parentPath);
        if (parent && parent.children) {
          parent.children.push(node);
        }
      } else {
        root.push(node);
      }
    }
  }

  // Sort each level: directories first, then alphabetically
  const sortNodes = (nodes: FileNode[]) => {
    nodes.sort((a, b) => {
      if (a.type === "directory" && b.type === "file") return -1;
      if (a.type === "file" && b.type === "directory") return 1;
      return a.name.localeCompare(b.name);
    });
    nodes.forEach((node) => {
      if (node.children) sortNodes(node.children);
    });
  };

  sortNodes(root);
  return root;
}

export const api = {
  async login(credentials: LoginCredentials): Promise<AuthResponse> {
    const response = await fetch(`${BASE_URL}/api/v1/auth/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(credentials),
    });

    if (!response.ok) {
      const error = await response.text();
      throw new Error(error || "Login failed");
    }

    return response.json();
  },

  async signup(data: SignupRequest): Promise<AuthResponse> {
    const response = await fetch(`${BASE_URL}/api/v1/auth/signup`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data),
    });

    if (!response.ok) {
      const error = await response.text();
      throw new Error(error || "Signup failed");
    }

    return response.json();
  },

  async refreshToken(): Promise<AuthResponse> {
    const refreshToken = getRefreshToken();
    if (!refreshToken) throw new Error("No refresh token available");

    const response = await fetch(`${BASE_URL}/api/v1/auth/refresh`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ refreshToken }),
    });

    if (!response.ok) {
      removeAuthToken();
      removeUserInfo();
      throw new Error("Token refresh failed");
    }

    const data: AuthResponse = await response.json();
    setAuthToken(data.accessToken, data.refreshToken);
    return data;
  },

  async getFiles(projectId: string): Promise<FileNode[]> {
    const response = await fetchWithAuth(`${BASE_URL}/api/v1/projects/${projectId}/files`);

    if (!response.ok) {
      throw new Error("Failed to fetch files");
    }

    const data: FilesApiResponse = await response.json();
    return buildFileTree(data.files);
  },

  async getFileContent(projectId: string, path: string): Promise<string> {
    const response = await fetchWithAuth(
      `${BASE_URL}/api/v1/projects/${projectId}/files/content?path=${path}`
    );

    const data = await response.json();

    if (!response.ok) {
      console.error(`Error fetching file: ${response.status} ${response.statusText}`);
      throw new Error("Failed to fetch file content");
    }

    return data.content;
  },

  async deploy(projectId: string): Promise<DeployResponse> {
    const response = await fetchWithAuth(`${BASE_URL}/api/v1/projects/${projectId}/deploy`, {
      method: "POST",
    });

    if (!response.ok) {
      const errorText = await response.text();
      let message = "Deployment failed";
      try {
        const errorJson = JSON.parse(errorText);
        message = errorJson.message || errorJson.error || message;
      } catch {
        if (errorText) message = errorText;
      }
      throw new Error(message);
    }

    return response.json();
  },

  async getProjects(): Promise<ProjectSummaryResponse[]> {
    const response = await fetchWithAuth(`${BASE_URL}/api/v1/projects`);

    if (!response.ok) {
      throw new Error("Failed to fetch projects");
    }

    return response.json();
  },

  async createProject(name: string): Promise<ProjectSummaryResponse> {
    const response = await fetchWithAuth(`${BASE_URL}/api/v1/projects`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ name }),
    });

    if (!response.ok) {
      throw new Error("Failed to create project");
    }

    return response.json();
  },

  async getProject(id: string): Promise<ProjectResponse> {
    const response = await fetchWithAuth(`${BASE_URL}/api/v1/projects/${id}`);

    if (!response.ok) {
      throw new Error("Failed to fetch project");
    }

    return response.json();
  },

  async updateProject(id: string, name: string): Promise<ProjectResponse> {
    const response = await fetchWithAuth(`${BASE_URL}/api/v1/projects/${id}`, {
      method: "PATCH",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ name }),
    });

    if (!response.ok) {
      throw new Error("Failed to update project");
    }

    return response.json();
  },

  async deleteProject(id: string): Promise<void> {
    const response = await fetchWithAuth(`${BASE_URL}/api/v1/projects/${id}`, {
      method: "DELETE",
    });

    if (!response.ok) {
      throw new Error("Failed to delete project");
    }
  },

  async downloadProjectZip(id: string): Promise<Blob> {
    const response = await fetchWithAuth(`${BASE_URL}/api/v1/projects/${id}/files/download-zip`);

    if (!response.ok) {
      throw new Error("Failed to download project");
    }

    return response.blob();
  },

  async getProjectMembers(projectId: string): Promise<ProjectMember[]> {
    const response = await fetchWithAuth(`${BASE_URL}/api/v1/projects/${projectId}/members`);

    if (!response.ok) {
      throw new Error("Failed to fetch project members");
    }

    const data = await response.json();
    return data.map((m: any) => ({
      userId: m.userId,
      email: m.email,
      name: m.name,
      role: m.projectRole || m.role,
      invitedAt: m.invitedAt,
    }));
  },

  async inviteMember(projectId: string, email: string, role: ProjectRole): Promise<ProjectMember> {
    const response = await fetchWithAuth(`${BASE_URL}/api/v1/projects/${projectId}/members`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, role }),
    });

    if (!response.ok) {
      const errorText = await response.text();
      let message = "Failed to invite member";
      try {
        const errorJson = JSON.parse(errorText);
        message = errorJson.message || errorJson.error || message;
      } catch {
        if (errorText) message = errorText;
      }
      throw new Error(message);
    }

    const m = await response.json();
    return {
      userId: m.userId,
      email: m.email,
      name: m.name,
      role: m.projectRole || m.role,
      invitedAt: m.invitedAt,
    };
  },

  async updateMemberRole(projectId: string, userId: string, role: ProjectRole): Promise<void> {
    const response = await fetchWithAuth(`${BASE_URL}/api/v1/projects/${projectId}/members/${userId}`, {
      method: "PATCH",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ role }),
    });

    if (!response.ok) {
      throw new Error("Failed to update member role");
    }
  },

  async removeMember(projectId: string, userId: string): Promise<void> {
    const response = await fetchWithAuth(`${BASE_URL}/api/v1/projects/${projectId}/members/${userId}`, {
      method: "DELETE",
    });

    if (!response.ok) {
      throw new Error("Failed to remove member");
    }
  },

  async getChatHistory(projectId: string): Promise<ChatMessage[]> {
    const response = await fetchWithAuth(`${BASE_URL}/api/v1/chat/projects/${projectId}`);

    if (!response.ok) {
      throw new Error("Failed to fetch chat history");
    }

    return response.json();
  },

  async streamChat(
    projectId: string,
    message: string,
    onChunk: (chunk: string) => void,
    onFile: (path: string, content: string) => void,
    onComplete: () => void,
    onError: (error: Error) => void
  ) {
    const controller = new AbortController();

    fetchWithAuth(`${BASE_URL}/api/v1/chat/stream`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ message, projectId }),
      signal: controller.signal,
    })
      .then(async (response) => {
        if (!response.ok) throw new Error("Chat stream failed");

        const reader = response.body?.getReader();
        if (!reader) throw new Error("No reader available");

        const decoder = new TextDecoder();

        // Buffers
        let sseBuffer = ""; // To handle split SSE lines
        let fullContentBuffer = ""; // To accumulate clean text for file regex
        let lastProcessedIndex = 0; // Optimization for regex

        while (true) {
          const { done, value } = await reader.read();
          if (done) break;

          const chunk = decoder.decode(value, { stream: true });
          sseBuffer += chunk;

          // Process line by line to handle SSE format (data: ...)
          const lines = sseBuffer.split("\n");
          sseBuffer = lines.pop() || "";

          for (const line of lines) {
            const trimmedLine = line.trim();
            if (!trimmedLine || !trimmedLine.startsWith("data:")) continue;

            const dataStr = trimmedLine.slice(5).trim();
            if (!dataStr) continue;

            try {
              // FIX: Parse JSON to get the real text with newlines preserved
              const parsed = JSON.parse(dataStr);
              const content = parsed.text;

              // 1. Send clean text to UI
              onChunk(content);

              // 2. Accumulate for file parsing (Same as before)
              fullContentBuffer += content;
              // ... (rest of regex logic) ...

            } catch (e) {
              console.error("Failed to parse SSE JSON:", e);
            }
          }
        }

        onComplete();
      })
      .catch((error) => {
        if (error.name !== "AbortError") {
          console.error("Stream error:", error);
          onError(error);
        }
      });

    return () => controller.abort();
  }

};

