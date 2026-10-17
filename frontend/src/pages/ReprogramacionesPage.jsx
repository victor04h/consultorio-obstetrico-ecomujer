import { useEffect, useState } from "react";
import Alert from "@mui/material/Alert";
import Box from "@mui/material/Box";
import Paper from "@mui/material/Paper";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";

import api, { mensajeError } from "../api/client";
import PageHeader from "../components/PageHeader";
import EstadoChip from "../components/EstadoChip";

/**
 * Historial de "Reprogramaciones" (Reagendar Cita del BPM): cada vez que se
 * aprueba o rechaza una solicitud de reagendamiento desde Citas, queda un
 * registro aqui. Es una pantalla de solo lectura; la accion de reagendar en
 * si se hace desde Citas.
 */
export default function ReprogramacionesPage() {
  const [reprogramaciones, setReprogramaciones] = useState([]);
  const [error, setError] = useState("");
  const [cargando, setCargando] = useState(true);

  useEffect(() => {
    (async () => {
      try {
        const { data } = await api.get("/admin/citas/reasignaciones");
        setReprogramaciones(data);
      } catch (err) {
        setError(mensajeError(err, "No se pudieron cargar las reprogramaciones"));
      } finally {
        setCargando(false);
      }
    })();
  }, []);

  return (
    <Box>
      <PageHeader
        titulo="Reprogramaciones"
        subtitulo="Historial de solicitudes de reagendamiento de citas (aprobadas y rechazadas)"
      />

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Paciente</TableCell>
              <TableCell>Profesional</TableCell>
              <TableCell>Producto/Servicio</TableCell>
              <TableCell>Fecha anterior</TableCell>
              <TableCell>Fecha nueva</TableCell>
              <TableCell>Resultado</TableCell>
              <TableCell>Motivo de rechazo</TableCell>
              <TableCell>Solicitado el</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {reprogramaciones.map((r) => (
              <TableRow key={r.id} hover>
                <TableCell>{r.nombrePaciente}</TableCell>
                <TableCell>{r.nombreProfesional}</TableCell>
                <TableCell>{r.nombreProducto}</TableCell>
                <TableCell>{new Date(r.fechaHoraAnterior).toLocaleString()}</TableCell>
                <TableCell>{r.fechaHoraNueva ? new Date(r.fechaHoraNueva).toLocaleString() : "-"}</TableCell>
                <TableCell>
                  <EstadoChip estado={r.resultado} />
                </TableCell>
                <TableCell>{r.motivoRechazo || "-"}</TableCell>
                <TableCell>{new Date(r.fechaSolicitud).toLocaleString()}</TableCell>
              </TableRow>
            ))}
            {!cargando && reprogramaciones.length === 0 && (
              <TableRow>
                <TableCell colSpan={8} align="center">
                  Aún no hay reprogramaciones registradas.
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </TableContainer>
    </Box>
  );
}
