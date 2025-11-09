import { createTheme } from '@mui/material/styles';

// Create a theme instance.
const theme = createTheme({
  palette: {
    primary: {
      main: '#1c355e', // Dark Blue
    },
    secondary: {
      main: '#cc0000', // Deep Red
    },
    background: {
      default: '#fdfcf8', // Off-White
    },
  },
  typography: {
    fontFamily: 'Roboto, sans-serif',
    h1: {
      fontFamily: 'Roboto Slab, serif',
    },
    h2: {
      fontFamily: 'Roboto Slab, serif',
    },
    h3: {
      fontFamily: 'Roboto Slab, serif',
    },
    h4: {
      fontFamily: 'Roboto Slab, serif',
    },
    h5: {
      fontFamily: 'Roboto Slab, serif',
    },
    h6: {
      fontFamily: 'Roboto Slab, serif',
    },
  },
});

export default theme;
