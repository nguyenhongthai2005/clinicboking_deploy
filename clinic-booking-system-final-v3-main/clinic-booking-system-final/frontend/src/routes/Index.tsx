import { Routes, Route } from 'react-router-dom';
import RequireAuth from './_guards/RequireAuth';
import RequireRole from './_guards/RequireRole';

// Pages dùng chung (guest + patient)
import PublicLayout from '../components/common/PublicLayout';
import Home from '../pages/guest/Home';
import Doctors from '../pages/guest/Doctors';
import Login from '../pages/Login';
import Register from '../pages/Register';
import Appointment from '../pages/guest/Appointment';
import About from '../pages/guest/About';
import Services from '../pages/guest/Services';
import SpecialtyDetail from '../pages/guest/SpecialtyDetail';
import OAuth2Callback from '../pages/OAuth2Callback';
import RegisterAdmin from '../pages/admin/RegisterAdmin';

// Trang patient
import Profile from '../components/user/Profile';

//Trang bị lỗi
import NotFound from '../components/errors/NotFound';
import Forbidden from '../components/errors/Forbidden';

//Trang admin
import AdminLayout from '../components/layout/adminlayouts/AdminLayout';
import AdminDashboard from '../pages/admin/AdminDashboard';
import AdminDoctor from '../pages/admin/AdminDoctor';
import AdminSpecialty from '../pages/admin/AdminSpecialty';
import AdminReceptionist from '../pages/admin/AdminReceptionist';
import AdminCreateUser from '../pages/admin/AdminCreateUser';
import AdminDoctorShifts from '../pages/admin/AdminDoctorShifts';
//Trang doctor
import DoctorLayout from '../components/layout/doctorlayouts/DoctorLayout';
import DoctorDashboard from '../pages/doctor/DoctorDashboard';
import DoctorShifts from '../pages/doctor/DoctorShifts';
import CreateShift from '../pages/doctor/CreateShift';
import DoctorAppointment from '../pages/doctor/DoctorAppointment';
import AddPrescription from '../pages/doctor/AddPrescription';
//Trang receptionist
import RecepLayout from '../components/layout/receplayouts/RecepLayout';
import RecepShift from '../pages/receptionist/RecepShift';
import RecepDashboard from '../pages/receptionist/RecepDashboard';
import RecepAppointment from '../pages/receptionist/RecepAppointment';




export default function AppRoutes() {
  return (
    <Routes>
      {/* Guest/public */}
      <Route element={<PublicLayout />}>
        <Route path="/" element={<Home />} />
        <Route path="/doctors" element={<Doctors />} />
        <Route path="/services" element={<Services />} />
        <Route path="/about" element={<About />} />
        <Route path="/specialty/:id" element={<SpecialtyDetail />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/admin/register-admin" element={<RegisterAdmin />} />
        <Route path="/oauth2/callback" element={<OAuth2Callback />} />
      
        {/* Patient-only */}
        <Route element={<RequireAuth />}>
          <Route element={<RequireRole allow={['Patient']} />}>
            <Route path="/users/me" element={<Profile />} />
            <Route path="/appointment" element={<Appointment />} />
          </Route>
        </Route>
      </Route>

      {/* Patient-only */}
      <Route element={<RequireAuth />}>
        <Route element={<RequireRole allow={['Admin']} />}>
          <Route element={<AdminLayout />}>
            <Route path="/admin/dashboard" element={<AdminDashboard/>} />
            <Route path="/admin/doctors" element={<AdminDoctor />} />
            <Route path="/admin/doctors/by-specialty/:specialtyId" element={<AdminDoctor />} />
            <Route path="/admin/create-user" element={<AdminCreateUser />} />
            <Route path="/admin/doctors/:id" element={<AdminDoctor />} />
            <Route path="/admin/update-doctor/:id" element={<AdminDoctor />} />
            <Route path="/admin/receptionists" element={<AdminReceptionist />} />
            <Route path="/receptionists/:id" element={<AdminReceptionist />} />
            <Route path="/receptionists/update/:id" element={<AdminReceptionist />} />
            <Route path="/admin/specialties" element={<AdminSpecialty/>} />
            <Route path="/admin/specialties/create" element={<AdminSpecialty/>} />
            <Route path="/admin/specialties/update/:id" element={<AdminSpecialty />} />
            <Route path="/admin/shifts" element={<AdminDoctorShifts />} />
          </Route>
        </Route>
      </Route>

      {/* Doctor Area */}
      <Route element={<RequireAuth />}>
        <Route element={<RequireRole allow={['Doctor']} />}>
          <Route element={<DoctorLayout />}>
            <Route path="/doctor/dashboard" element={<DoctorDashboard />} />
            <Route path="/doctor/shifts" element={<DoctorShifts />} />
            <Route path="/doctor/shift/create" element={<CreateShift />} />
            {/* Placeholder routes */}
            <Route path="/doctor/appointments" element={<DoctorAppointment />} />
            <Route path="/doctor/appointment/:id/prescription" element={<AddPrescription />} />
            <Route path="/doctor/patients" element={<DoctorDashboard />} />
          </Route>
        </Route>
      </Route>

      {/* Receptionist Area */}
      <Route element={<RequireAuth />}>
        <Route element={<RequireRole allow={['Receptionist']} />}>
          <Route element={<RecepLayout />}>
            <Route path="/receptionist/dashboard" element={<RecepDashboard />} />
            <Route path="/receptionist/shift" element={<RecepShift />} />
            <Route path="/receptionist/appointment" element={<RecepAppointment />} />
          </Route>
        </Route>
      </Route>

      {/* Error*/}
      <Route path="/403" element={<Forbidden/>} />
      <Route path="*" element={<NotFound/>} />
    </Routes>
  );
}
