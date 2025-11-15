import React, { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import AdminRoute from '../../../../components/AdminRoute';
import { admin } from '../../../../services/api';
import { 
  Box, CircularProgress, Typography, Select, MenuItem, Button, FormControl, InputLabel, Alert
} from '@mui/material';

const EditUserPage = () => {
  const router = useRouter();
  const { id } = router.query;
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [selectedRole, setSelectedRole] = useState('');
  const [errors, setErrors] = useState({});
  const [submitError, setSubmitError] = useState('');

  useEffect(() => {
    if (id) {
      admin.getUserById(id)
        .then(response => {
          setUser(response.data);
          setSelectedRole(response.data.role.name);
          setLoading(false);
        })
        .catch(error => {
          console.error('Failed to fetch user:', error);
          setLoading(false);
        });
    }
  }, [id]);

  const validate = () => {
    let tempErrors = {};
    tempErrors.selectedRole = selectedRole ? '' : 'Role is required.';
    setErrors(tempErrors);
    return Object.values(tempErrors).every(x => x === '');
  };

  const handleRoleChange = (event) => {
    setSelectedRole(event.target.value);
    validate();
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitError('');

    if (validate()) {
      try {
        const updatedUserData = { ...user, role: { name: selectedRole } };
        await admin.updateUser(id, updatedUserData);
        router.push('/admin/users');
      } catch (error) {
        console.error('Failed to update user:', error);
        setSubmitError('Failed to update user role. Please try again.');
      }
    }
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', p: 4 }}>
        <CircularProgress />
      </Box>
    );
  }

  if (!user) {
    return <div>User not found.</div>;
  }

  return (
    <Box component="form" onSubmit={handleSubmit} sx={{ display: 'flex', flexDirection: 'column', gap: 2, maxWidth: 400 }}>
      <Typography variant="h4">Edit User: {user.username}</Typography>
      <FormControl fullWidth error={!!errors.selectedRole}>
        <InputLabel id="role-select-label">Role</InputLabel>
        <Select
          labelId="role-select-label"
          value={selectedRole}
          label="Role"
          onChange={handleRoleChange}
          onBlur={validate}
        >
          <MenuItem value="ADMIN">ADMIN</MenuItem>
          <MenuItem value="EDITOR">EDITOR</MenuItem>
        </Select>
        {errors.selectedRole && <Typography color="error" variant="caption">{errors.selectedRole}</Typography>}
      </FormControl>
      {submitError && <Alert severity="error" sx={{ width: '100%', mt: 2 }}>{submitError}</Alert>}
      <Box>
        <Button type="submit" variant="contained" color="primary">
          Update Role
        </Button>
        <Button variant="outlined" onClick={() => router.push('/admin/users')} sx={{ ml: 2 }}>
          Cancel
        </Button>
      </Box>
    </Box>
  );
};

const ProtectedEditUserPage = () => (
  <AdminRoute>
    <EditUserPage />
  </AdminRoute>
);

export default ProtectedEditUserPage;
