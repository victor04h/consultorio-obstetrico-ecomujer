import { useEffect, useState } from "react";
import Alert from "@mui/material/Alert";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import Checkbox from "@mui/material/Checkbox";
import Chip from "@mui/material/Chip";
import Dialog from "@mui/material/Dialog";
import DialogActions from "@mui/material/DialogActions";
import DialogContent from "@mui/material/DialogContent";
import DialogTitle from "@mui/material/DialogTitle";
import FormControlLabel from "@mui/material/FormControlLabel";
import FormGroup from "@mui/material/FormGroup";
import IconButton from "@mui/material/IconButton";
import Paper from "@mui/material/Paper";
import Switch from "@mui/material/Switch";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import TextField from "@mui/material/TextField";
import Tooltip from "@mui/material/Tooltip";
import Typography from "@mui/material/Typography";
import AddIcon from "@mui/icons-material/Add";
import EditIcon from "@mui/icons-material/Edit";

import api, { mensajeError } from "../api/client";
import PageHeader from "../components/PageHeader";

const VACIO = { nombre: "", descripcion: "", permisoIds: [] };

const NOMBRES_MODULO = {
  PERFILES: "Perfiles",
  USUARIOS: "Usuarios",
  CATEGORIAS: "Categorías",
  PRODUCTOS: "Productos y servicios",
  PROFESIONALES: "Profesionales",
  PACIENTES: "Pacientes",
  CITAS: "Citas",
  REPROGRAMACIONES: "Reprogramaciones",
  HISTORIAL_CLINICO: "Historial clínico",
  CALIFICACIONES: "Calificaciones",
};

function nombreModulo(modulo) {
  return NOMBRES_MODULO[modulo] || modulo;
}

export default function PerfilesPage() {
  const [perfiles, setPerfiles] = useState([]);
  const [permisos, setPermisos] = useState([]);
  const [error, setError] = useState("");
  const [dialogoAbierto, setDialogoAbierto] = useState(false);
  const [editando, setEditando] = useState(null);
  const [form, setForm] = useState(VACIO);
  const [guardando, setGuardando] = useState(false);

  const cargar = async () => {
    try {
      const [{ data: perfilesData }, { data: permisosData }] = await Promise.all([
        api.get("/admin/perfiles"),
        api.get("/admin/permisos"),
      ]);
      setPerfiles(perfilesData);
      setPermisos(permisosData);
    } catch (err) {
      setError(mensajeError(err, "No se pudieron cargar los perfiles"));
    }
  };

  useEffect(() => {
    cargar();
  }, []);

  const abrirNuevo = () => {
    setEditando(null);
    setForm(VACIO);
    setDialogoAbierto(true);
  };

  const abrirEdicion = (perfil) => {
    setEditando(perfil);
    setForm({
      nombre: perfil.nombre,
      descripcion: perfil.descripcion || "",
      permisoIds: perfil.permisos.map((p) => p.id),
    });
    setDialogoAbierto(true);
  };

  const alternarPermiso = (idPermiso) => {
    setForm((prev) => ({
      ...prev,
      permisoIds: prev.permisoIds.includes(idPermiso)
        ? prev.permisoIds.filter((id) => id !== idPermiso)
        : [...prev.permisoIds, idPermiso],
    }));
  };

  const guardar = async () => {
    setGuardando(true);
    setError("");
    try {
      if (editando) {
        await api.put(`/admin/perfiles/${editando.id}`, form);
      } else {
        await api.post("/admin/perfiles", form);
      }
      setDialogoAbierto(false);
      await cargar();
    } catch (err) {
      setError(mensajeError(err, "No se pudo guardar el perfil"));
    } finally {
      setGuardando(false);
    }
  };

  const alternarEstado = async (perfil) => {
    const nuevoEstado = perfil.estado === "ACTIVO" ? "INACTIVO" : "ACTIVO";
    try {
      await api.patch(`/admin/perfiles/${perfil.id}/estado`, { estado: nuevoEstado });
      await cargar();
    } catch (err) {
      setError(mensajeError(err, "No se pudo cambiar el estado del perfil"));
    }
  };

  return (
    <Box>
      <PageHeader
        titulo="Perfiles"
        subtitulo="Roles del sistema y los permisos que llevan asociados"
        acciones={
          <Button variant="contained" startIcon={<AddIcon />} onClick={abrirNuevo}>
            Nuevo perfil
          </Button>
        }
      />

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Nombre</TableCell>
              <TableCell>Descripción</TableCell>
              <TableCell>Permisos</TableCell>
              <TableCell>Activo</TableCell>
              <TableCell align="right">Acciones</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {perfiles.map((perfil) => (
              <TableRow key={perfil.id} hover>
                <TableCell>{perfil.nombre}</TableCell>
                <TableCell>{perfil.descripcion}</TableCell>
                <TableCell>
                  {perfil.permisos.length === 0 ? (
                    <Typography variant="body2" color="text.secondary">
                      Sin permisos
                    </Typography>
                  ) : (
                    <Box sx={{ display: "flex", flexWrap: "wrap", gap: 0.5 }}>
                      {perfil.permisos.map((p) => (
                        <Chip key={p.id} size="small" label={nombreModulo(p.modulo)} />
                      ))}
                    </Box>
                  )}
                </TableCell>
                <TableCell>
                  <Switch checked={perfil.estado === "ACTIVO"} onChange={() => alternarEstado(perfil)} />
                </TableCell>
                <TableCell align="right">
                  <Tooltip title="Editar">
                    <IconButton onClick={() => abrirEdicion(perfil)}>
                      <EditIcon fontSize="small" />
                    </IconButton>
                  </Tooltip>
                </TableCell>
              </TableRow>
            ))}
            {perfiles.length === 0 && (
              <TableRow>
                <TableCell colSpan={5} align="center">
                  Aún no hay perfiles registrados.
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </TableContainer>

      <Dialog open={dialogoAbierto} onClose={() => setDialogoAbierto(false)} maxWidth="sm" fullWidth>
        <DialogTitle>{editando ? "Editar perfil" : "Nuevo perfil"}</DialogTitle>
        <DialogContent sx={{ display: "flex", flexDirection: "column", gap: 2, mt: 1 }}>
          <TextField
            label="Nombre"
            value={form.nombre}
            onChange={(e) => setForm({ ...form, nombre: e.target.value })}
            fullWidth
            required
          />
          <TextField
            label="Descripción"
            value={form.descripcion}
            onChange={(e) => setForm({ ...form, descripcion: e.target.value })}
            fullWidth
            multiline
            rows={2}
          />
          <Typography variant="subtitle2">Secciones del panel a las que tiene acceso este perfil</Typography>
          {permisos.length === 0 ? (
            <Alert severity="info">Todavia no hay permisos en el catalogo.</Alert>
          ) : (
            <FormGroup>
              {permisos.map((permiso) => (
                <FormControlLabel
                  key={permiso.id}
                  control={
                    <Checkbox
                      checked={form.permisoIds.includes(permiso.id)}
                      onChange={() => alternarPermiso(permiso.id)}
                    />
                  }
                  label={nombreModulo(permiso.modulo)}
                />
              ))}
            </FormGroup>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDialogoAbierto(false)}>Cancelar</Button>
          <Button onClick={guardar} variant="contained" disabled={guardando || !form.nombre}>
            Guardar
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
