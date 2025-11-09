import React from 'react';
import { Container, Typography, Box } from '@mui/material';

const ContactPage = () => {
  return (
    <Container maxWidth="md">
      <Box my={4}>
        <Typography variant="h3" component="h1" gutterBottom>
          Contact Us
        </Typography>
        <Typography variant="body1">
          This page will contain contact information. Content to be added later.
        </Typography>
      </Box>
    </Container>
  );
};

export default ContactPage;
