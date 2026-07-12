import { createContext, useContext, useState, useCallback } from 'react';

const AuthContext = createContext(null);

function decodeToken(token) {
  try {
    const payload = token.split('.')[1];
    return JSON.parse(atob(payload));
  } catch {
    return null;
  }
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const token = localStorage.getItem('assetflow_token');
    return token ? decodeToken(token) : null;
  });

  const login = useCallback((token) => {
    localStorage.setItem('assetflow_token', token);
    setUser(decodeToken(token));
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem('assetflow_token');
    setUser(null);
  }, []);

  const value = { user, role: user?.role ?? null, isAuthenticated: !!user, login, logout };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
