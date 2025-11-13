import { Row, Col, Form } from 'react-bootstrap';

interface BasicInforProps {
  user: any;
}

const BasicInfor: React.FC<BasicInforProps> = ({ user }) => {
  return (
    <div>
      <h4 className="mb-4">Thông tin cơ bản</h4>
      <Form>
        <Row>
          <Col md={6} className="mb-3">
            <Form.Group>
              <Form.Label>Họ và tên</Form.Label>
              <Form.Control
                type="text"
                value={user.fullName || ''}
                readOnly
              />
            </Form.Group>
          </Col>
          <Col md={6} className="mb-3">
            <Form.Group>
              <Form.Label>Email</Form.Label>
              <Form.Control
                type="email"
                value={user.email || ''}
                readOnly
              />
            </Form.Group>
          </Col>
          <Col md={6} className="mb-3">
            <Form.Group>
              <Form.Label>Số điện thoại</Form.Label>
              <Form.Control
                type="text"
                value={user.phoneNumber || ''}
                readOnly
              />
            </Form.Group>
          </Col>
          <Col md={6} className="mb-3">
            <Form.Group>
              <Form.Label>Ngày sinh</Form.Label>
              <Form.Control
                type="date"
                value={user.dob || ''}
                readOnly
              />
            </Form.Group>
          </Col>
          <Col md={6} className="mb-3">
            <Form.Group>
              <Form.Label>Giới tính</Form.Label>
              <Form.Control
                type="text"
                value={user.gender || ''}
                readOnly
              />
            </Form.Group>
          </Col>
          <Col md={6} className="mb-3">
            <Form.Group>
              <Form.Label>Địa chỉ</Form.Label>
              <Form.Control
                type="text"
                value={user.address || ''}
                readOnly
              />
            </Form.Group>
          </Col>
        </Row>
      </Form>
    </div>
  );
};

export default BasicInfor;


