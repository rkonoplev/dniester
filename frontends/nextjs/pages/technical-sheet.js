import React from 'react';
import { Container, Typography, Box } from '@mui/material';

const TechnicalSheetPage = () => {
  return (
    <Container maxWidth="md">
      <Box my={4}>
        <Typography variant="h3" component="h1" gutterBottom>
          Technical Sheet
        </Typography>
        <Typography variant="body1">
          This page will contain the technical sheet information. Content to be added later.
        </Typography>
      </Box>
    </Container>
  );
};

export default TechnicalSheetPage;
