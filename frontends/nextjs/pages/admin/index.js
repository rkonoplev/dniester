import React from 'react';
import ProtectedRoute from '../../components/ProtectedRoute';
import { Typography } from '@mui/material';

const AdminDashboard = () => {
  return (
    <Typography variant="h4" component="h1">
      Admin Dashboard
    </Typography>
  );
};

const ProtectedAdminDashboard = () => {
  return (
    <ProtectedRoute>
      <AdminDashboard />
    </ProtectedRoute>
  );
};

export default ProtectedAdminDashboard;
