# 🤖 **AI Prompt Log – AppointmentStatusService.changeStatus()**
### Mode: AI-Assisted Unit Testing with Mock Packages

---

## 🧩 Context

**Feature Tested:** `AppointmentStatusService.changeStatus()`  
**Objective:** Achieve ≥80% coverage using AI-generated unit tests  
**Frameworks:** Spring Boot, JUnit 5, Mockito, AssertJ  
**Mocks Used:**
- `UserRepositoryMock`
- `SlotRepositoryMock`
- `PrescriptionRepositoryMock`

---

## 🧠 Prompt 1 – Feature Analysis
**Prompt:**
> “Analyze the AppointmentStatusService class and identify the testable methods, core dependencies, and edge cases.”

**AI Output Summary:**
- Method under test: `changeStatus()`
- Dependencies: AppointmentRepo, UserRepo, SlotRepo, PrescriptionRepo
- Edge cases:
    - Invalid or null status
    - Missing actorUserId
    - Cancel completed appointment
    - Missing prescription before completion

---

## 💡 Prompt 2 – Test Case Matrix Design
**Prompt:**
> “Generate 15 test cases for AppointmentStatusService.changeStatus() including success and exception scenarios.”

**AI Output Summary:**
- Designed 15 cases covering:
    - All valid transitions
    - Illegal transitions
    - Slot release verification
    - Missing actor/prescription
- Total coverage target: ≥80%

---

## 🧪 Prompt 3 – Generate Full Test Code
**Prompt:**
> “Generate complete JUnit 5 + Mockito test class using external mock packages.”

**AI Output Summary:**
- Generated:
    - `AppointmentStatusServiceTest.java`
    - Integrated mocks from `mocks/`
- Added setup:
  ```java
  when(mapper.map(any(), any())).thenAnswer(inv -> new AppointmentDto(...));
  ```
- **Result:** 15 test cases PASS

---

## 🔍 Prompt 4 – Debug & Optimize
**Prompt:**
> “Help fix NullPointerException and UnnecessaryStubbingException in test.”

**AI Output Summary:**
- Added `@MockitoSettings(strictness = LENIENT)`
- Injected `SlotRepositoryMock` to avoid null slot
- Mocked `UserRepositoryMock.emptyUser()` for invalid user tests
- **Result:** 100% pass rate, coverage ↑ to 95%

---

## 🧰 Prompt 5 – Add Mock Packages
**Prompt:**
> “Create reusable mock files for UserRepository, SlotRepository, and PrescriptionRepository for better modular testing.”

**AI Output Summary:**
- Generated:
    - `UserRepositoryMock` → returns doctor/receptionist object
    - `SlotRepositoryMock` → sample `ShiftSlot`
    - `PrescriptionRepositoryMock` → returns boolean flags
- Improved isolation: no DB dependency.

---

## 📊 Prompt 6 – Documentation & Reporting
**Prompt:**
> “Generate README.md, AI_Prompt_Log.md, and TestCase Matrix following SWP format.”

**AI Output Summary:**
- Created structured 12-phase README
- Added AI prompt flow
- Coverage summary with metrics table

---

## ✅ Final Results

| Metric | Result |
|:--------|:--------|
| Test Cases | 15 |
| Framework | JUnit 5 + Mockito |
| Coverage | 95% Line, 86% Branch |
| Errors | 0 |
| AI Role | Analysis → Design → Code → Debug → Optimize → Document |
| Isolation | Fully Mocked |

---

## 🏁 Conclusion
AI-guided unit testing achieved **robust coverage and modular mocking**,  
meeting all exam criteria (**≥80% coverage**, **≥15 tests**, **mock usage**).

Demonstrates efficient synergy between **human understanding and AI reasoning**  
in **automated software testing**.
