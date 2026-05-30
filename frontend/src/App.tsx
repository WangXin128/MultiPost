import { Navigate, Route, BrowserRouter as Router, Routes } from 'react-router-dom';
import { getToken } from './api/client';
import AppLayout from './components/AppLayout';
import LoginPage from './pages/LoginPage';
import DashboardPage from './pages/DashboardPage';
import ContentPage from './pages/ContentPage';
import AdaptPage from './pages/AdaptPage';
import PublishPage from './pages/PublishPage';

function ProtectedRoute() {
  if (!getToken()) {
    return <Navigate to="/login" replace />;
  }
  return <AppLayout />;
}

export default function App() {
  return (
    <Router>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route element={<ProtectedRoute />}>
          <Route path="/" element={<DashboardPage />} />
          <Route path="/contents" element={<ContentPage />} />
          <Route path="/adapt" element={<AdaptPage />} />
          <Route path="/publish" element={<PublishPage />} />
        </Route>
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </Router>
  );
}
