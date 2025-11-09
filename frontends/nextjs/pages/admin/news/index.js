import React, { useEffect, useState } from 'react';
import ProtectedRoute from '../../../components/ProtectedRoute';
import { admin } from '../../../services/api';
import { 
  Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, 
  Button, IconButton, Typography, Box, TablePagination 
} from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import Link from 'next/link';

const NewsListPage = () => {
  const [news, setNews] = useState([]);
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(20);
  const [totalElements, setTotalElements] = useState(0);

  const fetchNews = async () => {
    try {
      const response = await admin.getNews(page, rowsPerPage);
      setNews(response.data.content);
      setTotalElements(response.data.totalElements);
    } catch (error) {
      console.error('Failed to fetch news:', error);
    }
  };

  useEffect(() => {
    fetchNews();
  }, [page, rowsPerPage]);

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this article?')) {
      try {
        await admin.deleteNews(id);
        fetchNews(); // Refresh the list
      } catch (error) {
        console.error('Failed to delete news:', error);
      }
    }
  };

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };

  return (
    <Box>
      <Typography variant="h4" gutterBottom>News Management</Typography>
      <Link href="/admin/news/new" passHref>
        <Button variant="contained" color="primary" sx={{ mb: 2 }}>
          Create News
        </Button>
      </Link>
      <Paper>
        <TableContainer>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Title</TableCell>
                <TableCell>Published</TableCell>
                <TableCell>Last Modified</TableCell>
                <TableCell align="right">Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {news.map((article) => (
                <TableRow key={article.id}>
                  <TableCell>{article.title}</TableCell>
                  <TableCell>{article.published ? 'Yes' : 'No'}</TableCell>
                  <TableCell>{new Date(article.lastModifiedDate).toLocaleString()}</TableCell>
                  <TableCell align="right">
                    <Link href={`/admin/news/edit/${article.id}`} passHref>
                      <IconButton color="primary"><EditIcon /></IconButton>
                    </Link>
                    <IconButton color="error" onClick={() => handleDelete(article.id)}>
                      <DeleteIcon />
                    </IconButton>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
        <TablePagination
          component="div"
          count={totalElements}
          page={page}
          onPageChange={handleChangePage}
          rowsPerPage={rowsPerPage}
          onRowsPerPageChange={handleChangeRowsPerPage}
          rowsPerPageOptions={[20, 50, 100]}
        />
      </Paper>
    </Box>
  );
};

const ProtectedNewsListPage = () => (
  <ProtectedRoute>
    <NewsListPage />
  </ProtectedRoute>
);

export default ProtectedNewsListPage;
