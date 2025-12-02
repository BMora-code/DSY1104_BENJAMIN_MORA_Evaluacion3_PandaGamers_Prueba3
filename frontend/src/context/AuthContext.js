// src/context/AuthContext.js
import { createContext, useState, useEffect } from "react";

export const AuthContext = createContext();

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(null);

  // Cargar usuario y token desde localStorage al iniciar
  useEffect(() => {
    const savedUser = localStorage.getItem('auth_user');
    const savedToken = localStorage.getItem('token');
    if (savedUser) {
      try {
        setUser(JSON.parse(savedUser));
      } catch (error) {
        console.warn('Error loading user from localStorage:', error);
        localStorage.removeItem('auth_user');
      }
    }
    if (savedToken) setToken(savedToken);
  }, []);

  // login acepta (token, userData) o un solo objeto { token, user }
  const login = (a, b) => {
    let newToken = null;
    let userData = null;
    if (typeof a === 'string' && b) {
      newToken = a;
      userData = b;
    } else if (a && typeof a === 'object') {
      newToken = a.token;
      userData = a.user || a;
    }

    if (userData) {
      const hasDuocDiscount = userData.email && userData.email.toLowerCase().endsWith('@duocuc.cl');
      const userWithDiscount = { ...userData, hasDuocDiscount };
      setUser(userWithDiscount);
      localStorage.setItem('auth_user', JSON.stringify(userWithDiscount));
    }

    if (newToken) {
      setToken(newToken);
      localStorage.setItem('token', newToken);
    }
  };

  const logout = () => {
    setUser(null);
    setToken(null);
    localStorage.removeItem('auth_user');
    localStorage.removeItem('token');
  };

  return (
    <AuthContext.Provider value={{ user, token, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}
