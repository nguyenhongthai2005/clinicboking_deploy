// src/components/user/Profile.tsx
import React, { useState, useRef } from 'react';
import { Container, Row, Col, Card, Nav, Tab, Button } from 'react-bootstrap';
import { useAuth } from '../../hooks/useAuth';

// import các component con
import BasicInfor from './BasicInfor';
import PatientRecord from './PatientRecord';
import ExaminationHistory from './ExaminationHistory';
import Appointment from './Appointment';


const Profile: React.FC = () => {
  const { user } = useAuth();
  const [activeTab, setActiveTab] = useState('basic-info');
  const [avatarImage, setAvatarImage] = useState<string | null>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);

  if (!user) {
    return <div>Không tìm thấy thông tin người dùng.</div>;
  }

  // Generate patient ID based on user ID
  const patientId = `BN-2024-${String(user.id).padStart(6, '0')}`;

  const handleAvatarClick = () => {
    fileInputRef.current?.click();
  };

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (file) {
      // Validate file type
      if (!file.type.startsWith('image/')) {
        alert('Vui lòng chọn file ảnh hợp lệ!');
        return;
      }

      // Validate file size (max 5MB)
      if (file.size > 5 * 1024 * 1024) {
        alert('Kích thước file không được vượt quá 5MB!');
        return;
      }

      // Create preview URL
      const reader = new FileReader();
      reader.onload = (e) => {
        setAvatarImage(e.target?.result as string);
      };
      reader.readAsDataURL(file);

      // TODO: Upload file to server
      console.log('Uploading file:', file);
    }
  };

  return (
    <div className="bg-light" style={{ minHeight: '100vh' }}>
      <div className="py-5">
        <Container>
          {/* Header Section */}
          <Row className="justify-content-center mb-4">
            <Col md={8}>
              <Card className="text-center border-0 shadow-sm bg-white">
                <Card.Body className="py-4">
                  <div
                    className="mx-auto mb-3 position-relative"
                    style={{
                      width: '80px',
                      height: '80px',
                      cursor: 'pointer',
                    }}
                    onClick={handleAvatarClick}
                  >
                    {/* Avatar Image or Default Icon */}
                    <div
                      className="rounded-circle d-flex align-items-center justify-content-center"
                      style={{
                        width: '80px',
                        height: '80px',
                        backgroundColor: avatarImage ? 'transparent' : '#6f42c1',
                        color: 'white',
                        fontSize: '2rem',
                        overflow: 'hidden',
                        border: '3px solid #6f42c1',
                      }}
                    >
                      {avatarImage ? (
                        <img
                          src={avatarImage}
                          alt="Avatar"
                          className="w-100 h-100"
                          style={{ objectFit: 'cover' }}
                        />
                      ) : (
                        '👤'
                      )}
                    </div>

                    {/* Plus Button */}
                    <Button
                      variant="primary"
                      size="sm"
                      className="position-absolute"
                      style={{
                        bottom: '0',
                        right: '0',
                        width: '24px',
                        height: '24px',
                        borderRadius: '50%',
                        backgroundColor: '#6f42c1',
                        borderColor: '#6f42c1',
                        border: '2px solid white',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        padding: '0',
                        fontSize: '12px',
                        fontWeight: 'bold',
                      }}
                      onClick={(e) => {
                        e.stopPropagation();
                        handleAvatarClick();
                      }}
                    >
                      +
                    </Button>
                  </div>

                  <h3 className="mb-2 fw-semibold" style={{ color: '#333' }}>
                    {user.fullName}
                  </h3>
                  <p className="mb-0 fs-5 text-muted">{patientId}</p>

                  {/* Hidden File Input */}
                  <input
                    ref={fileInputRef}
                    type="file"
                    accept="image/*"
                    onChange={handleFileChange}
                    className="d-none"
                  />
                </Card.Body>
              </Card>
            </Col>
          </Row>

          {/* Navigation Tabs */}
          <Row className="justify-content-center mb-4">
            <Col md={8}>
              <Card className="border-0 shadow-sm">
                <Card.Body className="p-0">
                  <Nav
                    variant="tabs"
                    className="border-0"
                    activeKey={activeTab}
                    onSelect={(key) => setActiveTab(key || 'basic-info')}
                  >
                    <Nav.Item>
                      <Nav.Link
                        eventKey="basic-info"
                        className={`fw-semibold ${
                          activeTab === 'basic-info' ? 'text-primary' : 'text-muted'
                        }`}
                        style={{
                          borderBottom:
                            activeTab === 'basic-info'
                              ? '3px solid #6f42c1'
                              : '3px solid transparent',
                          backgroundColor: 'transparent',
                        }}
                      >
                        Thông tin cơ bản
                      </Nav.Link>
                    </Nav.Item>
                    <Nav.Item>
                      <Nav.Link
                        eventKey="patient-record"
                        className={`fw-semibold ${
                          activeTab === 'patient-record'
                            ? 'text-primary'
                            : 'text-muted'
                        }`}
                        style={{
                          borderBottom:
                            activeTab === 'patient-record'
                              ? '3px solid #6f42c1'
                              : '3px solid transparent',
                          backgroundColor: 'transparent',
                        }}
                      >
                        Hồ sơ bệnh nhân
                      </Nav.Link>
                    </Nav.Item>
                    <Nav.Item>
                      <Nav.Link
                        eventKey="examination-history"
                        className={`fw-semibold ${
                          activeTab === 'examination-history'
                            ? 'text-primary'
                            : 'text-muted'
                        }`}
                        style={{
                          borderBottom:
                            activeTab === 'examination-history'
                              ? '3px solid #6f42c1'
                              : '3px solid transparent',
                          backgroundColor: 'transparent',
                        }}
                      >
                        Lịch sử khám
                      </Nav.Link>
                    </Nav.Item>
                    <Nav.Item>
                      <Nav.Link
                        eventKey="appointments"
                        className={`fw-semibold ${
                          activeTab === 'appointments'
                            ? 'text-primary'
                            : 'text-muted'
                        }`}
                        style={{
                          borderBottom:
                            activeTab === 'appointments'
                              ? '3px solid #6f42c1'
                              : '3px solid transparent',
                          backgroundColor: 'transparent',
                        }}
                      >
                        Lịch hẹn
                      </Nav.Link>
                    </Nav.Item>
                  </Nav>
                </Card.Body>
              </Card>
            </Col>
          </Row>

          {/* Content Area */}
          <Row className="justify-content-center">
            <Col md={8}>
              <Card className="border-0 shadow-sm">
                <Card.Body className="p-4">
                  <Tab.Content>
                    <Tab.Pane
                      eventKey="basic-info"
                      active={activeTab === 'basic-info'}
                    >
                      <BasicInfor user={user} />
                    </Tab.Pane>
                    <Tab.Pane
                      eventKey="patient-record"
                      active={activeTab === 'patient-record'}
                    >
                      <PatientRecord user={user} />
                    </Tab.Pane>
                    <Tab.Pane
                      eventKey="examination-history"
                      active={activeTab === 'examination-history'}
                    >
                      <ExaminationHistory user={user} />
                    </Tab.Pane>
                    <Tab.Pane
                      eventKey="appointments"
                      active={activeTab === 'appointments'}
                    >
                      <Appointment user={user} />
                    </Tab.Pane>
                  </Tab.Content>
                </Card.Body>
              </Card>
            </Col>
          </Row>
        </Container>
      </div>
    </div>
  );
};

export default Profile;
