import { useEffect, useState } from "react";
import Alert from "@mui/material/Alert";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import Dialog from "@mui/material/Dialog";
import DialogActions from "@mui/material/DialogActions";
import DialogContent from "@mui/material/DialogContent";
import DialogTitle from "@mui/material/DialogTitle";
import Divider from "@mui/material/Divider";
import IconButton from "@mui/material/IconButton";
import MenuItem from "@mui/material/MenuItem";
import Paper from "@mui/material/Paper";
import Radio from "@mui/material/Radio";
import RadioGroup from "@mui/material/RadioGroup";
import FormControlLabel from "@mui/material/FormControlLabel";
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
import CancelIcon from "@mui/icons-material/Cancel";
import EventRepeatIcon from "@mui/icons-material/EventRepeat";
import PaymentIcon from "@mui/icons-material/Payment";

import api, { mensajeError } from "../api/client";
import PageHeader from "../components/PageHeader";
import EstadoChip from "../components/EstadoChip";

const CITA_VACIA = { idPaciente: "", idProducto: "", idProfesional: "", fechaHora: "", motivoCita: "" };
const PAGO_VACIO = { monto: "", metodoPago: "Tarjeta", referenciaPasarela: "", aprobado: true };
const REAGENDAR_VACIO = { aprobar: true, fechaHoraNueva: "", motivoRechazo: "" };

