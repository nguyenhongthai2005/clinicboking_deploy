import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import Input from '../components/common/Input';
import Button from '../components/common/Button';
import { loginApi } from '../api/auth';
import { getErrorMessage } from '../utils/error';
import '../styles/login.css';
import { useToast } from '../components/common/ToastProvider';
import { useAuth } from '../hooks/useAuth';

export default function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [failedOnce, setFailedOnce] = useState(false);
  const navigate = useNavigate();
  const { showSuccess } = useToast();
  const { login } = useAuth();

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const res = await loginApi({ email, password });
      login(res.user, res.token);

      const role = (res?.user?.userType || res?.user?.userType?.[0] || "").toString();

      if (role === "Admin") {
        showSuccess('Đăng nhập thành công!');
        navigate("/admin/dashboard", { replace: true });
      } else if (role === "Patient") {
        showSuccess('Đăng nhập thành công!');
        navigate("/users/me", { replace: true });
      } else if (role === "Doctor") {
        showSuccess('Đăng nhập thành công!');
        navigate("/doctor/dashboard", { replace: true });
      } else if (role === "Receptionist") {
        showSuccess('Đăng nhập thành công!');
        navigate("/receptionist/dashboard", { replace: true });
      } else {
        showSuccess('Đăng nhập thành công!');
        navigate("/", { replace: true });
      }
    } catch (err: unknown) {
      setError(getErrorMessage(err) || 'Đăng nhập thất bại');
      setFailedOnce(true);
    } finally {
      setLoading(false);
    }
  }

  // Determine the backend base URL for OAuth2 (must NOT include /api or /api/v1)
  const authBase = (import.meta.env.VITE_API_URL
      ? String(import.meta.env.VITE_API_URL).replace(/\/(?:api)(?:\/v1)?\/?$/, '')
      : 'http://localhost:8080');

  return (
    <section className="login-page">
      <div className="login-card">
        <form onSubmit={onSubmit} className="login-form">
          <Input
            id="email"
            name="email"
            label="Email"
            type="email"
            value={email}
            placeholder="you@example.com"
            onChange={(e) => setEmail(e.target.value)}
            required
          />

          <Input
            id="password"
            name="password"
            label="Mật khẩu"
            type="password"
            value={password}
            placeholder="••••••••"
            onChange={(e) => setPassword(e.target.value)}
            required
          />

          {error && (
            <div className="error-block">
              <p>{error}</p>
              {failedOnce && (
                <Link to="/forgot-password" className="btn-forgot">Quên mật khẩu?</Link>
              )}
            </div>
          )}

          <Button type="submit" disabled={loading} className="btn-auth">
            {loading ? 'Đang xử lý…' : 'Đăng nhập'}
          </Button>

          <p className="login-register">
            Chưa có tài khoản? <Link to="/register">Đăng ký ngay</Link>
          </p>

          <div className="social-group">
            <button type="button" className="btn-social btn-google" onClick={() => (window.location.href = `${authBase}/oauth2/authorization/google`)}>
              <span aria-hidden>G</span> Đăng nhập với Google
            </button>
            <button type="button" className="btn-social btn-facebook" onClick={() => (window.location.href = `${authBase}/oauth2/authorization/facebook`)}>
              <span aria-hidden>f</span> Đăng nhập với Facebook
            </button>
          </div>
        </form>
      </div>
    </section>
  );
}