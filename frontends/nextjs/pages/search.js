import React, { useState, useEffect } from 'react';
import { useRouter } from 'next/router';
import { Container, Typography, Box, TextField, Button, Grid, Card, CardContent, CircularProgress } from '@mui/material';
import Link from 'next/link';
import { searchPublicNews } from '../services/api';

const SearchPage = () => {
  const router = useRouter();
  const { q: initialQuery } = router.query;
  const [query, setQuery] = useState(initialQuery || '');
  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(false);

  const performSearch = async (searchQuery) => {
    if (!searchQuery.trim()) {
      setResults([]);
      return;
    }
    setLoading(true);
    try {
      const response = await searchPublicNews(searchQuery);
      setResults(response.data);
    } catch (error) {
      console.error('Search failed:', error);
      setResults([]);
    }
    setLoading(false);
  };

  useEffect(() => {
    if (initialQuery) {
      performSearch(initialQuery);
    }
  }, [initialQuery]);

  const handleSearch = (e) => {
    e.preventDefault();
    router.push(`/search?q=${encodeURIComponent(query)}`, undefined, { shallow: true });
  };

  return (
    <Container maxWidth="md">
      <Box my={4}>
        <Typography variant="h3" component="h1" gutterBottom>
          Search
        </Typography>
        <Box component="form" onSubmit={handleSearch} sx={{ display: 'flex', gap: 1, mb: 4 }}>
          <TextField
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            label="Search for articles..."
            variant="outlined"
            fullWidth
          />
          <Button type="submit" variant="contained" disabled={loading}>
            Search
          </Button>
        </Box>

        {loading ? (
          <Box sx={{ display: 'flex', justifyContent: 'center' }}>
            <CircularProgress />
          </Box>
        ) : (
          <Grid container spacing={3}>
            {results.length > 0 ? (
              results.map((article) => (
                <Grid item xs={12} key={article.id}>
                  <Link href={`/node/${article.id}`} passHref>
                    <Card component="a" sx={{ textDecoration: 'none' }}>
                      <CardContent>
                        <Typography variant="h5">{article.title}</Typography>
                        <Typography 
                          color="text.secondary" 
                          dangerouslySetInnerHTML={{ __html: article.teaser }} // Render teaser as HTML
                        />
                      </CardContent>
                    </Card>
                  </Link>
                </Grid>
              ))
            ) : (
              initialQuery && <Typography sx={{ ml: 2 }}>No results found for "{initialQuery}".</Typography>
            )}
          </Grid>
        )}
      </Box>
    </Container>
  );
};

export default SearchPage;
