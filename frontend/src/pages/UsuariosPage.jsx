import { useEffect, useMemo, useState } from "react";
import Alert from "@mui/material/Alert";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import Chip from "@mui/material/Chip";
import Dialog from "@mui/material/Dialog";
import DialogActions from "@mui/material/DialogActions";
import DialogContent from "@mui/material/DialogContent";
import DialogTitle from "@mui/material/DialogTitle";
import MenuItem from "@mui/material/MenuItem";
import Paper from "@mui/material/Paper";
import Switch from "@mui/material/Switch";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import TextField from "@mui/material/TextField";
import Typography from "@mui/material/Typography";
import AddIcon from "@mui/icons-material/Add";

import api, { mensajeError } from "../api/client";
import PageHeader from "../components/PageHeader";

const VACIO = { nombres: "", apellidos: "", correo: "", contrasena: "", idPerfil: "", idProfesional: "" };

export default function UsuariosPage() {
  const [usuarios, setUsuarios] = useState([]);
  const [perfiles, setPerfiles] = useState([]);
  const [profesionales, setProfesionales] = useState([]);
  const [error, setError] = useState("");

  const [dialogoAbierto, setDialogoAbierto] = useState(false);
  const [form, setForm] = useState(VACIO);
  const [guardando, setGuardando] = useState(false);

  const cargar = async () => {
    try {
      const { data } = await api.get("/admin/usuarios");
      setUsuarios(data);
    } catch (err) {
      setError(mensajeError(err, "No se pudieron cargar las cuentas de usuario"));
    }
  };

  useEffect(() => {
    cargar();
    api
      .get("/admin/perfiles")
      .then(({ data }) => setPerfiles(data.filter((p) => p.nombre !== "Paciente")))
      .catch(() => {});
    api
      .get("/admin/profesionales")
      .then(({ data }) => setProfesionales(data))
      .catch(() => {});
  }, []);

  const perfilSeleccionado = useMemo(
    () => perfiles.find((p) => String(p.id) === String(form.idPerfil)),
    [perfiles, form.idPerfil]
  );
  const esObstetra = perfilSeleccionado?.nombre === "Obstetra";

  const abrirNuevo = () => {
    setForm(VACIO);
    setDialogoAbierto(true);
  };

  const guardar = async () => {
    setGuardando(true);
    setError("");
    try {
      await api.post("/admin/usuarios", {
        ...form,
        idPerfil: Number(form.idPerfil),
        idProfesional: esObstetra && form.idProfesional ? Number(form.idProfesional) : null,
      });
      setDialogoAbierto(false);
      await cargar();
    } catch (err) {
      setError(mensajeError(err, "No se pudo crear la cuenta"));
    } finally {
      setGuardando(false);
    }
  };

  const alternarEstado = async (usuario) => {
    const nuevoEstado = usuario.estado === "ACTIVO" ? "INACTIVO" : "ACTIVO";
    try {
      await api.patch(`/admin/usuarios/${usuario.id}/estado`, { estado: nuevoEstado });
      await cargar();
    } catch (err) {
      setError(mensajeError(err, "No se pudo cambiar el estado de la cuenta"));
    }
  };

  return (
    <Box>
      <PageHeader
        titulo="Usuarios"
        subtitulo="Cuentas de acceso al panel para Secretaria, Obstetra y otros Administradores"
        acciones={
          <Button variant="contained" startIcon={<AddIcon />} onClick={abrirNuevo}>
            Nueva cuenta
          </Button>
        }
      />

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      <Alert severity="info" sx={{ mb: 2 }}>
        Los pacientes no se crean aqui: ellos se registran solos desde el sitio publico. Esta pantalla es solo para
        el personal interno del consultorio.
      </Alert>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Nombre</TableCell>
              <TableCell>Correo</TableCell>
              <TableCell>Perfil</TableCell>
              <TableCell>Profesional vinculado</TableCell>
              <TableCell>Activo</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {usuarios.map((u) => (
              <TableRow key={u.id} hover>
                <TableCell>
                  {u.nombres} {u.apellidos}
                </TableCell>
                <TableCell>{u.correo}</TableCell>
                <TableCell>
                  <Chip size="small" label={u.nombrePerfil} />
                </TableCell>
                <TableCell>{u.nombreProfesional || "-"}</TableCell>
                <TableCell>
                  <Switch checked={u.estado === "ACTIVO"} onChange={() => alternarEstado(u)} />
                </TableCell>
              </TableRow>
            ))}
            {usuarios.length === 0 && (
              <TableRow>
                <TableCell colSpan={5} align="center">
                  Aún no hay cuentas internas creadas.
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </TableContainer>

      <Dialog open={dialogoAbierto} onClose={() => setDialogoAbierto(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Nueva cuenta interna</DialogTitle>
        <DialogContent sx={{ display: "flex", flexDirection: "column", gap: 2, mt: 1 }}>
          <Box sx={{ display: "flex", gap: 2 }}>
            <TextField
              label="Nombres"
              value={form.nombres}
              onChange={(e) => setForm({ ...form, nombres: e.target.value })}
              fullWidth
              required
            />
            <TextField
              label="Apellidos"
              value={form.apellidos}
              onChange={(e) => setForm({ ...form, apellidos: e.target.value })}
              fullWidth
              required
            />
          </Box>
          <TextField
            label="Correo"
            type="email"
            value={form.correo}
            onChange={(e) => setForm({ ...form, correo: e.target.value })}
            fullWidth
            required
          />
          <TextField
            label="Contraseña temporal"
            type="text"
            value={form.contrasena}
            onChange={(e) => setForm({ ...form, contrasena: e.target.value })}
            fullWidth
            required
            helperText="Compártesela a la persona para que la cambie después de entrar."
          />
          <TextField
            select
            label="Perfil"
            value={form.idPerfil}
            onChange={(e) => setForm({ ...form, idPerfil: e.target.value, idProfesional: "" })}
            fullWidth
            required
          >
            {perfiles.map((p) => (
              <MenuItem key={p.id} value={p.id}>
                {p.nombre}
              </MenuItem>
            ))}
          </TextField>
          {esObstetra && (
            <TextField
              select
              label="Profesional (obstetra) vinculado"
              value={form.idProfesional}
              onChange={(e) => setForm({ ...form, idProfesional: e.target.value })}
              fullWidth
              required
              helperText="Con cual registro del modulo Profesionales corresponde esta cuenta"
            >
              {profesionales.map((p) => (
                <MenuItem key={p.id} value={p.id}>
                  {p.nombres} {p.apellidos}
                </MenuItem>
              ))}
            </TextField>
          )}
          {esObstetra && profesionales.length === 0 && (
            <Typography variant="caption" color="text.secondary">
              Todavía no hay profesionales registrados. Crea primero al obstetra en el módulo Profesionales.
            </Typography>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDialogoAbierto(false)}>Cancelar</Button>
          <Button
            onClick={guardar}
            variant="contained"
            disabled={
              guardando ||
              !form.nombres ||
              !form.apellidos ||
              !form.correo ||
              !form.contrasena ||
              !form.idPerfil ||
              (esObstetra && !form.idProfesional)
            }
          >
            Crear cuenta
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
