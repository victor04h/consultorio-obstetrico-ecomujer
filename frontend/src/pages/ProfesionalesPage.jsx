import { useEffect, useState } from "react";
import Alert from "@mui/material/Alert";
import Avatar from "@mui/material/Avatar";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import Chip from "@mui/material/Chip";
import Dialog from "@mui/material/Dialog";
import DialogActions from "@mui/material/DialogActions";
import DialogContent from "@mui/material/DialogContent";
import DialogTitle from "@mui/material/DialogTitle";
import IconButton from "@mui/material/IconButton";
import List from "@mui/material/List";
import ListItem from "@mui/material/ListItem";
import ListItemText from "@mui/material/ListItemText";
import Paper from "@mui/material/Paper";
import Rating from "@mui/material/Rating";
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
import DeleteIcon from "@mui/icons-material/Delete";
import EditIcon from "@mui/icons-material/Edit";
import ScheduleIcon from "@mui/icons-material/Schedule";

import api, { mensajeError } from "../api/client";
import PageHeader from "../components/PageHeader";

const VACIO = { nombres: "", apellidos: "", especialidad: "", añosExperiencia: "", reseña: "", fotoUrl: "" };
const HORARIO_VACIO = { fecha: "", horaInicio: "", horaFin: "" };

export default function ProfesionalesPage() {
  const [profesionales, setProfesionales] = useState([]);
  const [error, setError] = useState("");

  const [dialogoAbierto, setDialogoAbierto] = useState(false);
  const [editando, setEditando] = useState(null);
  const [form, setForm] = useState(VACIO);
  const [guardando, setGuardando] = useState(false);

  const [horariosAbierto, setHorariosAbierto] = useState(false);
  const [profesionalHorarios, setProfesionalHorarios] = useState(null);
  const [horarios, setHorarios] = useState([]);
  const [nuevoHorario, setNuevoHorario] = useState(HORARIO_VACIO);

  const cargar = async () => {
    try {
      const { data } = await api.get("/admin/profesionales");
      setProfesionales(data);
    } catch (err) {
      setError(mensajeError(err, "No se pudieron cargar los profesionales"));
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

  const abrirEdicion = (profesional) => {
    setEditando(profesional);
    setForm({
      nombres: profesional.nombres,
      apellidos: profesional.apellidos,
      especialidad: profesional.especialidad || "",
      añosExperiencia: profesional.añosExperiencia ?? "",
      reseña: profesional.reseña || "",
      fotoUrl: profesional.fotoUrl || "",
    });
    setDialogoAbierto(true);
  };

  const guardar = async () => {
    setGuardando(true);
    setError("");
    try {
      const payload = {
        ...form,
        añosExperiencia: form.añosExperiencia === "" ? null : Number(form.añosExperiencia),
      };
      if (editando) {
        await api.put(`/admin/profesionales/${editando.id}`, payload);
      } else {
        await api.post("/admin/profesionales", payload);
      }
      setDialogoAbierto(false);
      await cargar();
    } catch (err) {
      setError(mensajeError(err, "No se pudo guardar el profesional"));
    } finally {
      setGuardando(false);
    }
  };

  const alternarEstado = async (profesional) => {
    const nuevoEstado = profesional.estado === "ACTIVO" ? "INACTIVO" : "ACTIVO";
    try {
      await api.patch(`/admin/profesionales/${profesional.id}/estado`, { estado: nuevoEstado });
      await cargar();
    } catch (err) {
      setError(mensajeError(err, "No se pudo cambiar el estado"));
    }
  };

  const abrirHorarios = async (profesional) => {
    setProfesionalHorarios(profesional);
    setNuevoHorario(HORARIO_VACIO);
    setHorariosAbierto(true);
    try {
      const { data } = await api.get(`/admin/profesionales/${profesional.id}/horarios`);
      setHorarios(data);
    } catch (err) {
      setError(mensajeError(err, "No se pudieron cargar los horarios"));
    }
  };

  const agregarHorario = async () => {
    try {
      await api.post(`/admin/profesionales/${profesionalHorarios.id}/horarios`, nuevoHorario);
      const { data } = await api.get(`/admin/profesionales/${profesionalHorarios.id}/horarios`);
      setHorarios(data);
      setNuevoHorario(HORARIO_VACIO);
    } catch (err) {
      setError(mensajeError(err, "No se pudo agregar el horario"));
    }
  };

  const eliminarHorario = async (idHorario) => {
    try {
      await api.delete(`/admin/profesionales/${profesionalHorarios.id}/horarios/${idHorario}`);
      setHorarios((prev) => prev.filter((h) => h.id !== idHorario));
    } catch (err) {
      setError(mensajeError(err, "No se pudo eliminar el horario"));
    }
  };

  return (
    <Box>
      <PageHeader
        titulo="Profesionales"
        subtitulo="Alta, especialidad, horarios de atención y estado de los obstetras"
        acciones={
          <Button variant="contained" startIcon={<AddIcon />} onClick={abrirNuevo}>
            Nuevo profesional
          </Button>
        }
      />

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Profesional</TableCell>
              <TableCell>Especialidad</TableCell>
              <TableCell>Experiencia</TableCell>
              <TableCell>Calificación</TableCell>
              <TableCell>Activo</TableCell>
              <TableCell align="right">Acciones</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {profesionales.map((p) => (
              <TableRow key={p.id} hover>
                <TableCell>
                  <Box sx={{ display: "flex", alignItems: "center", gap: 1.5 }}>
                    <Avatar src={p.fotoUrl || undefined}>{p.nombres.charAt(0)}</Avatar>
                    <Box>
                      <Typography variant="body2" fontWeight={600}>
                        {p.nombres} {p.apellidos}
                      </Typography>
                    </Box>
                  </Box>
                </TableCell>
                <TableCell>{p.especialidad || "-"}</TableCell>
                <TableCell>{p.añosExperiencia != null ? `${p.añosExperiencia} años` : "-"}</TableCell>
                <TableCell>
                  {p.calificacionPromedio != null ? (
                    <Rating value={p.calificacionPromedio} precision={0.5} readOnly size="small" />
                  ) : (
                    <Typography variant="caption" color="text.secondary">
                      Sin calificaciones
                    </Typography>
                  )}
                </TableCell>
                <TableCell>
                  <Switch checked={p.estado === "ACTIVO"} onChange={() => alternarEstado(p)} />
                </TableCell>
                <TableCell align="right">
                  <Tooltip title="Horarios">
                    <IconButton onClick={() => abrirHorarios(p)}>
                      <ScheduleIcon fontSize="small" />
                    </IconButton>
                  </Tooltip>
                  <Tooltip title="Editar">
                    <IconButton onClick={() => abrirEdicion(p)}>
                      <EditIcon fontSize="small" />
                    </IconButton>
                  </Tooltip>
                </TableCell>
              </TableRow>
            ))}
            {profesionales.length === 0 && (
              <TableRow>
                <TableCell colSpan={6} align="center">
                  Aún no hay profesionales registrados.
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </TableContainer>

      {/* Alta / edición de datos */}
      <Dialog open={dialogoAbierto} onClose={() => setDialogoAbierto(false)} maxWidth="sm" fullWidth>
        <DialogTitle>{editando ? "Editar profesional" : "Nuevo profesional"}</DialogTitle>
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
            label="Especialidad"
            value={form.especialidad}
            onChange={(e) => setForm({ ...form, especialidad: e.target.value })}
            fullWidth
          />
          <TextField
            label="Años de experiencia"
            type="number"
            value={form.añosExperiencia}
            onChange={(e) => setForm({ ...form, añosExperiencia: e.target.value })}
            fullWidth
          />
          <TextField
            label="Reseña / descripción breve"
            value={form.reseña}
            onChange={(e) => setForm({ ...form, reseña: e.target.value })}
            fullWidth
            multiline
            rows={3}
          />
          <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
            <Avatar src={form.fotoUrl || undefined} sx={{ width: 56, height: 56 }}>
              {form.nombres ? form.nombres.charAt(0) : "?"}
            </Avatar>
            <TextField
              label="Foto (ruta o URL)"
              value={form.fotoUrl}
              onChange={(e) => setForm({ ...form, fotoUrl: e.target.value })}
              fullWidth
              helperText='Ej: /profesionales/nombre-archivo.jpg (deja vacío para usar la inicial del nombre)'
            />
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDialogoAbierto(false)}>Cancelar</Button>
          <Button onClick={guardar} variant="contained" disabled={guardando || !form.nombres || !form.apellidos}>
            Guardar
          </Button>
        </DialogActions>
      </Dialog>

      {/* Gestionar horarios */}
      <Dialog open={horariosAbierto} onClose={() => setHorariosAbierto(false)} maxWidth="xs" fullWidth>
        <DialogTitle>
          Horarios de {profesionalHorarios?.nombres} {profesionalHorarios?.apellidos}
        </DialogTitle>
        <DialogContent>
          <List dense>
            {horarios.map((h) => (
              <ListItem
                key={h.id}
                secondaryAction={
                  <IconButton edge="end" onClick={() => eliminarHorario(h.id)} disabled={h.estado === "OCUPADO"}>
                    <DeleteIcon fontSize="small" />
                  </IconButton>
                }
              >
                <ListItemText
                  primary={`${h.fecha} · ${h.horaInicio} - ${h.horaFin}`}
                  secondary={<Chip size="small" label={h.estado} />}
                />
              </ListItem>
            ))}
            {horarios.length === 0 && (
              <Typography variant="body2" color="text.secondary" sx={{ p: 1 }}>
                Aún no hay horarios cargados.
              </Typography>
            )}
          </List>

          <Typography variant="subtitle2" sx={{ mt: 2 }}>
            Agregar horario
          </Typography>
          <Box sx={{ display: "flex", flexDirection: "column", gap: 1.5, mt: 1 }}>
            <TextField
              label="Fecha"
              type="date"
              value={nuevoHorario.fecha}
              onChange={(e) => setNuevoHorario({ ...nuevoHorario, fecha: e.target.value })}
              InputLabelProps={{ shrink: true }}
              fullWidth
            />
            <Box sx={{ display: "flex", gap: 1.5 }}>
              <TextField
                label="Hora inicio"
                type="time"
                value={nuevoHorario.horaInicio}
                onChange={(e) => setNuevoHorario({ ...nuevoHorario, horaInicio: e.target.value })}
                InputLabelProps={{ shrink: true }}
                fullWidth
              />
              <TextField
                label="Hora fin"
                type="time"
                value={nuevoHorario.horaFin}
                onChange={(e) => setNuevoHorario({ ...nuevoHorario, horaFin: e.target.value })}
                InputLabelProps={{ shrink: true }}
                fullWidth
              />
            </Box>
            <Button
              variant="outlined"
              onClick={agregarHorario}
              disabled={!nuevoHorario.fecha || !nuevoHorario.horaInicio || !nuevoHorario.horaFin}
            >
              Agregar
            </Button>
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setHorariosAbierto(false)}>Cerrar</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
