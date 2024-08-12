import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import {
  Button,
  TextField,
  Typography,
  Paper,
  Container,
  Box,
  Grid,
} from "@mui/material";
import Layout from "../Layout/Layout";
import "./Login.css";
import { useAuth } from "../../hooks/AuthContext";
import CryptoJS from "crypto-js";

const Login = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const navigate = useNavigate();
  const { setAuthState } = useAuth();

  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      let passwordToSend = password;
    if (username.toLowerCase() !== "admin") {
      passwordToSend = CryptoJS.SHA256(password).toString(CryptoJS.enc.Hex);
    }

    const response = await axios.post("http://localhost:8080/api/v2/auth/login", {
      username,
      password: passwordToSend,
    },
    { headers: { 'Content-Type': 'application/json' } });

      if (response.status === 200) {
        const jwtToken = response.headers['authorization']?.split(' ')[1];
        console.log(jwtToken);
        const  user = response.data;
        const role = user.role;
        localStorage.setItem("token", jwtToken);
        localStorage.setItem("role", role);
        localStorage.setItem("userId", user.id);

        setAuthState({
          isAuthenticated: true,
          userRole: role,
        });

        if (role === "admin") {
          navigate("/admin-dashboard");
        } else if (role === "account-holder") {
          navigate("/account-holder-dashboard");
        }
      } else {
        setErrorMessage("Login failed");
      }
    } catch (error) {
      if (error.response && error.response.status === 401) {
        setErrorMessage(error.response.data);
      } else {
        console.log(error)
        setErrorMessage("An error occurred. Please try again.");
      }
    }
  };

  return (
    <Layout>
      <Container component="main" maxWidth="lg">
        <Paper elevation={3} className="login-paper">
          <Grid container spacing={2}>
            <Grid item xs={12} md={6}>
              <Box className="login-form-container">
                <Typography component="h1" variant="h5" className="login-title">
                  Login
                </Typography>
                {errorMessage && (
                  <Typography color="error">{errorMessage}</Typography>
                )}
                <Box
                  component="form"
                  onSubmit={handleLogin}
                  className="login-form"
                >
                  <TextField
                    variant="outlined"
                    margin="normal"
                    required
                    fullWidth
                    id="username"
                    label="Username"
                    name="username"
                    autoComplete="username"
                    autoFocus
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                  />
                  <TextField
                    variant="outlined"
                    margin="normal"
                    required
                    fullWidth
                    name="password"
                    label="Password"
                    type="password"
                    id="password"
                    autoComplete="current-password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                  />
                  <Button
                    type="submit"
                    fullWidth
                    variant="contained"
                    id="submit-btn"
                    className="login-button"
                  >
                    Login
                  </Button>
                </Box>
              </Box>
            </Grid>
            <Grid item xs={12} md={6} className="login-image-container">
              <img
                src="src/assets/images/hero_1.jpg"
                alt="Login"
                className="login-image"
              />
            </Grid>
          </Grid>
        </Paper>
      </Container>
    </Layout>
  );
};

export default Login;
