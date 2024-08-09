import React from "react";
import { AppBar, Toolbar, Typography, Button, Container } from "@mui/material";
import { Link, useNavigate } from "react-router-dom";
import "./Navbar.css";
import { useAuth } from "../../hooks/AuthContext";

const Navbar = () => {
  const { authState, setAuthState } = useAuth();
  const navigate = useNavigate();

  const handleLogin = () => {
    navigate("/login");
  };

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("role");
    localStorage.removeItem("accountId");
    setAuthState({
      isAuthenticated: false,
      userRole: "",
    });
    navigate("/");
  };

  return (
    <AppBar position="static" className="navbar">
      <Container>
        <Toolbar>
          <Typography variant="h6" className="navbar-title">
            BankingApp
          </Typography>
          <div className="navbar-links">
            {authState.isAuthenticated ? (
              <>
                {authState.userRole === "admin" && (
                  <>
                    <Button
                      color="inherit"
                      component={Link}
                      to="/admin-dashboard"
                    >
                      Admin Dashboard
                    </Button>
                    <Button color="inherit" component={Link} to="/manage-users">
                      Manage Users
                    </Button>
                    <Button
                      color="inherit"
                      component={Link}
                      to="/view-transactions"
                    >
                      View Transactions
                    </Button>
                  </>
                )}
                {authState.userRole === "account-holder" && (
                  <Button
                    color="inherit"
                    onClick={() => navigate("/account-holder-dashboard")}
                  >
                    {" "}
                    Dashboard
                  </Button>
                )}
                <Button color="inherit" onClick={handleLogout}>
                  Logout
                </Button>
              </>
            ) : (
              <Button color="inherit" onClick={handleLogin}>
                Login
              </Button>
            )}
          </div>
        </Toolbar>
      </Container>
    </AppBar>
  );
};

export default Navbar;
