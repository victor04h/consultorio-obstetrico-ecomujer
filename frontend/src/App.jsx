import { BrowserRouter, Route, Routes } from "react-router-dom";
import CssBaseline from "@mui/material/CssBaseline";
import { ThemeProvider } from "@mui/material/styles";

import theme from "./theme";
import { AuthProvider } from "./context/AuthContext";
import ProtectedRoute from "./routes/ProtectedRoute";
import PermisoRoute from "./routes/PermisoRoute";
import DashboardLayout from "./layout/DashboardLayout";

import LoginPage from "./pages/LoginPage";
import InicioPage from "./pages/InicioPage";
import PerfilesPage from "./pages/PerfilesPage";
import UsuariosPage from "./pages/UsuariosPage";
import CategoriasPage from "./pages/CategoriasPage";
import ProductosPage from "./pages/ProductosPage";
import ProfesionalesPage from "./pages/ProfesionalesPage";
import PacientesPage from "./pages/PacientesPage";
import CitasPage from "./pages/CitasPage";
import ReprogramacionesPage from "./pages/ReprogramacionesPage";
import HistorialClinicoPage from "./pages/HistorialClinicoPage";
import CalificacionesPage from "./pages/CalificacionesPage";

export default function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <BrowserRouter>
        <AuthProvider>
          <Routes>
            <Route path="/login" element={<LoginPage />} />

            <Route element={<ProtectedRoute />}>
              <Route element={<DashboardLayout />}>
                <Route path="/" element={<InicioPage />} />

                <Route element={<PermisoRoute modulo="PERFILES" />}>
                  <Route path="/perfiles" element={<PerfilesPage />} />
                </Route>
                <Route element={<PermisoRoute soloAdministrador />}>
                  <Route path="/usuarios" element={<UsuariosPage />} />
                </Route>
                <Route element={<PermisoRoute modulo="CATEGORIAS" />}>
                  <Route path="/categorias" element={<CategoriasPage />} />
                </Route>
                <Route element={<PermisoRoute modulo="PRODUCTOS" />}>
                  <Route path="/productos" element={<ProductosPage />} />
                </Route>
                <Route element={<PermisoRoute modulo="PROFESIONALES" />}>
                  <Route path="/profesionales" element={<ProfesionalesPage />} />
                </Route>
                <Route element={<PermisoRoute modulo="PACIENTES" />}>
                  <Route path="/pacientes" element={<PacientesPage />} />
                </Route>
                <Route element={<PermisoRoute modulo="CITAS" />}>
                  <Route path="/citas" element={<CitasPage />} />
                </Route>
                <Route element={<PermisoRoute modulo="REPROGRAMACIONES" />}>
                  <Route path="/reprogramaciones" element={<ReprogramacionesPage />} />
                </Route>
                <Route element={<PermisoRoute modulo="HISTORIAL_CLINICO" />}>
                  <Route path="/historial-clinico" element={<HistorialClinicoPage />} />
                </Route>
                <Route element={<PermisoRoute modulo="CALIFICACIONES" />}>
                  <Route path="/calificaciones" element={<CalificacionesPage />} />
                </Route>
              </Route>
            </Route>
          </Routes>
        </AuthProvider>
      </BrowserRouter>
    </ThemeProvider>
  );
}
