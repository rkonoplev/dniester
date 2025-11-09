import React from 'react';
import { Container, Typography, Box } from '@mui/material';

const AdvertisingPage = () => {
  return (
    <Container maxWidth="md">
      <Box my={4}>
        <Typography variant="h3" component="h1" gutterBottom>
          Advertising
        </Typography>
        <Typography variant="body1">
          This page will contain information about advertising. Content to be added later.
        </Typography>
      </Box>
    </Container>
  );
};

export default AdvertisingPage;
