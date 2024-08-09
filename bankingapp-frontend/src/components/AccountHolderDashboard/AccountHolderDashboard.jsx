import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import Layout from "../Layout/Layout";
import { Container, Paper, Typography, Button, Box, Grid } from "@mui/material";
import "./AccountHolderDashboard.css"; 

const AccountHolderDashboard = () => {
  const navigate = useNavigate();
  const [accountInfo, setAccountInfo] = useState(null);
  const [errorMessage, setErrorMessage] = useState("");

  const handleViewAccountDetails = async () => {
    try {
      const accountId = localStorage.getItem("accountId");
      if (!accountId) {
        setErrorMessage("No account ID found.");
        return;
      }

      const token = localStorage.getItem("token");
      const response = await axios.get(
        `http://localhost:8080/v1/accounts/get-account/${accountId}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      setAccountInfo(response.data);
      setErrorMessage("");
    } catch (error) {
      setErrorMessage("Error fetching account details. Please try again.");
    }
  };

  const handleSendMoney = () => {
    navigate("/send-money");
  };

  const handleViewTransactionHistory = () => {
    navigate("/transaction-history");
  };

  return (
    <Layout>
      <Container
        component="main"
        maxWidth="md"
        className="dashboard-container"
      >
        <Paper elevation={6} className="dashboard-paper">
          <Typography
            variant="h4"
            gutterBottom
            className="dashboard-title"
          >
            Explore Your Financial World
          </Typography>

          <Box display="flex" justifyContent="space-between" mb={3}>
            <Button
              variant="contained"
              color="success"
              onClick={handleViewAccountDetails}
              className="dashboard-button"
            >
              View Account Details
            </Button>
            <Button
              variant="contained"
              className="transaction-history-button dashboard-button"
              onClick={handleViewTransactionHistory}
            >
              View Transaction History
            </Button>
            <Button
              variant="contained"
              color="success"
              onClick={handleSendMoney}
              className="dashboard-button"
            >
              Send Money
            </Button>
          </Box>

          {errorMessage && (
            <Typography color="error">{errorMessage}</Typography>
          )}

          {accountInfo && (
            <Paper elevation={3} className="account-info-paper">
              <Typography
                variant="h6"
                gutterBottom
                className="account-details-title"
              >
                Account Details
              </Typography>
              <Grid container spacing={2} className="account-info-grid">
                <Grid item xs={12} sm={6}>
                  <Typography>
                    <strong>Username:</strong> {accountInfo.username}
                  </Typography>
                </Grid>
                <Grid item xs={12} sm={6}>
                  <Typography>
                    <strong>Email:</strong> {accountInfo.email}
                  </Typography>
                </Grid>
                <Grid item xs={12} sm={6}>
                  <Typography>
                    <strong>Address:</strong> {accountInfo.address}
                  </Typography>
                </Grid>
                <Grid item xs={12} sm={6}>
                  <Typography>
                    <strong>Balance:</strong> ${accountInfo.balance}
                  </Typography>
                </Grid>
                <Grid item xs={12}>
                  <Typography>
                    <strong>Account Number:</strong> {accountInfo.accountNumber}
                  </Typography>
                </Grid>
              </Grid>
            </Paper>
          )}
        </Paper>
      </Container>
    </Layout>
  );
};

export default AccountHolderDashboard;
