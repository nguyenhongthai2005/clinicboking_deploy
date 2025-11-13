import { Card } from 'react-bootstrap';

interface PatientRecordProps {
  user: any;
}

const PatientRecord: React.FC<PatientRecordProps> = ({ user }) => {
  return (
    <div>
      <h4 className="mb-4">Hồ sơ bệnh nhân</h4>
      <Card className="border-0 bg-light">
        <Card.Body>
          <p className="text-muted mb-0">
            Hồ sơ bệnh nhân sẽ được hiển thị tại đây.
          </p>
        </Card.Body>
      </Card>
    </div>
  );
};

export default PatientRecord;


