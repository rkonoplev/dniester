import React, { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import ProtectedRoute from '../../../../components/ProtectedRoute';
import NewsForm from '../../../../components/admin/NewsForm';
import { admin } from '../../../../services/api'; // Corrected path
import { Box, CircularProgress } from '@mui/material';

const EditNewsPage = () => {
  const router = useRouter();
  const { id } = router.query;
  const [article, setArticle] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (id) {
      admin.getNewsById(id) // Assuming admin API has getNewsById
        .then(response => {
          setArticle(response.data);
          setLoading(false);
        })
        .catch(error => {
          console.error('Failed to fetch article:', error);
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

  if (!article) {
    return <div>Article not found.</div>;
  }

  return <NewsForm article={article} />;
};

const ProtectedEditNewsPage = () => (
  <ProtectedRoute>
    <EditNewsPage />
  </ProtectedRoute>
);

export default ProtectedEditNewsPage;
