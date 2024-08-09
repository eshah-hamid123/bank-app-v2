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
  TableHead,
  TableRow,
  TableFooter,
  TablePagination,
  IconButton,
} from "@mui/material";
import { ArrowDownward, ArrowUpward } from "@mui/icons-material";
import "./TransactionHistory.css";
import Layout from "../Layout/Layout";

const TransactionHistory = () => {
  const [transactions, setTransactions] = useState([]);
  const [errorMessage, setErrorMessage] = useState("");
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);

  useEffect(() => {
    const fetchTransactions = async () => {
      try {
        const token = localStorage.getItem("token");

        const debitResponse = await axios.get(
          "http://localhost:8080/v1/transactions/get-debit-transactions",
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );

        const creditResponse = await axios.get(
          "http://localhost:8080/v1/transactions/get-credit-transactions",
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );


        const allTransactions = [
          ...debitResponse.data.map((txn) => ({ ...txn, type: "debit" })),
          ...creditResponse.data.map((txn) => ({ ...txn, type: "credit" })),
        ].sort((a, b) => new Date(b.date) - new Date(a.date)); // Sort by date descending

        setTransactions(allTransactions);
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
        className="transaction-history-container"
      >
        <Paper elevation={6} className="transaction-history-paper">
          <Typography
            variant="h4"
            gutterBottom
            className="transaction-history-title"
          >
            Transaction History
          </Typography>
          {errorMessage && (
            <Typography color="error">{errorMessage}</Typography>
          )}

          {transactions.length === 0 ? (
            <Typography>No transactions done</Typography>
          ) : (
            <TableContainer component={Paper}>
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell sx={{ color: "white" }}>Type</TableCell>
                    <TableCell sx={{ color: "white" }}>Date</TableCell>
                    <TableCell sx={{ color: "white" }}>Description</TableCell>
                    <TableCell sx={{ color: "white" }}>Amount</TableCell>
                    <TableCell sx={{ color: "white" }}>User</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {transactions
                    .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                    .map((transaction) => (
                      <TableRow key={transaction.id}>
                        <TableCell>
                          {transaction.type === "debit" ? (
                            <IconButton>
                              <ArrowDownward color="error" />
                            </IconButton>
                          ) : (
                            <IconButton>
                              <ArrowUpward color="success" />
                            </IconButton>
                          )}
                        </TableCell>
                        <TableCell>
                          {new Date(transaction.date).toLocaleString()}
                        </TableCell>
                        <TableCell>{transaction.description}</TableCell>
                        <TableCell>${transaction.amount}</TableCell>
                        <TableCell>
                          {transaction.type === "debit"
                            ? transaction.receiverUsername
                            : transaction.senderUsername}
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

export default TransactionHistory;
