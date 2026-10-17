import { useState } from "react";
import { NavLink, Outlet, useNavigate } from "react-router-dom";
import AppBar from "@mui/material/AppBar";
import Avatar from "@mui/material/Avatar";
import Box from "@mui/material/Box";
import CssBaseline from "@mui/material/CssBaseline";
import Divider from "@mui/material/Divider";
import Drawer from "@mui/material/Drawer";
import IconButton from "@mui/material/IconButton";
import List from "@mui/material/List";
import ListItemButton from "@mui/material/ListItemButton";
import ListItemIcon from "@mui/material/ListItemIcon";
import ListItemText from "@mui/material/ListItemText";
import Menu from "@mui/material/Menu";
import MenuItem from "@mui/material/MenuItem";
import Toolbar from "@mui/material/Toolbar";
import Typography from "@mui/material/Typography";

import MenuIcon from "@mui/icons-material/Menu";
import DashboardIcon from "@mui/icons-material/Dashboard";
import BadgeIcon from "@mui/icons-material/Badge";
import ManageAccountsIcon from "@mui/icons-material/ManageAccounts";
import CategoryIcon from "@mui/icons-material/Category";
import MedicalServicesIcon from "@mui/icons-material/MedicalServices";
import LocalHospitalIcon from "@mui/icons-material/LocalHospital";
import PeopleIcon from "@mui/icons-material/People";
import EventAvailableIcon from "@mui/icons-material/EventAvailable";
import EventRepeatIcon from "@mui/icons-material/EventRepeat";
import HistoryEduIcon from "@mui/icons-material/HistoryEdu";
import StarRateIcon from "@mui/icons-material/StarRate";
import LogoutIcon from "@mui/icons-material/Logout";

import { useAuth } from "../context/AuthContext";

const DRAWER_WIDTH = 260;

// "modulo: null" (Inicio) siempre se muestra. "soloAdministrador: true" solo
// se muestra si el perfil que inicio sesion es Administrador (no se controla
// con un checkbox de Perfiles, a diferencia del resto).
const NAV_ITEMS = [
  { texto: "Inicio", ruta: "/", icono: <DashboardIcon />, modulo: null },
  { texto: "Perfiles", ruta: "/perfiles", icono: <BadgeIcon />, modulo: "PERFILES" },
  { texto: "Usuarios", ruta: "/usuarios", icono: <ManageAccountsIcon />, soloAdministrador: true },
  { texto: "Categorías", ruta: "/categorias", icono: <CategoryIcon />, modulo: "CATEGORIAS" },
  { texto: "Productos y servicios", ruta: "/productos", icono: <MedicalServicesIcon />, modulo: "PRODUCTOS" },
  { texto: "Profesionales", ruta: "/profesionales", icono: <LocalHospitalIcon />, modulo: "PROFESIONALES" },
  { texto: "Pacientes", ruta: "/pacientes", icono: <PeopleIcon />, modulo: "PACIENTES" },
  { texto: "Citas", ruta: "/citas", icono: <EventAvailableIcon />, modulo: "CITAS" },
  { texto: "Reprogramaciones", ruta: "/reprogramaciones", icono: <EventRepeatIcon />, modulo: "REPROGRAMACIONES" },
  { texto: "Historial clínico", ruta: "/historial-clinico", icono: <HistoryEduIcon />, modulo: "HISTORIAL_CLINICO" },
  { texto: "Calificaciones", ruta: "/calificaciones", icono: <StarRateIcon />, modulo: "CALIFICACIONES" },
];

export default function DashboardLayout() {
  const { usuario, logout, tienePermiso } = useAuth();
  const navigate = useNavigate();
  const [movilAbierto, setMovilAbierto] = useState(false);
  const [anclaMenu, setAnclaMenu] = useState(null);

  const cerrarSesion = () => {
    setAnclaMenu(null);
    logout();
    navigate("/login", { replace: true });
  };

  const itemsVisibles = NAV_ITEMS.filter((item) => {
    if (item.soloAdministrador) return usuario?.rol === "Administrador";
    return tienePermiso(item.modulo);
  });

  const contenidoDrawer = (
    <Box>
      <Toolbar>
        <Typography variant="h6" fontWeight={700} color="primary" noWrap>
          Consultorio Obstétrico
        </Typography>
      </Toolbar>
      <Divider />
      <List>
        {itemsVisibles.map((item) => (
          <ListItemButton
            key={item.ruta}
            component={NavLink}
            to={item.ruta}
            end={item.ruta === "/"}
            onClick={() => setMovilAbierto(false)}
            sx={{
              "&.active": {
                backgroundColor: "action.selected",
                borderRight: "3px solid",
                borderColor: "primary.main",
              },
            }}
          >
            <ListItemIcon>{item.icono}</ListItemIcon>
            <ListItemText primary={item.texto} />
          </ListItemButton>
        ))}
      </List>
    </Box>
  );

  return (
    <Box sx={{ display: "flex" }}>
      <CssBaseline />
      <AppBar
        position="fixed"
        sx={{ width: { md: `calc(100% - ${DRAWER_WIDTH}px)` }, ml: { md: `${DRAWER_WIDTH}px` } }}
      >
        <Toolbar sx={{ display: "flex", justifyContent: "space-between" }}>
          <IconButton
            color="inherit"
            edge="start"
            onClick={() => setMovilAbierto(true)}
            sx={{ display: { md: "none" } }}
          >
            <MenuIcon />
          </IconButton>
          <Typography variant="subtitle1">Panel administrativo</Typography>
          <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
            <Typography variant="body2" sx={{ display: { xs: "none", sm: "block" } }}>
              {usuario?.nombreCompleto} ({usuario?.rol})
            </Typography>
            <IconButton onClick={(e) => setAnclaMenu(e.currentTarget)} size="small">
              <Avatar sx={{ width: 32, height: 32, bgcolor: "secondary.main" }}>
                {usuario?.nombreCompleto?.charAt(0)?.toUpperCase() || "A"}
              </Avatar>
            </IconButton>
            <Menu anchorEl={anclaMenu} open={Boolean(anclaMenu)} onClose={() => setAnclaMenu(null)}>
              <MenuItem onClick={cerrarSesion}>
                <LogoutIcon fontSize="small" sx={{ mr: 1 }} /> Cerrar sesión
              </MenuItem>
            </Menu>
          </Box>
        </Toolbar>
      </AppBar>

      <Box component="nav" sx={{ width: { md: DRAWER_WIDTH }, flexShrink: { md: 0 } }}>
        <Drawer
          variant="temporary"
          open={movilAbierto}
          onClose={() => setMovilAbierto(false)}
          ModalProps={{ keepMounted: true }}
          sx={{ display: { xs: "block", md: "none" }, "& .MuiDrawer-paper": { width: DRAWER_WIDTH } }}
        >
          {contenidoDrawer}
        </Drawer>
        <Drawer
          variant="permanent"
          sx={{ display: { xs: "none", md: "block" }, "& .MuiDrawer-paper": { width: DRAWER_WIDTH } }}
          open
        >
          {contenidoDrawer}
        </Drawer>
      </Box>

      <Box
        component="main"
        sx={{
          flexGrow: 1,
          p: 3,
          width: { md: `calc(100% - ${DRAWER_WIDTH}px)` },
          mt: 8,
        }}
      >
        <Outlet />
      </Box>
    </Box>
  );
}
