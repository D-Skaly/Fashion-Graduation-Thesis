import { useState, useEffect, useCallback } from 'react';
import api from '@/lib/axios';

interface UserProfile {
  id: string;
  email: string;
  fullName: string;
  role: string;
  avatar?: string;
}

interface UseAuthReturn {
  isAuthenticated: boolean;
  user: UserProfile | null;
  loading: boolean;
  logout: () => Promise<void>;
}

export function useAuth(): UseAuthReturn {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [user, setUser] = useState<UserProfile | null>(null);
  const [loading, setLoading] = useState(true);

  const checkAuth = useCallback(async () => {
    try {
      const { data } = await api.get('/auth/me');
      setUser(data);
      setIsAuthenticated(true);
    } catch (error) {
      setUser(null);
      setIsAuthenticated(false);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    checkAuth();
  }, [checkAuth]);

  const logout = async () => {
    try {
      await api.post('/auth/logout');
    } catch (error) {
      console.error('Logout error:', error);
    } finally {
      setUser(null);
      setIsAuthenticated(false);
      window.location.href = '/login';
    }
  };

  return { isAuthenticated, user, loading, logout };
}
