import React from 'react';
import { Box, Typography, Button } from '@mui/material';
import Link from 'next/link';

export default function Custom404() {
  return (
    <Box
      sx={{
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'center',
        alignItems: 'center',
        minHeight: '60vh',
        textAlign: 'center',
      }}
    >
      <Typography variant="h1" component="h1" gutterBottom>
        404
      </Typography>
      <Typography variant="h5" component="h2" gutterBottom>
        Page Not Found
      </Typography>
      <Typography variant="body1" color="text.secondary" sx={{ mb: 3 }}>
        The page you are looking for does not exist or has been moved.
      </Typography>
      <Link href="/" passHref>
        <Button variant="contained" color="primary">
          Go to Homepage
        </Button>
      </Link>
    </Box>
  );
}
