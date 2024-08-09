import React from "react";
import { Link } from "react-router-dom";
import Layout from "../Layout/Layout";
import "./Unauthorized.css";

const Unauthorized = () => {
  return (
    <Layout>
      <div className="unauthorized-container">
        <h1 className="unauthorized-title">Access Denied</h1>
        <p className="unauthorized-message">
          You do not have permission to view this page.
        </p>
        <Link to="/" className="unauthorized-link">
          Go to Home
        </Link>
      </div>
    </Layout>
  );
};

export default Unauthorized;
