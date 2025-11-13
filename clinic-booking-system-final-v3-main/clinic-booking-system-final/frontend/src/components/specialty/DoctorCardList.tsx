import { Row, Col, Card } from 'react-bootstrap';
import type { Doctor } from '../../api/doctor';

interface DoctorCardListProps {
  doctors: Doctor[];
}

export default function DoctorCardList({ doctors }: DoctorCardListProps) {
  if (doctors.length === 0) {
    return (
      <div className="text-center py-5">
        <p className="text-muted">Không tìm thấy bác sĩ nào.</p>
      </div>
    );
  }

  return (
    <Row className="g-4">
      {doctors.map((doctor) => (
        <Col key={doctor.id} md={6} lg={4}>
          <Card className="h-100 shadow-sm">
            <Card.Body>
              <Card.Title>{doctor.fullName}</Card.Title>
              {doctor.degree && (
                <Card.Subtitle className="mb-2 text-muted">{doctor.degree}</Card.Subtitle>
              )}
              {doctor.specialtyName && (
                <p className="text-primary small mb-2">{doctor.specialtyName}</p>
              )}
              {doctor.experience && (
                <p className="text-muted small">Kinh nghiệm: {doctor.experience}</p>
              )}
              {doctor.description && (
                <Card.Text className="small">{doctor.description}</Card.Text>
              )}
            </Card.Body>
          </Card>
        </Col>
      ))}
    </Row>
  );
}

