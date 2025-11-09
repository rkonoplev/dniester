import React from 'react';
import { AppBar, Toolbar, Button, Box } from '@mui/material';
import Link from 'next/link';
import { useAuth } from '../context/AuthContext';
import { useRouter } from 'next/router';

const AdminBar = () => {
  const { user, logout } = useAuth();
  const router = useRouter();

  const handleLogout = () => {
    logout();
    router.push('/'); // Redirect to homepage after logout
  };

  const isAdmin = user?.role?.name === 'ADMIN';

  return (
    <AppBar position="static" sx={{ backgroundColor: '#000' }}>
      <Toolbar variant="dense">
        <Box sx={{ flexGrow: 1, display: 'flex', gap: 2 }}>
          {/* Links for both ADMIN and EDITOR */}
          <Link href="/" passHref>
            <Button sx={{ color: 'white' }}>Home</Button>
          </Link>
          <Link href="/admin/news/new" passHref>
            <Button sx={{ color: 'white' }}>Create News</Button>
          </Link>
          <Link href="/admin/news" passHref>
            <Button sx={{ color: 'white' }}>News List</Button>
          </Link>

          {/* Links for ADMIN only */}
          {isAdmin && (
            <>
              <Link href="/admin/taxonomy" passHref>
                <Button sx={{ color: 'white' }}>Taxonomy</Button>
              </Link>
              <Link href="/admin/users" passHref>
                <Button sx={{ color: 'white' }}>Users</Button>
              </Link>
            </>
          )}
        </Box>
        <Button color="inherit" onClick={handleLogout}>
          Logout
        </Button>
      </Toolbar>
    </AppBar>
  );
};

export default AdminBar;
