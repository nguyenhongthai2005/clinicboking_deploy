import { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Button, Card, Carousel, Col, Container, Row, Spinner } from 'react-bootstrap';
import { fetchDoctorsAll, fetchDoctorById as fetchDoctorByIdApi, type Doctor as ApiDoctor } from '../../api/doctor';
import '../../styles/doctors-section.css';

type DoctorCard = {
  id: number | string;
  fullName: string;
  degree?: string;
  specialty?: string;
  specialtyName?: string;
  avatarUrl?: string;
  email?: string;
  phoneNumber?: string;
  description?: string;
  experience?: string;
};

type DoctorsSectionProps = {
  title?: string;
  doctors?: DoctorCard[];
  fetchDoctorById?: (id: number) => Promise<ApiDoctor>;
};

const VISIBLE_COUNT = 3;

function mapApiDoctorToCard(doctor: ApiDoctor): DoctorCard {
  const raw = doctor as unknown as Record<string, unknown>;
  const avatarUrl = typeof raw.avatarUrl === 'string'
    ? raw.avatarUrl
    : typeof raw.imageUrl === 'string'
      ? raw.imageUrl
      : undefined;
  const specialization = typeof raw.specialization === 'string' ? raw.specialization : undefined;

  return {
    id: doctor.id,
    fullName: doctor.fullName,
    degree: doctor.degree,
    specialtyName: doctor.specialtyName ?? specialization,
    avatarUrl,
    email: doctor.email,
    phoneNumber: doctor.phoneNumber,
    description: doctor.description,
    experience: doctor.experience,
  };
}

export default function DoctorsSection({ title = 'Đội ngũ CHUYÊN GIA - BÁC SĨ', doctors, fetchDoctorById }: DoctorsSectionProps) {
  const navigate = useNavigate();

  const [fetchedDoctors, setFetchedDoctors] = useState<DoctorCard[]>([]);
  const [loading, setLoading] = useState(false);
  const [bookingDoctorId, setBookingDoctorId] = useState<number | string | null>(null);

  // Use provided doctors or fetch from API
  const items = useMemo<DoctorCard[]>(() => {
    if (doctors && doctors.length > 0) return doctors;
    return fetchedDoctors;
  }, [doctors, fetchedDoctors]);

  const slides = useMemo<DoctorCard[][]>(() => {
    if (items.length === 0) return [];
    if (items.length <= VISIBLE_COUNT) return [items];

    const groups: DoctorCard[][] = [];
    for (let idx = 0; idx <= items.length - VISIBLE_COUNT; idx += 1) {
      groups.push(items.slice(idx, idx + VISIBLE_COUNT));
    }
    return groups;
  }, [items]);

  const [activeIndex, setActiveIndex] = useState(0);

  useEffect(() => {
    if (doctors && doctors.length > 0) {
      setFetchedDoctors([]);
      setLoading(false);
      return;
    }

    let ignore = false;
    setLoading(true);

    fetchDoctorsAll()
      .then((list) => {
        if (ignore) return;
        setFetchedDoctors(list.map(mapApiDoctorToCard));
      })
      .catch((err: unknown) => {
        if (ignore) return;
        console.error(err);
        setFetchedDoctors([]);
      })
      .finally(() => {
        if (ignore) return;
        setLoading(false);
      });

    return () => {
      ignore = true;
    };
  }, [doctors]);

  useEffect(() => {
    setActiveIndex(0);
  }, [slides.length]);

  const handleSelect = (selectedIndex: number) => {
    setActiveIndex(selectedIndex);
  };

  const canPrev = !loading && activeIndex > 0;
  const canNext = !loading && activeIndex < Math.max(slides.length - 1, 0);
  const showControls = slides.length > 1;

  const handlePrev = () => {
    if (!canPrev) return;
    setActiveIndex((idx) => Math.max(0, idx - 1));
  };

  const handleNext = () => {
    if (!canNext) return;
    setActiveIndex((idx) => Math.min(Math.max(slides.length - 1, 0), idx + 1));
  };

  const handleBook = async (doctor: DoctorCard) => {
    const targetId = doctor.id;
    const numericId = typeof targetId === 'string' ? Number(targetId) : targetId;
    const fetcher = fetchDoctorById ?? fetchDoctorByIdApi;
    let detail: ApiDoctor | DoctorCard = doctor;

    if (Number.isFinite(numericId)) {
      setBookingDoctorId(targetId);
      try {
        detail = await fetcher(Number(numericId));
      } catch (err) {
        console.error(err);
        window.alert('Không thể lấy thông tin bác sĩ đã chọn');
      } finally {
        setBookingDoctorId(null);
      }
    }

    navigate(`/appointment?doctorId=${targetId}`.trim(), {
      state: {
        doctor: detail,
      },
    });
  };

  if (!loading && items.length === 0) {
    return null;
  }

  return (
    <section className="doctors-section">
      <Container>
        <div className="doctors-section__top">
          <h2 className="doctors-section__title">{title}</h2>
          {showControls && (
            <div className="doctors-section__controls">
              <Button
                variant="outline-secondary"
                onClick={handlePrev}
                disabled={!canPrev}
                className="doctors-section__nav-btn"
                aria-label="Previous"
              >
                ‹
              </Button>
              <Button
                variant="outline-secondary"
                onClick={handleNext}
                disabled={!canNext}
                className="doctors-section__nav-btn"
                aria-label="Next"
              >
                ›
              </Button>
            </div>
          )}
        </div>

        {loading ? (
          <div className="doctors-section__loading">
            <Spinner animation="border" role="status" />
            <span>Đang tải bác sĩ...</span>
          </div>
        ) : (
          <Carousel
            activeIndex={activeIndex}
            onSelect={handleSelect}
            controls={false}
            indicators={showControls}
            interval={null}
            wrap={false}
            className="doctors-section__carousel"
            variant="dark"
          >
            {slides.map((group, slideIndex) => (
              <Carousel.Item key={slideIndex}>
                <Row className="g-4 justify-content-center">
                  {group.map((d) => (
                    <Col key={d.id} xs={12} md={6} lg={4} className="d-flex">
                      <Card className="doctors-section__card h-100 shadow-sm border-0">
                        <div className="doctors-section__image-wrapper">
                          {d.avatarUrl ? (
                            <img
                              src={d.avatarUrl}
                              alt={d.fullName}
                              className="doctors-section__image"
                            />
                          ) : (
                            <div className="doctors-section__image-placeholder">No Image</div>
                          )}
                        </div>
                        <Card.Body className="doctors-section__card-body">
                          <Card.Title as="h3" className="doctors-section__name">
                            {d.fullName}
                          </Card.Title>
                          <Card.Subtitle className="doctors-section__degree">
                            {d.degree || 'Bác sĩ'}
                          </Card.Subtitle>
                          <Card.Text className="doctors-section__specialty">
                            {d.specialtyName || d.specialty || 'Chuyên khoa'}
                          </Card.Text>
                          <Button
                            onClick={() => handleBook(d)}
                            variant="primary"
                            className="doctors-section__book-btn"
                            disabled={bookingDoctorId === d.id}
                          >
                            {bookingDoctorId === d.id ? 'Đang tải...' : 'Đặt lịch'}
                          </Button>
                        </Card.Body>
                      </Card>
                    </Col>
                  ))}
                </Row>
              </Carousel.Item>
            ))}
          </Carousel>
        )}
      </Container>
    </section>
  );
}
