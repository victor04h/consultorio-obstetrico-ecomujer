import { useEffect, useState } from "react";
import Alert from "@mui/material/Alert";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import Dialog from "@mui/material/Dialog";
import DialogActions from "@mui/material/DialogActions";
import DialogContent from "@mui/material/DialogContent";
import DialogTitle from "@mui/material/DialogTitle";
import IconButton from "@mui/material/IconButton";
import InputAdornment from "@mui/material/InputAdornment";
import MenuItem from "@mui/material/MenuItem";
import Paper from "@mui/material/Paper";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import TextField from "@mui/material/TextField";
import Tooltip from "@mui/material/Tooltip";
import AddIcon from "@mui/icons-material/Add";
import EditIcon from "@mui/icons-material/Edit";
import SearchIcon from "@mui/icons-material/Search";

import api, { mensajeError } from "../api/client";
import PageHeader from "../components/PageHeader";

const VACIO = { idPaciente: "", idCita: "", idProfesional: "", diagnostico: "", tratamiento: "", notas: "" };

export default function HistorialClinicoPage() {
  const [pacientes, setPacientes] = useState([]);
  const [profesionales, setProfesionales] = useState([]);
  const [pacienteId, setPacienteId] = useState("");
  const [historial, setHistorial] = useState([]);
  const [error, setError] = useState("");

  const [dialogoAbierto, setDialogoAbierto] = useState(false);
  const [editando, setEditando] = useState(null);
  const [form, setForm] = useState(VACIO);
  const [guardando, setGuardando] = useState(false);

  useEffect(() => {
    Promise.all([api.get("/admin/pacientes"), api.get("/admin/profesionales")])
      .then(([{ data: pac }, { data: prof }]) => {
        setPacientes(pac);
        setProfesionales(prof);
      })
      .catch((err) => setError(mensajeError(err, "No se pudieron cargar los datos base")));
  }, []);

  const buscar = async (idPaciente) => {
    if (!idPaciente) {
      setHistorial([]);
      return;
    }
    try {
      const { data } = await api.get("/admin/historial-clinico", { params: { idPaciente } });
      setHistorial(data);
    } catch (err) {
      setError(mensajeError(err, "No se pudo cargar el historial clínico"));
    }
  };

  const abrirNuevo = () => {
    setEditando(null);
    setForm({ ...VACIO, idPaciente: pacienteId });
    setDialogoAbierto(true);
  };

  const abrirEdicion = (entrada) => {
    setEditando(entrada);
    setForm({
      idPaciente: entrada.idPaciente,
      idCita: entrada.idCita || "",
      idProfesional: entrada.idProfesional,
      diagnostico: entrada.diagnostico || "",
      tratamiento: entrada.tratamiento || "",
      notas: entrada.notas || "",
    });
    setDialogoAbierto(true);
  };

  const guardar = async () => {
    setGuardando(true);
    setError("");
    try {
      const payload = {
        ...form,
        idPaciente: Number(form.idPaciente),
        idCita: form.idCita ? Number(form.idCita) : null,
        idProfesional: Number(form.idProfesional),
      };
      if (editando) {
        await api.put(`/admin/historial-clinico/${editando.id}`, payload);
      } else {
        await api.post("/admin/historial-clinico", payload);
      }
      setDialogoAbierto(false);
      await buscar(pacienteId);
    } catch (err) {
      setError(mensajeError(err, "No se pudo guardar la entrada de historial clínico"));
    } finally {
      setGuardando(false);
    }
  };

  return (
    <Box>
      <PageHeader
        titulo="Historial clínico"
        subtitulo="Busca un paciente para ver y actualizar su historial"
        acciones={
          <Button variant="contained" startIcon={<AddIcon />} onClick={abrirNuevo} disabled={!pacienteId}>
            Nueva entrada
          </Button>
        }
      />

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      <TextField
        select
        label="Paciente"
        value={pacienteId}
        onChange={(e) => {
          setPacienteId(e.target.value);
          buscar(e.target.value);
        }}
        sx={{ width: { xs: "100%", sm: 360 }, mb: 2 }}
        InputProps={{
          startAdornment: (
            <InputAdornment position="start">
              <SearchIcon fontSize="small" />
            </InputAdornment>
          ),
        }}
      >
        {pacientes.map((p) => (
          <MenuItem key={p.id} value={p.id}>
            {p.nombres} {p.apellidos} ({p.numeroDocumento})
          </MenuItem>
        ))}
      </TextField>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Fecha</TableCell>
              <TableCell>Profesional</TableCell>
              <TableCell>Diagnóstico</TableCell>
              <TableCell>Tratamiento</TableCell>
              <TableCell align="right">Acciones</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {historial.map((h) => (
              <TableRow key={h.id} hover>
                <TableCell>{new Date(h.fechaRegistro).toLocaleDateString()}</TableCell>
                <TableCell>{h.nombreProfesional}</TableCell>
                <TableCell>{h.diagnostico}</TableCell>
                <TableCell>{h.tratamiento}</TableCell>
                <TableCell align="right">
                  <Tooltip title="Editar">
                    <IconButton onClick={() => abrirEdicion(h)}>
                      <EditIcon fontSize="small" />
                    </IconButton>
                  </Tooltip>
                </TableCell>
              </TableRow>
            ))}
            {historial.length === 0 && (
              <TableRow>
                <TableCell colSpan={5} align="center">
                  {pacienteId
                    ? "Este paciente aún no tiene entradas de historial clínico."
                    : "Selecciona un paciente para ver su historial."}
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </TableContainer>

      <Dialog open={dialogoAbierto} onClose={() => setDialogoAbierto(false)} maxWidth="sm" fullWidth>
        <DialogTitle>{editando ? "Editar entrada" : "Nueva entrada de historial clínico"}</DialogTitle>
        <DialogContent sx={{ display: "flex", flexDirection: "column", gap: 2, mt: 1 }}>
          <TextField
            select
            label="Profesional"
            value={form.idProfesional}
            onChange={(e) => setForm({ ...form, idProfesional: e.target.value })}
            fullWidth
            required
          >
            {profesionales.map((p) => (
              <MenuItem key={p.id} value={p.id}>
                {p.nombres} {p.apellidos}
              </MenuItem>
            ))}
          </TextField>
          <TextField
            label="Diagnóstico"
            value={form.diagnostico}
            onChange={(e) => setForm({ ...form, diagnostico: e.target.value })}
            fullWidth
            multiline
            rows={2}
          />
          <TextField
            label="Tratamiento"
            value={form.tratamiento}
            onChange={(e) => setForm({ ...form, tratamiento: e.target.value })}
            fullWidth
            multiline
            rows={2}
          />
          <TextField
            label="Notas"
            value={form.notas}
            onChange={(e) => setForm({ ...form, notas: e.target.value })}
            fullWidth
            multiline
            rows={2}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDialogoAbierto(false)}>Cancelar</Button>
          <Button onClick={guardar} variant="contained" disabled={guardando || !form.idProfesional}>
            Guardar
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
