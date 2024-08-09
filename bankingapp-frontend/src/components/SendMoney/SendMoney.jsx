import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import {
  Container,
  Paper,
  Typography,
  TextField,
  Button,
  Grid,
} from "@mui/material";
import "./SendMoney.css";
import Layout from "../Layout/Layout";

const SendMoney = () => {
  const [formData, setFormData] = useState({
    recieverAccountNumber: "",
    amount: "",
    description: "",
  });
  const [errorMessage, setErrorMessage] = useState("");
  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrorMessage("");
    try {
      const token = localStorage.getItem("token");
      const response = await axios.post(
        "http://localhost:8080/v1/transactions/transfer-money",
        formData,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      if (response.status === 201) {
        navigate("/transaction-success", {
          state: { transaction: response.data },
        });
      } else {
        setErrorMessage(response.data);
      }
    } catch (error) {
      if (error.response && error.response.status === 400) {
        setErrorMessage(error.response.data);
      } else {
        console.log(error);
        setErrorMessage("An error occurred. Please try again.");
      }
    }
  };

  return (
    <Layout>
      <Container
        component="main"
        maxWidth="md"
        className="send-money-container"
      >
        <Paper elevation={6} className="send-money-paper">
          <Typography variant="h4" gutterBottom className="send-money-title">
            Send Money
          </Typography>
          {errorMessage && (
            <Typography color="error">{errorMessage}</Typography>
          )}
          <Grid container spacing={2}>
            <Grid item xs={12} md={6}>
              <form onSubmit={handleSubmit} className="send-money-form">
                <TextField
                  fullWidth
                  label="Receiver Account Number"
                  name="recieverAccountNumber"
                  value={formData.recieverAccountNumber}
                  onChange={handleChange}
                  required
                  margin="normal"
                />
                <TextField
                  fullWidth
                  label="Amount"
                  type="number"
                  name="amount"
                  value={formData.amount}
                  onChange={handleChange}
                  required
                  margin="normal"
                  inputProps={{ step: "any", min: "50" }}
                />
                <TextField
                  fullWidth
                  label="Description"
                  name="description"
                  value={formData.description}
                  onChange={handleChange}
                  margin="normal"
                />
                <Button
                  type="submit"
                  variant="contained"
                  color="success"
                  fullWidth
                  className="send-money-button"
                >
                  Send Money
                </Button>
              </form>
            </Grid>
            <Grid item xs={12} md={6} className="send-money-image-container">
              <img
                src="src/assets/images/hero_2.jpg" // Replace with your image path
                alt="Send Money"
                className="send-money-image"
              />
            </Grid>
          </Grid>
        </Paper>
      </Container>
    </Layout>
  );
};

export default SendMoney;
