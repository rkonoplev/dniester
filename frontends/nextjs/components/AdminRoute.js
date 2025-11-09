import React, { useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { useRouter } from 'next/router';
import { Box, CircularProgress, Typography } from '@mui/material';

const AdminRoute = ({ children }) => {
  const { user, isAuthenticated, loading } = useAuth();
  const router = useRouter();

  useEffect(() => {
    if (!loading) {
      if (!isAuthenticated) {
        router.push('/admin/login');
      } else if (user?.role?.name !== 'ADMIN') {
        // If authenticated but not an admin, redirect to a safe page (e.g., admin dashboard)
        // or show an "Access Denied" message.
        router.push('/admin'); 
      }
    }
  }, [isAuthenticated, user, loading, router]);

  if (loading || !isAuthenticated || user?.role?.name !== 'ADMIN') {
    // Show a loading spinner or a placeholder while checking auth status
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '80vh' }}>
        <CircularProgress />
      </Box>
    );
  }

  // If everything is fine, render the children components
  return children;
};

export default AdminRoute;
