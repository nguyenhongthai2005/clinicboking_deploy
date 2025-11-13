import { Container, Row, Col, Card, Button } from 'react-bootstrap';
import { NavLink } from 'react-router-dom';
import '../../styles/features-section.css';

export default function FeaturesSection() {
  return (
    <section className="features-section py-5">
      <Container>
        <Row className="g-4">
          <Col md={4}>
            <Card className="h-100 shadow-sm border-0">
              <Card.Body className="p-4 d-flex flex-column justify-content-between">
                <div>
                  <h3 className="mb-3 text-primary fw-bold">Chủ động chăm sóc sức khỏe</h3>
                  <p className="text-muted">
                    Không cần chờ đợi hay xếp hàng dài - bạn có thể chủ động đặt lịch khám bệnh trực tuyến mọi lúc, mọi nơi. 
                    Hệ thống thông minh giúp bạn lựa chọn đúng chuyên khoa, đúng bác sĩ và thời gian thuận tiện.
                  </p>
                </div>
                <NavLink to="/appointment" className="mt-3">
                  <Button variant="primary" className="w-100 fw-bold">
                    ĐĂNG KÝ NGAY
                  </Button>
                </NavLink>
              </Card.Body>
            </Card>
          </Col>
          <Col md={4}>
            <Card className="h-100 shadow-sm border-0">
              <Card.Body className="p-4 d-flex flex-column justify-content-between">
                <div>
                  <h3 className="mb-3 text-primary fw-bold">Đội ngũ Y, Bác sĩ uy tín</h3>
                  <p className="text-muted">
                    Chúng tôi tự hào sở hữu đội ngũ bác sĩ giàu kinh nghiệm, có chuyên môn sâu trong từng lĩnh vực và 
                    luôn đặt sự an toàn của bệnh nhân lên hàng đầu. Sẵn sàng lắng nghe và đồng hành cùng bạn trong hành trình chăm sóc sức khỏe.
                  </p>
                </div>
                <NavLink to="/doctors" className="mt-3">
                  <Button variant="primary" className="w-100 fw-bold">
                    CHUYÊN GIA - BÁC SĨ
                  </Button>
                </NavLink>
              </Card.Body>
            </Card>
          </Col>
          <Col md={4}>
            <Card className="h-100 shadow-sm border-0">
              <Card.Body className="p-4 d-flex flex-column justify-content-between">
                <div>
                  <h3 className="mb-3 text-primary fw-bold">Khám đúng chuyên khoa</h3>
                  <p className="text-muted">
                    Hệ thống phòng khám cung cấp đầy đủ các chuyên khoa như Cơ Xương Khớp, Răng Hàm Mặt, Mắt giúp bạn 
                    dễ dàng lựa chọn dịch vụ phù hợp với nhu cầu bản thân, đảm bảo hiệu quả và sự an tâm tuyệt đối.
                  </p>
                </div>
                <NavLink to="/appointment" className="mt-3">
                  <Button variant="primary" className="w-100 fw-bold">
                    CHUYÊN KHOA
                  </Button>
                </NavLink>
              </Card.Body>
            </Card>
          </Col>
        </Row>
      </Container>
    </section>
  );
}

