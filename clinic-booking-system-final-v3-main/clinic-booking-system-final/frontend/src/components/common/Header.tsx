import { NavLink, Link } from 'react-router-dom';
import NavDropdown from 'react-bootstrap/NavDropdown';
import logo from '../../assets/logo.png';
import '../../styles/header.css';
import { useAuth } from '../../hooks/useAuth';
import { useEffect, useState } from 'react';
import { fetchSpecialties, type Specialty } from '../../api/specialty';


export default function Header(){
  const headerClass = 'header';
  const { user, userType, isAuthenticated, logout } = useAuth();
  const [specialties, setSpecialties] = useState<Specialty[]>([]);
  const [loadingSpecs, setLoadingSpecs] = useState<boolean>(false);

  useEffect(() => {
    let alive = true;
    setLoadingSpecs(true);
    fetchSpecialties()
      .then((sp) => { if (alive) setSpecialties(sp); })
      .catch(() => { /* silent */ })
      .finally(() => { if (alive) setLoadingSpecs(false); });
    return () => { alive = false; };
  }, []);

  const linkClass = ({isActive}:{isActive:boolean}) =>
    `navlink ${isActive ? 'active' : ''}`;

  return (
    <header className={headerClass} role="banner">
      <div className="header-inner">
        <Link to="/" className="logo-box" aria-label="Trang chủ">
          <img src={logo} alt="Clinic Logo"/>
        </Link>

        <nav className="nav" aria-label="Chính">
          <NavLink to="/" end className={linkClass}>Trang chủ</NavLink>
          <NavDropdown title="Chuyên khoa" id="specialty-dropdown">
            {loadingSpecs && (
              <NavDropdown.Item disabled>Đang tải...</NavDropdown.Item>
            )}
            {!loadingSpecs && specialties.length === 0 && (
              <NavDropdown.Item disabled>Không có dữ liệu</NavDropdown.Item>
            )}
            {!loadingSpecs && specialties.map((s) => (
              <NavDropdown.Item as={Link} to={`/specialty/${s.id}`} key={s.id}>
                {s.name}
              </NavDropdown.Item>
            ))}
          </NavDropdown>

          <NavLink to="/doctors" end className={linkClass}>Bác sĩ</NavLink>
          <NavLink to="/services" end className={linkClass}>Dịch vụ</NavLink>
          <NavLink to="/about" end className={linkClass}>Giới thiệu</NavLink>
        </nav>

        <div className="login-wrap">
          {!isAuthenticated || userType === 'Guest' ? (
            <>
              <NavLink to="/appointment" className="btn login">Đặt lịch khám</NavLink>
              <NavLink to="/login" className="btn login login--ghost">Đăng nhập</NavLink>
            </>
          ) : (
            <>
            <NavLink to="/appointment" className="btn login">Đặt lịch khám</NavLink>
            <NavDropdown className="btn login login--ghost" title={user?.fullName || 'User'} id="user-dropdown">
              <NavDropdown.Item as={Link} to="/users/me">Xem thông tin</NavDropdown.Item>
              <NavDropdown.Item as={Link} to="/vouchers">Xem voucher</NavDropdown.Item>
              <NavDropdown.Divider />
              <NavDropdown.Item onClick={() => { logout(); }}>Đăng xuất</NavDropdown.Item>
            </NavDropdown>
            </>
          )}
        </div>
      </div>
    </header>
  );
}
