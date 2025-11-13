# 🧪 AI-Assisted Unit Testing Report
### Feature: `AppointmentStatusService.changeStatus()`
**Version:** With Mock Package Integration
---

## 🗓 **1/12 – Overview**

**Project:** Clinic Booking System  
**Feature Under Test:** `AppointmentStatusService.changeStatus()`  
**Language:** Java 21  
**Frameworks:** Spring Boot · JUnit 5 · Mockito · AssertJ

**Mock Packages:**
- `com.nano.clinicbooking.mocks.UserRepositoryMock`
- `com.nano.clinicbooking.mocks.SlotRepositoryMock`
- `com.nano.clinicbooking.mocks.PrescriptionRepositoryMock`

**Goal:**  
⏱ Duration: 3 hours  
🧩 15 Test Cases  
🎯 ≥ 80% Coverage → ✅ **Achieved 95%**

---

## 🕒 **2/12 – Testing Timeline **

| Phase | Duration | Description |
|:------|:----------|:------------|
| **1️⃣ Analysis** | 15’ | Identify logic, inputs, dependencies |
| **2️⃣ Design** | 20’ | Create test matrix (15 cases) |
| **3️⃣ Coding** | 75’ | Write JUnit 5 tests + mocks |
| **4️⃣ Debugging** | 40’ | Resolve Mockito & logic errors |
| **5️⃣ Optimization** | 15’ | Improve coverage & mock isolation |
| **6️⃣ Documentation** | 15’ | Prepare README, logs & matrix |

> 🧭 Each phase contributes to final coverage & reliability.

---

## ⚙️ **3/12 – Phase 1: Feature Analysis **

### 🎯 Core Feature
`AppointmentStatusService.changeStatus()` — manages appointment lifecycle transitions.

### 🧩 Dependencies
- `AppointmentRepository`
- `UserRepository`
- `PrescriptionRepository`
- `ShiftSlotRepository`
- `ModelMapper`

### 🔁 Main Transitions

| Transition | Description |
|-------------|-------------|
| `PENDING_CONFIRMATION → CONFIRMED` | Receptionist approves |
| `CONFIRMED → CHECKED_IN` | Patient arrives |
| `CHECKED_IN → IN_PROGRESS` | Doctor starts exam |
| `IN_PROGRESS → COMPLETED` | Appointment finished |
| `ANY → CANCELLED` | Cancelled by patient/staff |
| `ANY → RESCHEDULED` | Rebooked to a new slot |

---

## 💡 **4/12 – AI Prompt #1: Feature Analysis**

**Prompt Used:**
> “Analyze `AppointmentStatusService` and identify all methods that should be tested.  
> List dependencies, possible transitions, and edge cases for `changeStatus()`.”
> 
> Dependencies: AppointmentRepo, UserRepo, SlotRepo, PrescriptionRepo


**AI Output Summary:**
- Core method: `changeStatus()`
- Transition handlers: confirm, check-in, in-progress, complete, cancel
- Edge cases:
    - Invalid status (null)
    - Missing actor user
    - Missing prescription before completion
    - Cancelling completed appointment
---

## 🧩 **5/12 – Phase 2: Test Case Design **

**Prompt Used:**
> “Generate 15 test cases for `AppointmentStatusService.changeStatus()` including valid transitions and exception scenarios.”

### ✅ AI-Designed Test Categories

| Category | Test Case | Expected Result |
|:----------|:-----------|:----------------|
| Happy Path | Confirm pending | Status → `CONFIRMED` |
| Happy Path | Check-in confirmed | Status → `CHECKED_IN` |
| Happy Path | Start consultation | Status → `IN_PROGRESS` |
| Happy Path | Complete with prescription | Status → `COMPLETED` |
| Happy Path | Cancel appointment | Slot released |
| Edge | Complete w/o prescription | `IllegalStateException` |
| Edge | Cancel completed | `IllegalStateException` |
| Edge | Invalid `newStatus = null` | `IllegalStateException` |
| Error | Actor not found | `ResourceNotFoundException` |
| Error | Appointment not found | `ResourceNotFoundException` |
| Validation | Confirm non-pending | `IllegalStateException` |
| Validation | Check-in wrong state | `IllegalStateException` |
| Validation | Start before check-in | `IllegalStateException` |
| Other | Reschedule appointment | Status → `RESCHEDULED` |
| Other | Cancel without slot | No `slotRepo.save()` called |

🎯 **Total:** 15 test cases (TC01 – TC15)

---

## 💻 **6/12 – Phase 3: Test Implementation **

