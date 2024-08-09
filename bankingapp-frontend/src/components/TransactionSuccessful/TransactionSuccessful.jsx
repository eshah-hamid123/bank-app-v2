import React from "react";
import { Container, Paper, Typography, Box, Divider } from "@mui/material";
import { useLocation } from "react-router-dom";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import "./TransactionSuccessful.css";
import Layout from "../Layout/Layout";

const TransactionSuccessful = () => {
  const { state } = useLocation();
  const transaction = state?.transaction;

  if (!transaction) {
    return <Typography color="error">No transaction data available</Typography>;
  }

  return (
    <Layout>
      <Container
        component="main"
        maxWidth="md"
        className="success-transaction-container"
      >
        <Paper elevation={6} className="success-transaction-paper">
          <Box display="flex" flexDirection="column" alignItems="center" p={3}>
            <CheckCircleIcon
              color="success"
              fontSize="large"
              className="success-icon"
            />
            <Typography
              variant="h4"
              gutterBottom
              className="success-transaction-title"
            >
              Transaction Successful
            </Typography>
            <Divider />
            <Box mt={2} className="transaction-details">
              <Typography variant="h6" gutterBottom>
                <strong>Transaction Details</strong>
              </Typography>
              <Divider />
              <Box mt={2}>
                <Typography variant="body1" className="transaction-info">
                  <strong>Description:</strong> {transaction.description}
                </Typography>
                <Typography variant="body1" className="transaction-info">
                  <strong>Amount:</strong> ${transaction.amount}
                </Typography>
                <Typography variant="body1" className="transaction-info">
                  <strong>Date:</strong>{" "}
                  {new Date(transaction.date).toLocaleString()}
                </Typography>
                <Typography variant="body1" className="transaction-info">
                  <strong>To:</strong> {transaction.receiverUsername}
                </Typography>
                <Typography variant="body1" className="transaction-info">
                  <strong>From:</strong> {transaction.senderUsername}
                </Typography>
              </Box>
            </Box>
          </Box>
        </Paper>
      </Container>
    </Layout>
  );
};

export default TransactionSuccessful;
