import { useEffect, useState } from "react";
import Alert from "@mui/material/Alert";
import Box from "@mui/material/Box";
import Chip from "@mui/material/Chip";
import Dialog from "@mui/material/Dialog";
import DialogContent from "@mui/material/DialogContent";
import DialogTitle from "@mui/material/DialogTitle";
import IconButton from "@mui/material/IconButton";
import InputAdornment from "@mui/material/InputAdornment";
import List from "@mui/material/List";
import ListItem from "@mui/material/ListItem";
import ListItemText from "@mui/material/ListItemText";
import Paper from "@mui/material/Paper";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import TextField from "@mui/material/TextField";
import Tooltip from "@mui/material/Tooltip";
import Typography from "@mui/material/Typography";
import SearchIcon from "@mui/icons-material/Search";
import VisibilityIcon from "@mui/icons-material/Visibility";

import api, { mensajeError } from "../api/client";
import PageHeader from "../components/PageHeader";
import EstadoChip from "../components/EstadoChip";

export default function PacientesPage() {
  const [pacientes, setPacientes] = useState([]);
  const [busqueda, setBusqueda] = useState("");
  const [error, setError] = useState("");

  const [detalleAbierto, setDetalleAbierto] = useState(false);
  const [pacienteSeleccionado, setPacienteSeleccionado] = useState(null);
  const [citas, setCitas] = useState([]);
  const [historial, setHistorial] = useState([]);

  const cargar = async (texto) => {
    try {
      const { data } = await api.get("/admin/pacientes", { params: texto ? { busqueda: texto } : {} });
      setPacientes(data);
    } catch (err) {
      setError(mensajeError(err, "No se pudieron cargar los pacientes"));
    }
  };

  useEffect(() => {
    cargar("");
  }, []);

  const buscar = (e) => {
    e.preventDefault();
    cargar(busqueda);
  };

  const verDetalle = async (paciente) => {
    setPacienteSeleccionado(paciente);
    setDetalleAbierto(true);
    try {
      const [{ data: citasData }, { data: historialData }] = await Promise.all([
        api.get("/admin/citas", { params: { idPaciente: paciente.id } }),
        api.get("/admin/historial-clinico", { params: { idPaciente: paciente.id } }),
      ]);
      setCitas(citasData);
      setHistorial(historialData);
    } catch (err) {
      setError(mensajeError(err, "No se pudo cargar el detalle del paciente"));
    }
  };

  return (
    <Box>
      <PageHeader titulo="Pacientes" subtitulo="Pacientes registrados desde el sitio público (proceso de Registro)" />

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      <Box component="form" onSubmit={buscar} sx={{ mb: 2 }}>
        <TextField
          placeholder="Buscar por nombres o apellidos..."
          value={busqueda}
          onChange={(e) => setBusqueda(e.target.value)}
          size="small"
          sx={{ width: { xs: "100%", sm: 360 } }}
          InputProps={{
            endAdornment: (
              <InputAdornment position="end">
                <IconButton type="submit">
                  <SearchIcon />
                </IconButton>
              </InputAdornment>
            ),
          }}
        />
      </Box>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Paciente</TableCell>
              <TableCell>Documento</TableCell>
              <TableCell>Correo</TableCell>
              <TableCell>Menor de edad</TableCell>
              <TableCell align="right">Acciones</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {pacientes.map((p) => (
              <TableRow key={p.id} hover>
                <TableCell>
                  {p.nombres} {p.apellidos}
                </TableCell>
                <TableCell>{p.numeroDocumento}</TableCell>
                <TableCell>{p.correo}</TableCell>
                <TableCell>
                  {p.esMenorEdad ? <Chip size="small" color="warning" label={`Apoderado: ${p.nombreApoderado || "-"}`} /> : "No"}
                </TableCell>
                <TableCell align="right">
                  <Tooltip title="Ver detalle">
                    <IconButton onClick={() => verDetalle(p)}>
                      <VisibilityIcon fontSize="small" />
                    </IconButton>
                  </Tooltip>
                </TableCell>
              </TableRow>
            ))}
            {pacientes.length === 0 && (
              <TableRow>
                <TableCell colSpan={5} align="center">
                  No se encontraron pacientes.
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </TableContainer>

      <Dialog open={detalleAbierto} onClose={() => setDetalleAbierto(false)} maxWidth="sm" fullWidth>
        <DialogTitle>
          {pacienteSeleccionado?.nombres} {pacienteSeleccionado?.apellidos}
        </DialogTitle>
        <DialogContent>
          <Typography variant="subtitle2" sx={{ mt: 1 }}>
            Citas
          </Typography>
          <List dense>
            {citas.map((c) => (
              <ListItem key={c.id}>
                <ListItemText
                  primary={`${c.nombreProducto} con ${c.nombreProfesional}`}
                  secondary={new Date(c.fechaHora).toLocaleString()}
                />
                <EstadoChip estado={c.estado} />
              </ListItem>
            ))}
            {citas.length === 0 && (
              <Typography variant="body2" color="text.secondary" sx={{ p: 1 }}>
                Sin citas registradas.
              </Typography>
            )}
          </List>

          <Typography variant="subtitle2" sx={{ mt: 2 }}>
            Historial clinico
          </Typography>
          <List dense>
            {historial.map((h) => (
              <ListItem key={h.id} sx={{ display: "block" }}>
                <Typography variant="body2" fontWeight={600}>
                  {new Date(h.fechaRegistro).toLocaleDateString()} · {h.nombreProfesional}
                </Typography>
                <Typography variant="body2">{h.diagnostico}</Typography>
              </ListItem>
            ))}
            {historial.length === 0 && (
              <Typography variant="body2" color="text.secondary" sx={{ p: 1 }}>
                Sin entradas de historial clinico.
              </Typography>
            )}
          </List>
        </DialogContent>
      </Dialog>
    </Box>
  );
}
