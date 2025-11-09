import React from 'react';
import { getNews, getNewsById } from '../../services/api';
import { Container, Typography, Box } from '@mui/material';
import Head from 'next/head';

const ArticlePage = ({ article }) => {
  if (!article) {
    return <div>Article not found.</div>;
  }

  return (
    <>
      <Head>
        <title>{article.title}</title>
        <meta name="description" content={article.teaser} />
        {/* Add more meta tags for OpenGraph, Twitter, etc. later */}
      </Head>
      <Container maxWidth="md">
        <Box my={4}>
          <Typography variant="h2" component="h1" gutterBottom>
            {article.title}
          </Typography>
          <Typography variant="subtitle1" color="text.secondary" gutterBottom>
            Published on: {new Date(article.publicationDate).toLocaleDateString()}
          </Typography>
          {/* Removed image display as it will be part of body HTML */}
          <Typography variant="body1" dangerouslySetInnerHTML={{ __html: article.body }} />
        </Box>
      </Container>
    </>
  );
};

export async function getStaticPaths() {
  try {
    // Fetch the first few pages of news to pre-render popular articles
    const response = await getNews(0, 20); // Pre-render first 20 articles
    const news = response.data.content;

    const paths = news.map((article) => ({
      params: { id: String(article.id) },
    }));

    return { paths, fallback: 'blocking' }; // 'blocking' will SSR new pages
  } catch (error) {
    console.error('Failed to get static paths:', error);
    return { paths: [], fallback: 'blocking' };
  }
}

export async function getStaticProps({ params }) {
  try {
    const response = await getNewsById(params.id);
    const article = response.data;
    return {
      props: { article },
      revalidate: 60, // Re-generate the page every 60 seconds (ISR)
    };
  } catch (error) {
    console.error(`Failed to fetch article ${params.id}:`, error);
    return {
      notFound: true,
    };
  }
}

export default ArticlePage;
