import React, { createContext, useState, useMemo, useContext, useEffect } from 'react';
import { createTheme } from '@mui/material/styles';

// Define the two themes
const lightTheme = createTheme({
  palette: {
    mode: 'light',
    primary: {
      main: '#1c355e', // Dark Blue
    },
    secondary: {
      main: '#cc0000', // Deep Red
    },
    background: {
      default: '#fdfcf8', // Off-White
      paper: '#ffffff',
    },
  },
  typography: {
    fontFamily: 'Roboto, sans-serif',
    h1: { fontFamily: 'Roboto Slab, serif' },
    h2: { fontFamily: 'Roboto Slab, serif' },
    h3: { fontFamily: 'Roboto Slab, serif' },
    h4: { fontFamily: 'Roboto Slab, serif' },
    h5: { fontFamily: 'Roboto Slab, serif' },
    h6: { fontFamily: 'Roboto Slab, serif' },
  },
});

const darkTheme = createTheme({
  palette: {
    mode: 'dark',
    primary: {
      main: '#8ab4f8', // A lighter blue for dark mode
    },
    secondary: {
      main: '#f28b82', // A lighter red for dark mode
    },
    background: {
      default: '#121212',
      paper: '#1e1e1e',
    },
  },
  typography: {
    fontFamily: 'Roboto, sans-serif',
    h1: { fontFamily: 'Roboto Slab, serif' },
    h2: { fontFamily: 'Roboto Slab, serif' },
    h3: { fontFamily: 'Roboto Slab, serif' },
    h4: { fontFamily: 'Roboto Slab, serif' },
    h5: { fontFamily: 'Roboto Slab, serif' },
    h6: { fontFamily: 'Roboto Slab, serif' },
  },
});


export const ThemeContext = createContext({
  toggleTheme: () => {},
});

export const useThemeContext = () => useContext(ThemeContext);

export const CustomThemeProvider = ({ children }) => {
  const [mode, setMode] = useState('light');

  useEffect(() => {
    const savedMode = localStorage.getItem('themeMode');
    if (savedMode) {
      setMode(savedMode);
    }
  }, []);

  const themeHelpers = useMemo(
    () => ({
      toggleTheme: () => {
        const newMode = mode === 'light' ? 'dark' : 'light';
        setMode(newMode);
        localStorage.setItem('themeMode', newMode);
      },
    }),
    [mode]
  );

  const theme = useMemo(() => (mode === 'light' ? lightTheme : darkTheme), [mode]);

  return (
    <ThemeContext.Provider value={themeHelpers}>
      {/* We need to pass the actual theme object to MUI's provider */}
      {React.cloneElement(children, { theme: theme })}
    </ThemeContext.Provider>
  );
};
