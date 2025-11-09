import React, { useState, useEffect } from 'react';
import { Box, TextField, Button, Switch, FormControlLabel, Typography, Alert } from '@mui/material';
import { useRouter } from 'next/router';
import { admin } from '../../services/api';

const NewsForm = ({ article: initialArticle }) => {
  const [article, setArticle] = useState({
    title: '',
    body: '',
    teaser: '',
    published: false,
  });
  const [errors, setErrors] = useState({});
  const [submitError, setSubmitError] = useState('');
  const router = useRouter();
  const isEditMode = !!initialArticle;

  useEffect(() => {
    if (initialArticle) {
      // Map initialArticle fields to frontend state
      setArticle({
        ...initialArticle,
        body: initialArticle.body || '',
        teaser: initialArticle.teaser || '',
      });
    }
  }, [initialArticle]);

  const validate = () => {
    let tempErrors = {};
    tempErrors.title = article.title.length >= 5 && article.title.length <= 50 ? '' : 'Title must be between 5 and 50 characters.';
    if (!article.title) tempErrors.title = 'Title is required.';

    tempErrors.teaser = article.teaser.length >= 10 && article.teaser.length <= 250 ? '' : 'Teaser must be between 10 and 250 characters.';
    if (!article.teaser) tempErrors.teaser = 'Teaser is required.';

    tempErrors.body = article.body.length >= 20 ? '' : 'Body must be at least 20 characters long.';
    if (!article.body) tempErrors.body = 'Body is required.';

    setErrors(tempErrors);
    return Object.values(tempErrors).every(x => x === '');
  };

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setArticle(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitError('');

    if (validate()) {
      try {
        if (isEditMode) {
          await admin.updateNews(article.id, article);
        } else {
          await admin.createNews(article);
        }
        router.push('/admin/news');
      } catch (error) {
        console.error('Failed to save article:', error);
        setSubmitError('Failed to save article. Please check your input and try again.');
      }
    }
  };

  return (
    <Box component="form" onSubmit={handleSubmit} sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
      <Typography variant="h4">{isEditMode ? 'Edit News' : 'Create News'}</Typography>
      <TextField
        name="title"
        label="Title"
        value={article.title}
        onChange={handleChange}
        onBlur={validate}
        required
        fullWidth
        error={!!errors.title}
        helperText={errors.title}
      />
      <TextField
        name="teaser"
        label="Teaser"
        value={article.teaser}
        onChange={handleChange}
        onBlur={validate}
        multiline
        rows={3}
        fullWidth
        required
        error={!!errors.teaser}
        helperText={errors.teaser || "Supports HTML content, including image links (e.g., <img src='...' />) and YouTube embeds."} // Added helperText
      />
      <TextField
        name="body"
        label="Body"
        value={article.body}
        onChange={handleChange}
        onBlur={validate}
        multiline
        rows={10}
        fullWidth
        required
        error={!!errors.body}
        helperText={errors.body || "Supports HTML content, including image links (e.g., <img src='...' />) and YouTube embeds."}
      />
      <FormControlLabel
        control={<Switch name="published" checked={article.published} onChange={handleChange} />}
        label="Published"
      />
      {submitError && <Alert severity="error" sx={{ width: '100%', mt: 2 }}>{submitError}</Alert>}
      <Box>
        <Button type="submit" variant="contained" color="primary">
          {isEditMode ? 'Update' : 'Create'}
        </Button>
        <Button variant="outlined" onClick={() => router.push('/admin/news')} sx={{ ml: 2 }}>
          Cancel
        </Button>
      </Box>
    </Box>
  );
};

export default NewsForm;
