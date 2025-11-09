import React from 'react';
import ProtectedRoute from '../../../components/ProtectedRoute';
import TermForm from '../../../components/admin/TermForm';

const CreateTermPage = () => {
  return <TermForm />;
};

const ProtectedCreateTermPage = () => (
  <ProtectedRoute>
    <CreateTermPage />
  </ProtectedRoute>
);

export default ProtectedCreateTermPage;
