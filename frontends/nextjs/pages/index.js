import React from 'react';
import { getNews } from '../services/api';
import { Container, Typography, Grid, Card, CardContent, CardActionArea } from '@mui/material';
import Link from 'next/link';

const HomePage = ({ news }) => {
  return (
    <Container>
      <Typography variant="h2" component="h1" gutterBottom>
        Latest News
      </Typography>
      <Grid container spacing={3}>
        {news.map((article) => (
          <Grid item xs={12} sm={6} md={4} key={article.id}>
            <Link href={`/node/${article.id}`} passHref legacyBehavior>
              <CardActionArea component="a" sx={{ height: '100%' }}>
                <Card>
                  <CardContent>
                    <Typography gutterBottom variant="h5" component="div">
                      {article.title}
                    </Typography>
                    <Typography 
                      variant="body2" 
                      color="text.secondary" 
                      dangerouslySetInnerHTML={{ __html: article.teaser }} // Render teaser as HTML
                    />
                  </CardContent>
                </Card>
              </CardActionArea>
            </Link>
          </Grid>
        ))}
      </Grid>
    </Container>
  );
};

export async function getServerSideProps() {
  try {
    const response = await getNews(0, 10);
    const news = response.data.content;
    return {
      props: { news },
    };
  } catch (error) {
    console.error('Failed to fetch news:', error);
    return {
      props: { news: [] },
    };
  }
}

export default HomePage;
