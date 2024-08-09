import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import {
  Button,
  Container,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableFooter,
  TablePagination,
  TableRow,
  IconButton,
  Typography,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
} from "@mui/material";
import { Edit, Delete } from "@mui/icons-material";
import Layout from "../Layout/Layout";
import "./ManageUsers.css";

const ManageUsers = () => {
  const [accounts, setAccounts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [open, setOpen] = useState(false);
  const [accountToDelete, setAccountToDelete] = useState(null);
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem("token");
    console.log("Hereeeeeee")
    axios
      .get("http://localhost:8080/v1/accounts/all-accounts", {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      .then((response) => {
        setAccounts(response.data);
        setLoading(false);
      })
      .catch((err) => {
        setError("Failed to fetch accounts" + err);
        setLoading(false);
      });
  }, []);

  if (loading) return <Typography variant="h6">Loading...</Typography>;
  if (error) return <Typography variant="h6">{error}</Typography>;

  const handleEdit = (accountId) => {
    navigate(`/edit-account/${accountId}`);
  };

  const handleDelete = (id) => {
    const token = localStorage.getItem("token");
    axios
      .delete(`http://localhost:8080/v1/accounts/delete-account/${id}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      .then(() => {
        setAccounts(accounts.filter((account) => account.id !== id));
        handleClose();
      })
      .catch((err) => {
        setError("Failed to delete account" + err);
      });
  };

  const handleClickOpen = (accountId) => {
    setAccountToDelete(accountId);
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
    setAccountToDelete(null);
  };

  const handleConfirmDelete = () => {
    handleDelete(accountToDelete);
  };

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
        className="manage-users-container"
      >
        <Paper elevation={5} className="manage-users-paper">
          <Typography variant="h4" gutterBottom className="manage-users-title">
            Manage Users
          </Typography>
          <Button
            variant="contained"
            color="success"
            onClick={() => navigate("/create-account")}
            className="create-account-button"
          >
            Create Account
          </Button>
          {accounts.length <= 1 ? (
            <Typography variant="h6">No accounts available</Typography>
          ) : (
            <TableContainer component={Paper}>
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell sx={{ color: "white" }}>Username</TableCell>
                    <TableCell sx={{ color: "white" }}>Email</TableCell>
                    <TableCell sx={{ color: "white" }}>Address</TableCell>
                    <TableCell sx={{ color: "white" }}>Balance</TableCell>
                    <TableCell sx={{ color: "white" }}>
                      Account Number
                    </TableCell>
                    <TableCell sx={{ color: "white" }}>Actions</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {accounts
                    .slice(1)
                    .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                    .map((account) => (
                      <TableRow key={account.id}>
                        <TableCell>{account.username}</TableCell>
                        <TableCell>{account.email}</TableCell>
                        <TableCell>{account.address}</TableCell>
                        <TableCell>{account.balance}</TableCell>
                        <TableCell>{account.accountNumber}</TableCell>
                        <TableCell>
                          <IconButton
                            onClick={() => handleEdit(account.id)}
                            sx={{ color: "#2e8b57" }}
                          >
                            <Edit />
                          </IconButton>
                          <IconButton
                            onClick={() => handleClickOpen(account.id)}
                            sx={{ color: "#3cb371" }}
                          >
                            <Delete />
                          </IconButton>
                        </TableCell>
                      </TableRow>
                    ))}
                </TableBody>
                <TableFooter>
                  <TableRow>
                    <TablePagination
                      rowsPerPageOptions={[10, 25, 50]}
                      count={accounts.length - 1}
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

      <Dialog open={open} onClose={handleClose}>
        <DialogTitle>Confirm Delete</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Are you sure you want to delete this account?
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose} sx={{ color: "black" }}>
            Cancel
          </Button>
          <Button onClick={handleConfirmDelete} sx={{ color: "red" }}>
            Delete
          </Button>
        </DialogActions>
      </Dialog>
    </Layout>
  );
};

export default ManageUsers;
