import { Card } from 'react-bootstrap';

interface AppointmentProps {
  user: any;
}

const Appointment: React.FC<AppointmentProps> = ({ user }) => {
  return (
    <div>
      <h4 className="mb-4">Lịch hẹn</h4>
      <Card className="border-0 bg-light">
        <Card.Body>
          <p className="text-muted mb-0">
            Lịch hẹn sẽ được hiển thị tại đây.
          </p>
        </Card.Body>
      </Card>
    </div>
  );
};

export default Appointment;


