import React from "react";
import { Typography, Container, Paper } from "@mui/material";
import Layout from "../Layout/Layout";
import "./LandingPage.css";

const LandingPage = () => {
  return (
    <Layout>
      <div className="landing-page">
        <main className="main-content">
          <Container>
            <Paper elevation={3} className="hero-paper">
              <Typography variant="h2" className="hero-title">
                Welcome to BankingApp
              </Typography>
              <Typography variant="h5" className="hero-subtitle">
                Your secure place to manage your finances.
              </Typography>
            </Paper>
          </Container>
        </main>
      </div>
    </Layout>
  );
};

export default LandingPage;
