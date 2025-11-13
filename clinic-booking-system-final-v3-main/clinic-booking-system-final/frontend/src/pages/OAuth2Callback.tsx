import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

export default function OAuth2Callback() {
  const { login } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    (async () => {
      try {
        const res = await fetch('/api/v1/auth/oauth2/user-info', {
          method: 'GET',
          credentials: 'include',
          headers: { 'Accept': 'application/json' },
        });
        if (!res.ok) throw new Error('OAuth2 user-info failed');
        const data = await res.json();
        if (!data?.token || !data?.user) throw new Error('Invalid OAuth2 response');
        login(
          {
            id: String(data.user.id),
            fullName: data.user.fullName,
            email: data.user.email,
            userType: data.user.userType,
          },
          data.token
        );
        navigate('/', { replace: true });
      } catch {
        navigate('/login', { replace: true });
      }
    })();
  }, [login, navigate]);

  return null;
}


