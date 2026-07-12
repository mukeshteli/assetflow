import { Routes, Route, Navigate } from 'react-router-dom';
import ProtectedRoute from './ProtectedRoute';
import AppLayout from '../components/layout/AppLayout';
import LoginPage from '../pages/LoginPage';
import SignupPage from '../pages/SignupPage';
import DashboardPage from '../pages/DashboardPage';
import AssetDirectoryPage from '../pages/assets/AssetDirectoryPage';
import RegisterAssetPage from '../pages/assets/RegisterAssetPage';
import OrganizationSetupPage from '../pages/OrganizationSetupPage';
import AllocationTransferPage from '../pages/AllocationTransferPage';
import ResourceBookingPage from '../pages/ResourceBookingPage';
import MaintenancePage from '../pages/MaintenancePage';
import AuditPage from '../pages/AuditPage';
import ReportsPage from '../pages/ReportsPage';
import NotificationsPage from '../pages/NotificationsPage';

export default function AppRoutes() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/signup" element={<SignupPage />} />

      <Route
        element={
          <ProtectedRoute>
            <AppLayout />
          </ProtectedRoute>
        }
      >
        <Route path="/dashboard" element={<DashboardPage />} />
        <Route path="/organization-setup" element={<OrganizationSetupPage />} />
        <Route path="/assets" element={<AssetDirectoryPage />} />
        <Route path="/assets/new" element={<RegisterAssetPage />} />
        <Route path="/assets/edit/:id" element={<RegisterAssetPage />} />
        <Route path="/allocations" element={<AllocationTransferPage />} />
        <Route path="/bookings" element={<ResourceBookingPage />} />
        <Route path="/maintenance" element={<MaintenancePage />} />
        <Route path="/audits" element={<AuditPage />} />
        <Route path="/reports" element={<ReportsPage />} />
        <Route path="/notifications" element={<NotificationsPage />} />
      </Route>

      <Route path="*" element={<Navigate to="/dashboard" replace />} />
    </Routes>
  );
}
