import { createContext, useState, useEffect, useContext } from 'react';
import type { ReactNode } from 'react';
import { useNavigate } from 'react-router-dom';
import type { UserType } from '../types/auth';

type User = {
  id?: number | string;
  fullName?: string;
  email?: string;
  phoneNumber?: string;
  userType?: string;
};

type AuthContextType = {
  user: User | null;
  userType: UserType;
  isAuthenticated: boolean;
  login: (userData: User, token: string) => void;
  logout: () => void;
};

const AuthContext = createContext<AuthContextType>({
  user: null,
  userType: 'Guest',
  isAuthenticated: false,
  login: () => {},
  logout: () => {},
});


export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [user, setUser] = useState<User | null>(null);
  const navigate = useNavigate();

  useEffect(() => {
    const storedUser = localStorage.getItem('user');
    if (storedUser) {
      const parsed = JSON.parse(storedUser);
      setUser(parsed);
    }
  }, []);

  const login = (userData: User, token: string) => {
    const formattedUser = { ...userData };
    localStorage.setItem('access_token', token);
    localStorage.setItem('user', JSON.stringify(formattedUser));
    setUser(formattedUser);
  };

  const logout = async () => {
    try {
      await fetch(`${import.meta.env.VITE_API_URL}/auth/oauth2/logout`, {
      method: 'POST',
      credentials: 'include',
    });
    } catch (err) {
      console.error('Logout network error:', err);
    } finally {
      localStorage.removeItem('access_token');
      localStorage.removeItem('user');
      setUser(null);
      navigate('/');
    }
  };

  function toUserType(value?: string): UserType {
    const v = (value || 'Guest');
    if (v === 'Guest' || v === 'Patient' || v === 'Doctor' || v === 'Receptionist' || v === 'Admin') {
      return v as UserType;
    }
    return 'Guest';
  }

  const userType: UserType = toUserType(user?.userType);
  const isAuthenticated = !!user;

  return (
    <AuthContext.Provider value={{ user, userType, isAuthenticated, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);