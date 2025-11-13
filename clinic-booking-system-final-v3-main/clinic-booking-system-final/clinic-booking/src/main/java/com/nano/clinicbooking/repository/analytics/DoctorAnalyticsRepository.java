package com.nano.clinicbooking.repository.analytics;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DoctorAnalyticsRepository {

    @PersistenceContext
    private EntityManager em;

    public List<Object[]> findTopDoctors(int limit) {
        String jpql = """
            SELECT 
                a.doctor.id,
                a.doctor.fullName,
                a.doctor.specialty.name,
                COUNT(a.id) AS totalAppointments
            FROM Appointment a
            WHERE a.status IN ('CONFIRMED', 'COMPLETED')
            GROUP BY a.doctor.id, a.doctor.fullName, a.doctor.specialty.name
            ORDER BY totalAppointments DESC
        """;

        return em.createQuery(jpql, Object[].class)
                .setMaxResults(limit)
                .getResultList();
    }
}
