import React, { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import ProtectedRoute from '../../../../components/ProtectedRoute';
import TermForm from '../../../../components/admin/TermForm';
import { admin } from '../../../../services/api';
import { Box, CircularProgress } from '@mui/material';

const EditTermPage = () => {
  const router = useRouter();
  const { id } = router.query;
  const [term, setTerm] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (id) {
      admin.getTermById(id)
        .then(response => {
          setTerm(response.data);
          setLoading(false);
        })
        .catch(error => {
          console.error('Failed to fetch term:', error);
          setLoading(false);
        });
    }
  }, [id]);

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', p: 4 }}>
        <CircularProgress />
      </Box>
    );
  }

  if (!term) {
    return <div>Term not found.</div>;
  }

  return <TermForm term={term} />;
};

const ProtectedEditTermPage = () => (
  <ProtectedRoute>
    <EditTermPage />
  </ProtectedRoute>
);

export default ProtectedEditTermPage;
