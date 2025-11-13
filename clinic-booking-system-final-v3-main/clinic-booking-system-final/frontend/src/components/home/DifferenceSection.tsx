import { Container, Row, Col, Carousel } from 'react-bootstrap';
import { useState, useMemo } from 'react';

const carouselReviewImages = [
  '/picture/carouselReview/anh1.png',
  '/picture/carouselReview/anh2.png',
  '/picture/carouselReview/anh3.png',
  '/picture/carouselReview/anh4.png',
  '/picture/carouselReview/anh5.png',
  '/picture/carouselReview/anh6.png',
  '/picture/carouselReview/anh7.png',
  '/picture/carouselReview/anh8.png',
  '/picture/carouselReview/anh9.png',
];

const VISIBLE_COUNT = 3;

export default function DifferenceSection() {
  const [activeIndex, setActiveIndex] = useState(0);

  const slides = useMemo(() => {
    if (carouselReviewImages.length === 0) return [];
    if (carouselReviewImages.length <= VISIBLE_COUNT) return [carouselReviewImages];

    const groups: string[][] = [];
    // Chia ảnh thành các nhóm 3 ảnh, không lặp lại
    for (let idx = 0; idx < carouselReviewImages.length; idx += VISIBLE_COUNT) {
      groups.push(carouselReviewImages.slice(idx, idx + VISIBLE_COUNT));
    }
    return groups;
  }, []);

  const handleSelect = (selectedIndex: number) => {
    setActiveIndex(selectedIndex);
  };

  return (
    <section className="difference-section py-5 bg-light">
      <Container>
        <Row className="mb-4">
          <Col md={12} className="text-center">
            <h2 className="display-5 fw-bold text-primary mb-4">CHÚNG TÔI CÓ SỰ KHÁC BIỆT</h2>
            <p className="lead text-muted mx-auto" style={{ maxWidth: '900px' }}>
              Hệ thống phòng khám của chúng tôi là nơi tinh hoa y học châu Âu hội tụ cùng sự ân cần, nồng hậu của người châu Á. 
              Chúng tôi tạo ra một môi trường hiện đại, thoải mái, nơi bác sĩ là những người bạn thân của bạn, 
              và bạn chính là trung tâm của dịch vụ chất lượng cao với giá phải chăng.
            </p>
          </Col>
        </Row>

        {slides.length > 0 && (
          <Row className="mt-5">
            <Col md={12}>
              <Carousel
                activeIndex={activeIndex}
                onSelect={handleSelect}
                controls={true}
                indicators={true}
                interval={5000}
                pause="hover"
                className="difference-carousel"
              >
                {slides.map((group, slideIndex) => (
                  <Carousel.Item key={slideIndex}>
                    <Row className="g-3">
                      {group.map((imgSrc, imgIndex) => (
                        <Col key={imgIndex} md={4}>
                          <div className="difference-image-wrapper">
                            <img
                              src={imgSrc}
                              alt={`Hình ảnh phòng khám ${slideIndex * VISIBLE_COUNT + imgIndex + 1}`}
                              className="img-fluid rounded shadow-sm"
                              style={{ width: '100%', height: '300px', objectFit: 'cover' }}
                            />
                          </div>
                        </Col>
                      ))}
                    </Row>
                  </Carousel.Item>
                ))}
              </Carousel>
            </Col>
          </Row>
        )}
      </Container>
    </section>
  );
}

