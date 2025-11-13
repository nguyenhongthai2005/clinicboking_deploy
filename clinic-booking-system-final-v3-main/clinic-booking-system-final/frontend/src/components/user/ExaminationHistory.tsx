import { Card } from 'react-bootstrap';

interface ExaminationHistoryProps {
  user: any;
}

const ExaminationHistory: React.FC<ExaminationHistoryProps> = ({ user }) => {
  return (
    <div>
      <h4 className="mb-4">Lịch sử khám</h4>
      <Card className="border-0 bg-light">
        <Card.Body>
          <p className="text-muted mb-0">
            Lịch sử khám sẽ được hiển thị tại đây.
          </p>
        </Card.Body>
      </Card>
    </div>
  );
};

export default ExaminationHistory;


