import { useEffect, useState } from "react";
import Alert from "@mui/material/Alert";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import Dialog from "@mui/material/Dialog";
import DialogActions from "@mui/material/DialogActions";
import DialogContent from "@mui/material/DialogContent";
import DialogTitle from "@mui/material/DialogTitle";
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
import AddIcon from "@mui/icons-material/Add";
import EditIcon from "@mui/icons-material/Edit";

import api, { mensajeError } from "../api/client";
import PageHeader from "../components/PageHeader";

const VACIO = { nombre: "", descripcion: "" };

export default function CategoriasPage() {
  const [categorias, setCategorias] = useState([]);
  const [error, setError] = useState("");
  const [dialogoAbierto, setDialogoAbierto] = useState(false);
  const [editando, setEditando] = useState(null);
  const [form, setForm] = useState(VACIO);
  const [guardando, setGuardando] = useState(false);

  const cargar = async () => {
    try {
      const { data } = await api.get("/admin/categorias");
      setCategorias(data);
    } catch (err) {
      setError(mensajeError(err, "No se pudieron cargar las categorías"));
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

  const abrirEdicion = (categoria) => {
    setEditando(categoria);
    setForm({ nombre: categoria.nombre, descripcion: categoria.descripcion || "" });
    setDialogoAbierto(true);
  };

  const guardar = async () => {
    setGuardando(true);
    setError("");
    try {
      if (editando) {
        await api.put(`/admin/categorias/${editando.id}`, form);
      } else {
        await api.post("/admin/categorias", form);
      }
      setDialogoAbierto(false);
      await cargar();
    } catch (err) {
      setError(mensajeError(err, "No se pudo guardar la categoría"));
    } finally {
      setGuardando(false);
    }
  };

  const alternarEstado = async (categoria) => {
    const nuevoEstado = categoria.estado === "ACTIVO" ? "INACTIVO" : "ACTIVO";
    try {
      await api.patch(`/admin/categorias/${categoria.id}/estado`, { estado: nuevoEstado });
      await cargar();
    } catch (err) {
      setError(mensajeError(err, "No se pudo cambiar el estado"));
    }
  };

  return (
    <Box>
      <PageHeader
        titulo="Categorías"
        subtitulo="Agrupan los productos y servicios del catálogo (ecografías, medicamentos, etc.)"
        acciones={
          <Button variant="contained" startIcon={<AddIcon />} onClick={abrirNuevo}>
            Nueva categoría
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
              <TableCell>Activa</TableCell>
              <TableCell align="right">Acciones</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {categorias.map((categoria) => (
              <TableRow key={categoria.id} hover>
                <TableCell>{categoria.nombre}</TableCell>
                <TableCell>{categoria.descripcion}</TableCell>
                <TableCell>
                  <Switch checked={categoria.estado === "ACTIVO"} onChange={() => alternarEstado(categoria)} />
                </TableCell>
                <TableCell align="right">
                  <Tooltip title="Editar">
                    <IconButton onClick={() => abrirEdicion(categoria)}>
                      <EditIcon fontSize="small" />
                    </IconButton>
                  </Tooltip>
                </TableCell>
              </TableRow>
            ))}
            {categorias.length === 0 && (
              <TableRow>
                <TableCell colSpan={4} align="center">
                  Aún no hay categorías registradas.
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </TableContainer>

      <Dialog open={dialogoAbierto} onClose={() => setDialogoAbierto(false)} maxWidth="sm" fullWidth>
        <DialogTitle>{editando ? "Editar categoría" : "Nueva categoría"}</DialogTitle>
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
