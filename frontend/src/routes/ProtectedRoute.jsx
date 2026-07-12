import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

// Wrap any page that requires login. Pass allowedRoles to also
// restrict by role (e.g. Organization Setup is Admin-only).
export default function ProtectedRoute({ children, allowedRoles }) {
  const { isAuthenticated, role } = useAuth();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (allowedRoles && !allowedRoles.includes(role)) {
    return <Navigate to="/dashboard" replace />;
  }

  return children;
}
