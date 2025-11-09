import React from 'react';
import { getNewsByTerm } from '../../services/api';
import { Container, Typography, Grid, Card, CardContent } from '@mui/material';

const CategoryPage = ({ news, termId }) => {
  return (
    <Container>
      <Typography variant="h2" component="h1" gutterBottom>
        Category: {termId}
      </Typography>
      <Grid container spacing={3}>
        {news.map((article) => (
          <Grid item xs={12} sm={6} md={4} key={article.id}>
            <Card>
              <CardContent>
                <Typography gutterBottom variant="h5" component="div">
                  {article.title}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  {article.summary}
                </Typography>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>
    </Container>
  );
};

export async function getServerSideProps({ params }) {
  try {
    const { id } = params;
    const response = await getNewsByTerm(id, 0, 10);
    const news = response.data.content;
    return {
      props: { news, termId: id },
    };
  } catch (error) {
    console.error(`Failed to fetch news for category ${params.id}:`, error);
    return {
      props: { news: [], termId: params.id },
    };
  }
}

export default CategoryPage;
