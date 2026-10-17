import { useEffect, useState } from "react";
import Alert from "@mui/material/Alert";
import Box from "@mui/material/Box";
import MenuItem from "@mui/material/MenuItem";
import Paper from "@mui/material/Paper";
import Rating from "@mui/material/Rating";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import TextField from "@mui/material/TextField";

import api, { mensajeError } from "../api/client";
import PageHeader from "../components/PageHeader";

export default function CalificacionesPage() {
  const [calificaciones, setCalificaciones] = useState([]);
  const [profesionales, setProfesionales] = useState([]);
  const [idProfesional, setIdProfesional] = useState("");
  const [error, setError] = useState("");

  const cargar = async (filtro) => {
    try {
      const { data } = await api.get("/admin/calificaciones", {
        params: filtro ? { idProfesional: filtro } : {},
      });
      setCalificaciones(data);
    } catch (err) {
      setError(mensajeError(err, "No se pudieron cargar las calificaciones"));
    }
  };

  useEffect(() => {
    api
      .get("/admin/profesionales")
      .then(({ data }) => setProfesionales(data))
      .catch(() => {});
    cargar("");
  }, []);

  return (
    <Box>
      <PageHeader
        titulo="Calificaciones"
        subtitulo="Opiniones que los pacientes dejan sobre los profesionales (solo lectura)"
      />

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      <TextField
        select
        label="Filtrar por profesional"
        value={idProfesional}
        onChange={(e) => {
          setIdProfesional(e.target.value);
          cargar(e.target.value);
        }}
        sx={{ width: { xs: "100%", sm: 320 }, mb: 2 }}
      >
        <MenuItem value="">Todos</MenuItem>
        {profesionales.map((p) => (
          <MenuItem key={p.id} value={p.id}>
            {p.nombres} {p.apellidos}
          </MenuItem>
        ))}
      </TextField>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Fecha</TableCell>
              <TableCell>Profesional</TableCell>
              <TableCell>Paciente</TableCell>
              <TableCell>Puntuación</TableCell>
              <TableCell>Comentario</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {calificaciones.map((c) => (
              <TableRow key={c.id} hover>
                <TableCell>{new Date(c.fechaCreacion).toLocaleDateString()}</TableCell>
                <TableCell>{c.nombreProfesional}</TableCell>
                <TableCell>{c.nombrePaciente}</TableCell>
                <TableCell>
                  <Rating value={c.puntuacion} readOnly size="small" />
                </TableCell>
                <TableCell>{c.comentario}</TableCell>
              </TableRow>
            ))}
            {calificaciones.length === 0 && (
              <TableRow>
                <TableCell colSpan={5} align="center">
                  Aún no hay calificaciones registradas.
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </TableContainer>
    </Box>
  );
}
