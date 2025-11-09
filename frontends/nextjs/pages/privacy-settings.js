import React from 'react';
import { Container, Typography, Box } from '@mui/material';

const PrivacySettingsPage = () => {
  return (
    <Container maxWidth="md">
      <Box my={4}>
        <Typography variant="h3" component="h1" gutterBottom>
          Privacy Settings
        </Typography>
        <Typography variant="body1">
          This page will allow users to manage their privacy settings. Content to be added later.
        </Typography>
      </Box>
    </Container>
  );
};

export default PrivacySettingsPage;
