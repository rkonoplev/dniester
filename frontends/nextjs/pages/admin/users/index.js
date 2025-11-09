import React, { useEffect, useState } from 'react';
import AdminRoute from '../../../components/AdminRoute'; // Changed from ProtectedRoute
import { admin } from '../../../services/api';
import { 
  Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, 
  Button, IconButton, Typography, Box 
} from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';
import Link from 'next/link';

const UserListPage = () => {
  const [users, setUsers] = useState([]);

  const fetchUsers = async () => {
    try {
      const response = await admin.getUsers();
      setUsers(response.data);
    } catch (error) {
      console.error('Failed to fetch users:', error);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  return (
    <Box>
      <Typography variant="h4" gutterBottom>User Management</Typography>
      <Paper>
        <TableContainer>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Username</TableCell>
                <TableCell>Role</TableCell>
                <TableCell align="right">Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {users.map((user) => (
                <TableRow key={user.id}>
                  <TableCell>{user.username}</TableCell>
                  <TableCell>{user.role.name}</TableCell>
                  <TableCell align="right">
                    <Link href={`/admin/users/edit/${user.id}`} passHref>
                      <IconButton color="primary"><EditIcon /></IconButton>
                    </Link>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      </Paper>
    </Box>
  );
};

const ProtectedUserListPage = () => (
  <AdminRoute>
    <UserListPage />
  </AdminRoute>
);

export default ProtectedUserListPage;
