import React, { useEffect, useState } from "react";
import axios from "axios";
import bcrypt from "bcryptjs";
import { useNavigate, useParams } from "react-router-dom";
import {
  Container,
  Paper,
  Typography,
  TextField,
  Button,
  CircularProgress,
  Alert,
} from "@mui/material";
import CryptoJS from "crypto-js";
import Layout from "../Layout/Layout";
import "./EditAccount.css";

const EditAccount = () => {
  const { accountId } = useParams();
  const [account, setAccount] = useState({
    username: "",
    password: "",
    email: "",
    address: "",
    balance: "",
    accountNumber: "",
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [passwordError, setPasswordError] = useState("");
  const [balanceError, setBalanceError] = useState("");
  const [isPasswordModified, setIsPasswordModified] = useState(false);
  const [isBalanceModified, setIsBalanceModified] = useState(false);
  const navigate = useNavigate();

  const passwordRegex = /^(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{6,}$/;

  useEffect(() => {
    const token = localStorage.getItem("token");
    axios
      .get(`http://localhost:8080/v1/accounts/get-account/${accountId}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      .then((response) => {
        setAccount(response.data);
        setLoading(false);
      })
      .catch((err) => {
        console.log(err.response.data);
        setError("Failed to fetch account details");
        setLoading(false);
      });
  }, [accountId]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setAccount({
      ...account,
      [name]: value,
    });

    if (name === "password") {
      setIsPasswordModified(true);
      if (!passwordRegex.test(value) && value !== "") {
        setPasswordError(
          "Password must be at least 6 characters long, include an uppercase letter, a number, and a special character."
        );
      } else {
        setPasswordError("");
      }
    }

    if (name === "balance") {
      setIsBalanceModified(true);
      const numericValue = parseFloat(value);
      if (numericValue <= 0 && value !== "") {
        setBalanceError("Balance must be greater than 0.");
      } else {
        setBalanceError("");
      }
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if ((isPasswordModified && passwordError) || (isBalanceModified && balanceError)) {
      return; 
    }

    const token = localStorage.getItem("token");
    const updatedAccount = { ...account };

    try {
      if (isPasswordModified && account.password !== "") {
        //const salt = await bcrypt.genSalt(10);
        updatedAccount.password = CryptoJS.SHA256(account.password).toString(CryptoJS.enc.Hex);
        
      }

      await axios.put(
        `http://localhost:8080/v1/accounts/edit-account/${accountId}`,
        updatedAccount,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      navigate("/manage-users");
    } catch (err) {
      setError("Failed to update account");
    }
  };

  if (loading)
    return (
      <Layout>
        <Container
          component="main"
          maxWidth="sm"
          className="edit-account-container"
        >
          <CircularProgress />
        </Container>
      </Layout>
    );

  if (error)
    return (
      <Layout>
        <Container
          component="main"
          maxWidth="sm"
          className="edit-account-container"
        >
          <Alert severity="error">{error}</Alert>
        </Container>
      </Layout>
    );

  return (
    <Layout>
      <Container
        component="main"
        maxWidth="sm"
        className="edit-account-container"
      >
        <Paper elevation={5} className="edit-account-paper">
          <Typography variant="h4" gutterBottom className="edit-account-title">
            Edit Account
          </Typography>
          <form onSubmit={handleSubmit}>
            <TextField
              label="Username"
              name="username"
              value={account.username}
              onChange={handleChange}
              fullWidth
              required
              margin="normal"
              variant="outlined"
            />
            <TextField
              label="Password"
              name="password"
              type="password"
              value={account.password}
              inputProps={{ minLength: 6 }}
              onChange={handleChange}
              fullWidth
              margin="normal"
              variant="outlined"
              helperText={passwordError}
              error={!!passwordError}
            />
            <TextField
              label="Email"
              name="email"
              type="email"
              value={account.email}
              onChange={handleChange}
              fullWidth
              required
              margin="normal"
              variant="outlined"
            />
            <TextField
              label="Address"
              name="address"
              value={account.address}
              onChange={handleChange}
              fullWidth
              margin="normal"
              variant="outlined"
            />
            <TextField
              label="Balance"
              name="balance"
              type="number"
              value={account.balance}
              onChange={handleChange}
              fullWidth
              margin="normal"
              variant="outlined"
              helperText={balanceError}
              error={!!balanceError}
            />
            <Button
              type="submit"
              variant="contained"
              color="success"
              fullWidth
              className="edit-account-button"
            >
              Update
            </Button>
          </form>
        </Paper>
      </Container>
    </Layout>
  );
};

export default EditAccount;
