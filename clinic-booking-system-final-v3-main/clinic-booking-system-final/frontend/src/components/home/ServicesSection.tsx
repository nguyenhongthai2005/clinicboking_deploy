import { Container, Row, Col, Card, ListGroup } from 'react-bootstrap';
import { NavLink } from 'react-router-dom';
import '../../styles/services-section.css';

const services = [
  'Khám và điều trị đau cột sống, đau khớp gối',
  'Tiêm nội khớp, vật lý trị liệu phục hồi chức năng',
  'Tư vấn điều trị thoái hóa khớp, viêm khớp dạng thấp',
  'Chẩn đoán hình ảnh: X-quang, MRI, siêu âm khớp',
  'Nhổ răng khôn, trám răng, điều trị tủy',
  'Tẩy trắng răng bằng công nghệ laser',
  'Niềng răng - chỉnh nha thẩm mỹ',
  'Cấy ghép Implant và phục hình răng sứ',
  'Khám thị lực, đo kính, tư vấn điều chỉnh tật khúc xạ',
  'Điều trị viêm kết mạc, khô mắt, đau mắt đỏ',
  'Phẫu thuật đục thủy tinh thể, mộng thịt',
  'Tư vấn và theo dõi bệnh lý võng mạc, tăng nhãn áp',
];

const specialties = [
  {
    title: 'Khoa Nội Tổng Quát',
    description: 'Chăm sóc sức khỏe toàn diện, phát hiện và điều trị sớm các bệnh lý nội khoa. Chuyên chẩn đoán và điều trị các bệnh về tim mạch, hô hấp, tiêu hóa, nội tiết, thận - tiết niệu với đội ngũ bác sĩ giàu kinh nghiệm.',
  },
  {
    title: 'Chuyên khoa Cơ Xương Khớp',
    description: 'Tập trung chẩn đoán và điều trị các bệnh lý liên quan đến hệ vận động như đau lưng, thoái hóa khớp, viêm khớp, loãng xương, thoát vị đĩa đệm. Với đội ngũ bác sĩ chuyên môn cao và thiết bị hỗ trợ hiện đại, giúp bệnh nhân cải thiện chất lượng sống và khả năng vận động.',
  },
  {
    title: 'Chuyên khoa Răng Hàm Mặt',
    description: 'Cung cấp các dịch vụ chăm sóc răng miệng toàn diện, từ phòng ngừa đến điều trị chuyên sâu. Với công nghệ nha khoa hiện đại và đội ngũ bác sĩ tận tâm, mang đến trải nghiệm khám chữa nhẹ nhàng, hiệu quả và thẩm mỹ.',
  },
];

export default function ServicesSection() {
  return (
    <section className="services-section py-5">
      <Container>
        <Row className="g-4">
          <Col md={5}>
            <Card className="h-100 shadow-sm border-0">
              <Card.Body className="p-4">
                <h3 className="mb-4 text-white bg-primary p-3 rounded fw-bold">
                  DỊCH VỤ ĐIỀU TRỊ NỔI BẬT
                </h3>
                <ListGroup variant="flush" className="mb-3">
                  {services.map((service, index) => (
                    <ListGroup.Item key={index} className="px-0 py-2 border-0">
                      <span className="text-primary me-2">•</span>
                      {service}
                    </ListGroup.Item>
                  ))}
                </ListGroup>
                <NavLink to="/appointment" className="text-primary text-decoration-none fw-bold">
                  Xem tất cả các dịch vụ →
                </NavLink>
              </Card.Body>
            </Card>
          </Col>
          <Col md={7}>
            <Row className="g-4">
              {specialties.map((specialty, index) => (
                <Col key={index} md={12}>
                  <Card className="h-100 shadow-sm border-0">
                    <Card.Body className="p-4">
                      <h4 className="text-primary fw-bold mb-3">{specialty.title}</h4>
                      <p className="text-muted mb-3">{specialty.description}</p>
                      <NavLink to="/appointment" className="text-primary text-decoration-none fw-bold">
                        Tìm hiểu thêm →
                      </NavLink>
                    </Card.Body>
                  </Card>
                </Col>
              ))}
            </Row>
          </Col>
        </Row>
      </Container>
    </section>
  );
}