**Prompt Used:**
> “Generate full JUnit 5 + Mockito test class for `AppointmentStatusService` with 15 test cases.  
> Integrate custom mock packages for Slot, User, and Prescription.”

### 🧰 Techniques
- `@ExtendWith(MockitoExtension.class)`
- `@MockitoSettings(strictness = LENIENT)`
- AssertJ fluent assertions
- Fully mocked repositories (no DB)

### 📦 Mock Packages
| Mock Class | Purpose |
|-------------|----------|
| `UserRepositoryMock` | Simulate receptionist/doctor retrieval |
| `SlotRepositoryMock` | Provide sample `ShiftSlot` data |
| `PrescriptionRepositoryMock` | Toggle prescription presence |

---

## 🧪 **7/12 – Example Test Snippets**

```java
@Test
void shouldConfirmPendingAppointment() {
    when(appointmentRepo.findById(1L)).thenReturn(Optional.of(appointment));
    when(userRepo.findById(2L)).thenReturn(UserRepositoryMock.mockDoctor(2L));
    when(appointmentRepo.save(any())).thenReturn(appointment);

    AppointmentDto result = service.changeStatus(1L, AppointmentStatus.CONFIRMED, 2L);

    assertThat(result.getStatus()).isEqualTo(AppointmentStatus.CONFIRMED);
    verify(appointmentRepo).save(any());
}

@Test
void shouldThrowWhenCompleteWithoutPrescription() {
    appointment.setStatus(AppointmentStatus.IN_PROGRESS);
    when(appointmentRepo.findById(1L)).thenReturn(Optional.of(appointment));
    when(prescriptionRepo.existsByAppointmentId(1L))
        .thenReturn(PrescriptionRepositoryMock.noPrescription());

    assertThatThrownBy(() ->
        service.changeStatus(1L, AppointmentStatus.COMPLETED, null)
    ).isInstanceOf(IllegalStateException.class);
}
```

---

## 🧰 **8/12 – Phase 4: Debugging **

| Issue | Resolution |
|:-------|:------------|
| `NullPointerException` from slot | Added `SlotRepositoryMock` |
| `UnnecessaryStubbingException` | Set `@MockitoSettings(strictness = LENIENT)` |
| Invalid user mock | Fixed via `UserRepositoryMock` |
| Coverage < 80% | Added exception test paths |

✅ **All 15 tests passed successfully.**

---

## ⚡ **9/12 – Phase 5: Optimization & Mocking **

### 1️⃣ Coverage Optimization
- Added missing paths: *reschedule* & *cancel-without-slot*
- Verified repository save() calls via `verify()`

### 2️⃣ Isolated Mocking
Mocks simulate repository behavior **without** database dependency.

```java
when(prescriptionRepo.existsByAppointmentId(1L))
    .thenReturn(PrescriptionRepositoryMock.hasPrescription());
```

✅ **Final Coverage:**

| Metric | Result |
|:--------|:--------|
| Line Coverage | **95%** |
| Branch Coverage | **86%** |

---

## 📚 **10/12 – Phase 6: Documentation & Demo **

### 📁 Folder Structure

```
src/test/java/com/nano/clinicbooking/
 ├── mocks/
 │   ├── UserRepositoryMock.java
 │   ├── SlotRepositoryMock.java
 │   └── PrescriptionRepositoryMock.java
 └── service/Appointment/
     └── AppointmentStatusServiceTest.java
```

### ▶️ Run Commands

```bash
./mvnw clean test
./mvnw clean test jacoco:report
```

### 🧾 Result
✅ **15/15 Tests Passed**  
✅ **JaCoCo Report: 95% Coverage**

---

## 🏁 **11/12 – Final Results**

| Metric | Result |
|:--------|:--------|
| Feature Tested | `AppointmentStatusService.changeStatus()` |
| Framework | JUnit 5 + Mockito + AssertJ |
| Total Test Cases | 15 |
| Line Coverage | 95% |
| Branch Coverage | 86% |
| Errors | 0 |
| AI Role | Analysis → Design → Code → Debug → Optimize → Document |

---

## 🧾 **12/12 – Conclusion**

The **AI-assisted unit testing workflow** successfully produced a  
robust and isolated test suite for `AppointmentStatusService.changeStatus()`.

All 15 test cases executed successfully with **95% code coverage**,  
using fully mocked repositories and structured verification steps.

This project demonstrates effective **human–AI collaboration**  
in software testing, ensuring both quality and reproducibility.

---

### 📦 Deliverables

```
/reports/
 ├── README_UnitTest.md
 ├── AI_Prompt_Log.md
 ├── testcase_matrix.xlsx
 ├── test_log.md
 └── screenshots/jacoco_report.png
```
