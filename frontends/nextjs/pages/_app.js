import React from 'react';
import { ThemeProvider as MuiThemeProvider } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import Layout from '../components/Layout';
import { AuthProvider } from '../context/AuthContext';
import { CustomThemeProvider } from '../context/ThemeContext';

// A component to apply the theme from our context to MUI
const ThemeApplicator = ({ children, theme }) => {
  return (
    <MuiThemeProvider theme={theme}>
      <CssBaseline />
      {children}
    </MuiThemeProvider>
  );
};

function MyApp({ Component, pageProps }) {
  return (
    <AuthProvider>
      <CustomThemeProvider>
        {/* The 'theme' prop will be injected by CustomThemeProvider */}
        <ThemeApplicator>
          <Layout>
            <Component {...pageProps} />
          </Layout>
        </ThemeApplicator>
      </CustomThemeProvider>
    </AuthProvider>
  );
}

export default MyApp;