export default function CitasPage() {
  const [citas, setCitas] = useState([]);
  const [pacientes, setPacientes] = useState([]);
  const [productos, setProductos] = useState([]);
  const [profesionales, setProfesionales] = useState([]);
  const [error, setError] = useState("");

  const [nuevaCitaAbierta, setNuevaCitaAbierta] = useState(false);
  const [formCita, setFormCita] = useState(CITA_VACIA);

  const [pagoAbierto, setPagoAbierto] = useState(false);
  const [citaSeleccionada, setCitaSeleccionada] = useState(null);
  const [formPago, setFormPago] = useState(PAGO_VACIO);

  const [reagendarAbierto, setReagendarAbierto] = useState(false);
  const [formReagendar, setFormReagendar] = useState(REAGENDAR_VACIO);

  const [guardando, setGuardando] = useState(false);

  const cargar = async () => {
    try {
      const [{ data: citasData }, { data: pacientesData }, { data: productosData }, { data: profesionalesData }] =
        await Promise.all([
          api.get("/admin/citas"),
          api.get("/admin/pacientes"),
          api.get("/admin/productos"),
          api.get("/admin/profesionales"),
        ]);
      setCitas(citasData);
      setPacientes(pacientesData);
      setProductos(productosData);
      setProfesionales(profesionalesData);
    } catch (err) {
      setError(mensajeError(err, "No se pudieron cargar las citas"));
    }
  };

  useEffect(() => {
    cargar();
  }, []);

  const crearCita = async () => {
    setGuardando(true);
    setError("");
    try {
      await api.post("/admin/citas", {
        ...formCita,
        idPaciente: Number(formCita.idPaciente),
        idProducto: Number(formCita.idProducto),
        idProfesional: Number(formCita.idProfesional),
      });
      setNuevaCitaAbierta(false);
      setFormCita(CITA_VACIA);
      await cargar();
    } catch (err) {
      setError(mensajeError(err, "No se pudo agendar la cita"));
    } finally {
      setGuardando(false);
    }
  };

  const cancelarCita = async (cita) => {
    try {
      await api.patch(`/admin/citas/${cita.id}/cancelar`);
      await cargar();
    } catch (err) {
      setError(mensajeError(err, "No se pudo cancelar la cita"));
    }
  };

  const abrirPago = (cita) => {
    setCitaSeleccionada(cita);
    setFormPago({ ...PAGO_VACIO, monto: productos.find((p) => p.id === cita.idProducto)?.precio || "" });
    setPagoAbierto(true);
  };

  const registrarPago = async () => {
    setGuardando(true);
    setError("");
    try {
      await api.post(`/admin/citas/${citaSeleccionada.id}/pagos`, {
        ...formPago,
        monto: Number(formPago.monto),
      });
      setPagoAbierto(false);
      await cargar();
    } catch (err) {
      setError(mensajeError(err, "No se pudo registrar el pago"));
    } finally {
      setGuardando(false);
    }
  };

  const abrirReagendar = (cita) => {
    setCitaSeleccionada(cita);
    setFormReagendar(REAGENDAR_VACIO);
    setReagendarAbierto(true);
  };

  const confirmarReagendar = async () => {
    setGuardando(true);
    setError("");
    try {
      await api.post(`/admin/citas/${citaSeleccionada.id}/reagendar`, formReagendar);
      setReagendarAbierto(false);
      await cargar();
    } catch (err) {
      setError(mensajeError(err, "No se pudo procesar la reasignación"));
    } finally {
      setGuardando(false);
    }
  };

  return (
    <Box>
      <PageHeader
        titulo="Citas"
        subtitulo="Agendar, confirmar pago y reagendar citas"
        acciones={
          <Button variant="contained" startIcon={<AddIcon />} onClick={() => setNuevaCitaAbierta(true)}>
            Agendar cita
          </Button>
        }
      />

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Paciente</TableCell>
              <TableCell>Producto/Servicio</TableCell>
              <TableCell>Profesional</TableCell>
              <TableCell>Fecha y hora</TableCell>
              <TableCell>Motivo</TableCell>
              <TableCell>Estado</TableCell>
              <TableCell align="right">Acciones</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {citas.map((cita) => (
              <TableRow key={cita.id} hover>
                <TableCell>{cita.nombrePaciente}</TableCell>
                <TableCell>{cita.nombreProducto}</TableCell>
                <TableCell>{cita.nombreProfesional}</TableCell>
                <TableCell>{new Date(cita.fechaHora).toLocaleString()}</TableCell>
                <TableCell>{cita.motivoCita || "-"}</TableCell>
                <TableCell>
                  <EstadoChip estado={cita.estado} />
                </TableCell>
                <TableCell align="right">
                  <Tooltip title="Registrar pago">
                    <IconButton onClick={() => abrirPago(cita)}>
                      <PaymentIcon fontSize="small" />
                    </IconButton>
                  </Tooltip>
                  <Tooltip title="Reagendar">
                    <IconButton onClick={() => abrirReagendar(cita)}>
                      <EventRepeatIcon fontSize="small" />
                    </IconButton>
                  </Tooltip>
                  <Tooltip title="Cancelar">
                    <IconButton onClick={() => cancelarCita(cita)} color="error">
                      <CancelIcon fontSize="small" />
                    </IconButton>
                  </Tooltip>
                </TableCell>
              </TableRow>
            ))}
            {citas.length === 0 && (
              <TableRow>
                <TableCell colSpan={7} align="center">
                  Aún no hay citas registradas.
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </TableContainer>

      {/* Agendar cita */}
      <Dialog open={nuevaCitaAbierta} onClose={() => setNuevaCitaAbierta(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Agendar cita</DialogTitle>
        <DialogContent sx={{ display: "flex", flexDirection: "column", gap: 2, mt: 1 }}>
          <TextField
            select
            label="Paciente"
            value={formCita.idPaciente}
            onChange={(e) => setFormCita({ ...formCita, idPaciente: e.target.value })}
            fullWidth
            required
          >
            {pacientes.map((p) => (
              <MenuItem key={p.id} value={p.id}>
                {p.nombres} {p.apellidos} ({p.numeroDocumento})
              </MenuItem>
            ))}
          </TextField>
          <TextField
            select
            label="Producto / servicio"
            value={formCita.idProducto}
            onChange={(e) => setFormCita({ ...formCita, idProducto: e.target.value })}
            fullWidth
            required
          >
            {productos.map((p) => (
              <MenuItem key={p.id} value={p.id}>
                {p.nombre} - S/ {Number(p.precio).toFixed(2)}
              </MenuItem>
            ))}
          </TextField>
          <TextField
            select
            label="Profesional"
            value={formCita.idProfesional}
            onChange={(e) => setFormCita({ ...formCita, idProfesional: e.target.value })}
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
            label="Fecha y hora"
            type="datetime-local"
            value={formCita.fechaHora}
            onChange={(e) => setFormCita({ ...formCita, fechaHora: e.target.value })}
            InputLabelProps={{ shrink: true }}
            fullWidth
            required
          />
          <TextField
            label="Motivo de la cita (max. 300 caracteres)"
            value={formCita.motivoCita}
            onChange={(e) => setFormCita({ ...formCita, motivoCita: e.target.value })}
            fullWidth
            multiline
            rows={2}
            inputProps={{ maxLength: 300 }}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setNuevaCitaAbierta(false)}>Cancelar</Button>
          <Button
            onClick={crearCita}
            variant="contained"
            disabled={guardando || !formCita.idPaciente || !formCita.idProducto || !formCita.idProfesional || !formCita.fechaHora}
          >
            Agendar
          </Button>
        </DialogActions>
      </Dialog>

      {/* Registrar pago */}
      <Dialog open={pagoAbierto} onClose={() => setPagoAbierto(false)} maxWidth="xs" fullWidth>
        <DialogTitle>Registrar pago</DialogTitle>
        <DialogContent sx={{ display: "flex", flexDirection: "column", gap: 2, mt: 1 }}>
          <TextField
            label="Monto (S/)"
            type="number"
            value={formPago.monto}
            onChange={(e) => setFormPago({ ...formPago, monto: e.target.value })}
            fullWidth
          />
          <TextField
            label="Método de pago"
            value={formPago.metodoPago}
            onChange={(e) => setFormPago({ ...formPago, metodoPago: e.target.value })}
            fullWidth
          />
          <TextField
            label="Referencia de la pasarela"
            value={formPago.referenciaPasarela}
            onChange={(e) => setFormPago({ ...formPago, referenciaPasarela: e.target.value })}
            fullWidth
          />
          <RadioGroup
            row
            value={formPago.aprobado ? "aprobado" : "rechazado"}
            onChange={(e) => setFormPago({ ...formPago, aprobado: e.target.value === "aprobado" })}
          >
            <FormControlLabel value="aprobado" control={<Radio />} label="Aprobar pago" />
            <FormControlLabel value="rechazado" control={<Radio />} label="Rechazar pago" />
          </RadioGroup>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setPagoAbierto(false)}>Cancelar</Button>
          <Button onClick={registrarPago} variant="contained" disabled={guardando || !formPago.monto}>
            Registrar
          </Button>
        </DialogActions>
      </Dialog>

      {/* Reagendar cita */}
      <Dialog open={reagendarAbierto} onClose={() => setReagendarAbierto(false)} maxWidth="xs" fullWidth>
        <DialogTitle>Reagendar cita</DialogTitle>
        <DialogContent sx={{ display: "flex", flexDirection: "column", gap: 2, mt: 1 }}>
          <Typography variant="body2" color="text.secondary">
            Fecha actual: {citaSeleccionada && new Date(citaSeleccionada.fechaHora).toLocaleString()}
          </Typography>
          <RadioGroup
            row
            value={formReagendar.aprobar ? "aprobar" : "rechazar"}
            onChange={(e) => setFormReagendar({ ...formReagendar, aprobar: e.target.value === "aprobar" })}
          >
            <FormControlLabel value="aprobar" control={<Radio />} label="Aprobar" />
            <FormControlLabel value="rechazar" control={<Radio />} label="Rechazar" />
          </RadioGroup>
          <Divider />
          {formReagendar.aprobar ? (
            <TextField
              label="Nueva fecha y hora"
              type="datetime-local"
              value={formReagendar.fechaHoraNueva}
              onChange={(e) => setFormReagendar({ ...formReagendar, fechaHoraNueva: e.target.value })}
              InputLabelProps={{ shrink: true }}
              fullWidth
            />
          ) : (
            <TextField
              label="Motivo del rechazo"
              value={formReagendar.motivoRechazo}
              onChange={(e) => setFormReagendar({ ...formReagendar, motivoRechazo: e.target.value })}
              fullWidth
              multiline
              rows={2}
            />
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setReagendarAbierto(false)}>Cancelar</Button>
          <Button onClick={confirmarReagendar} variant="contained" disabled={guardando}>
            Confirmar
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
