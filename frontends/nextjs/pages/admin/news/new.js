import React from 'react';
import ProtectedRoute from '../../../components/ProtectedRoute';
import NewsForm from '../../../components/admin/NewsForm';

const CreateNewsPage = () => {
  return <NewsForm />;
};

const ProtectedCreateNewsPage = () => (
  <ProtectedRoute>
    <CreateNewsPage />
  </ProtectedRoute>
);

export default ProtectedCreateNewsPage;
