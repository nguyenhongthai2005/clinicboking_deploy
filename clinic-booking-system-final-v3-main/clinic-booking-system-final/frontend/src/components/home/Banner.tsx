import { Carousel, Container } from 'react-bootstrap';
import { NavLink } from 'react-router-dom';
import { carouselData } from '../../data/carouselData';
import '../../styles/banner.css';

export default function Banner() {
  return (
    <>
      <section className="banner" aria-label="Banner nổi bật">
        <Carousel
          controls
          indicators
          pause="hover"
          interval={5000}   // ms (null để tắt auto)
          fade              // bỏ nếu muốn slide ngang
          touch
          keyboard
        >
          {carouselData.map((s, i) => (
            <Carousel.Item key={i}>
              <img className="d-block w-100 banner-img" src={s.src} alt={s.alt} />
            </Carousel.Item>
          ))}
        </Carousel>
      </section>

      {/* Quick actions đè lên cạnh dưới của banner */}
      <section className="quick-action" aria-label="Tác vụ nhanh">
        <Container className="quick-action__inner">
          <NavLink to="/appointment" className="qa-btn qa-primary">Đăng ký ngay</NavLink>
          <NavLink to="/doctors" className="qa-btn qa-outline">Tìm bác sĩ</NavLink>
          <NavLink to="/about" className="qa-btn qa-light">Tìm hiểu thêm</NavLink>
        </Container>
      </section>
    </>
  );
}