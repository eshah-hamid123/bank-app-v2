import React, { useEffect, useState } from "react";
import axios from "axios";
import {
  Container,
  Paper,
  Typography,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableFooter,
  TablePagination,
  TableHead,
  TableRow,
  Alert,
} from "@mui/material";
import Layout from "../Layout/Layout";
import "./ViewTransactions.css";

const ViewTransactions = () => {
  const [transactions, setTransactions] = useState([]);
  const [errorMessage, setErrorMessage] = useState("");
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);

  useEffect(() => {
    const fetchTransactions = async () => {
      try {
        const token = localStorage.getItem("token");
        const response = await axios.get(
          "http://localhost:8080/v1/transactions/all-transactions",
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );

        // Reverse the transactions array
        const reversedTransactions = response.data.reverse();
        setTransactions(reversedTransactions);
      } catch (error) {
        setErrorMessage(
          "Error fetching transaction history. Please try again."
        );
      }
    };

    fetchTransactions();
  }, []);

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };

  return (
    <Layout>
      <Container
        component="main"
        maxWidth="lg"
        className="view-transactions-container"
      >
        <Paper elevation={5} className="view-transactions-paper">
          <Typography
            variant="h4"
            gutterBottom
            className="view-transactions-title"
          >
            All Transactions
          </Typography>
          {errorMessage && <Alert severity="error">{errorMessage}</Alert>}
          {transactions.length === 0 ? (
            <Typography variant="h6">No transactions available</Typography>
          ) : (
            <TableContainer
              component={Paper}
              className="view-transactions-table-container"
            >
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell sx={{ color: "white" }}>Sender</TableCell>
                    <TableCell sx={{ color: "white" }}>Receiver</TableCell>
                    <TableCell sx={{ color: "white" }}>Amount</TableCell>
                    <TableCell sx={{ color: "white" }}>Description</TableCell>
                    <TableCell sx={{ color: "white" }}>Date</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {transactions
                    .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                    .map((transaction) => (
                      <TableRow key={transaction.id}>
                        <TableCell>{transaction.senderUsername}</TableCell>
                        <TableCell>{transaction.receiverUsername}</TableCell>
                        <TableCell>${transaction.amount.toFixed(2)}</TableCell>
                        <TableCell>{transaction.description}</TableCell>
                        <TableCell>
                          {new Date(transaction.date).toLocaleString()}
                        </TableCell>
                      </TableRow>
                    ))}
                </TableBody>
                <TableFooter>
                  <TableRow>
                    <TablePagination
                      rowsPerPageOptions={[10, 25, 50]}
                      count={transactions.length}
                      rowsPerPage={rowsPerPage}
                      page={page}
                      onPageChange={handleChangePage}
                      onRowsPerPageChange={handleChangeRowsPerPage}
                    />
                  </TableRow>
                </TableFooter>
              </Table>
            </TableContainer>
          )}
        </Paper>
      </Container>
    </Layout>
  );
};

export default ViewTransactions;
