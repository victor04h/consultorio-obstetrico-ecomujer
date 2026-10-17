import { createTheme } from "@mui/material/styles";

const theme = createTheme({
  palette: {
    primary: { main: "#2E7A68" },
    secondary: { main: "#8A6D3B" },
    background: { default: "#f4f6f8" },
  },
  shape: { borderRadius: 10 },
  typography: {
    fontFamily: '"Roboto", "Segoe UI", Arial, sans-serif',
  },
});

export default theme;
