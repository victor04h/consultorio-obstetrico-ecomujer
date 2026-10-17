import { useEffect, useState } from "react";
import Alert from "@mui/material/Alert";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import Checkbox from "@mui/material/Checkbox";
import Dialog from "@mui/material/Dialog";
import DialogActions from "@mui/material/DialogActions";
import DialogContent from "@mui/material/DialogContent";
import DialogTitle from "@mui/material/DialogTitle";
import FormControlLabel from "@mui/material/FormControlLabel";
import FormGroup from "@mui/material/FormGroup";
import IconButton from "@mui/material/IconButton";
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
import Tooltip from "@mui/material/Tooltip";
import Typography from "@mui/material/Typography";
import AddIcon from "@mui/icons-material/Add";
import EditIcon from "@mui/icons-material/Edit";

import api, { mensajeError } from "../api/client";
import PageHeader from "../components/PageHeader";
import EstadoChip from "../components/EstadoChip";

const VACIO = {
  idCategoria: "",
  nombre: "",
  descripcionCorta: "",
  precio: "",
  stock: "",
  disponible: true,
  profesionalIds: [],
};

export default function ProductosPage() {
  const [productos, setProductos] = useState([]);
  const [categorias, setCategorias] = useState([]);
  const [profesionales, setProfesionales] = useState([]);
  const [error, setError] = useState("");
  const [dialogoAbierto, setDialogoAbierto] = useState(false);
  const [editando, setEditando] = useState(null);
  const [form, setForm] = useState(VACIO);
  const [guardando, setGuardando] = useState(false);

  const cargar = async () => {
    try {
      const [{ data: productosData }, { data: categoriasData }, { data: profesionalesData }] = await Promise.all([
        api.get("/admin/productos"),
        api.get("/admin/categorias"),
        api.get("/admin/profesionales"),
      ]);
      setProductos(productosData);
      setCategorias(categoriasData);
      setProfesionales(profesionalesData);
    } catch (err) {
      setError(mensajeError(err, "No se pudieron cargar los productos"));
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

  const abrirEdicion = (producto) => {
    setEditando(producto);
    setForm({
      idCategoria: producto.idCategoria,
      nombre: producto.nombre,
      descripcionCorta: producto.descripcionCorta || "",
      precio: producto.precio,
      stock: producto.stock ?? "",
      disponible: producto.disponible,
      profesionalIds: profesionales.filter((p) => producto.profesionales?.includes(`${p.nombres} ${p.apellidos}`)).map((p) => p.id),
    });
    setDialogoAbierto(true);
  };

  const alternarProfesional = (id) => {
    setForm((prev) => ({
      ...prev,
      profesionalIds: prev.profesionalIds.includes(id)
        ? prev.profesionalIds.filter((pid) => pid !== id)
        : [...prev.profesionalIds, id],
    }));
  };

  const guardar = async () => {
    setGuardando(true);
    setError("");
    try {
      const payload = {
        ...form,
        idCategoria: Number(form.idCategoria),
        precio: Number(form.precio),
        stock: form.stock === "" ? null : Number(form.stock),
      };
      if (editando) {
        await api.put(`/admin/productos/${editando.id}`, payload);
      } else {
        await api.post("/admin/productos", payload);
      }
      setDialogoAbierto(false);
      await cargar();
    } catch (err) {
      setError(mensajeError(err, "No se pudo guardar el producto"));
    } finally {
      setGuardando(false);
    }
  };

  const alternarEstado = async (producto) => {
    const nuevoEstado = producto.estado === "ACTIVO" ? "INACTIVO" : "ACTIVO";
    try {
      await api.patch(`/admin/productos/${producto.id}/estado`, { estado: nuevoEstado });
      await cargar();
    } catch (err) {
      setError(mensajeError(err, "No se pudo cambiar el estado"));
    }
  };

  return (
    <Box>
      <PageHeader
        titulo="Productos y servicios"
        subtitulo="Catálogo de ecografías, consultas y medicamentos"
        acciones={
          <Button variant="contained" startIcon={<AddIcon />} onClick={abrirNuevo}>
            Nuevo producto
          </Button>
        }
      />

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Nombre</TableCell>
              <TableCell>Categoría</TableCell>
              <TableCell>Precio</TableCell>
              <TableCell>Stock</TableCell>
              <TableCell>Estado</TableCell>
              <TableCell align="right">Acciones</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {productos.map((producto) => (
              <TableRow key={producto.id} hover>
                <TableCell>
                  <Typography variant="body2" fontWeight={600}>
                    {producto.nombre}
                  </Typography>
                  <Typography variant="caption" color="text.secondary">
                    {producto.descripcionCorta}
                  </Typography>
                </TableCell>
                <TableCell>{producto.nombreCategoria}</TableCell>
                <TableCell>S/ {Number(producto.precio).toFixed(2)}</TableCell>
                <TableCell>{producto.stock ?? "-"}</TableCell>
                <TableCell>
                  <EstadoChip estado={producto.estado} />
                </TableCell>
                <TableCell align="right">
                  <Switch
                    size="small"
                    checked={producto.estado === "ACTIVO"}
                    onChange={() => alternarEstado(producto)}
                  />
                  <Tooltip title="Editar">
                    <IconButton onClick={() => abrirEdicion(producto)}>
                      <EditIcon fontSize="small" />
                    </IconButton>
                  </Tooltip>
                </TableCell>
              </TableRow>
            ))}
            {productos.length === 0 && (
              <TableRow>
                <TableCell colSpan={6} align="center">
                  Aún no hay productos registrados.
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </TableContainer>

      <Dialog open={dialogoAbierto} onClose={() => setDialogoAbierto(false)} maxWidth="sm" fullWidth>
        <DialogTitle>{editando ? "Editar producto" : "Nuevo producto"}</DialogTitle>
        <DialogContent sx={{ display: "flex", flexDirection: "column", gap: 2, mt: 1 }}>
          <TextField
            select
            label="Categoría"
            value={form.idCategoria}
            onChange={(e) => setForm({ ...form, idCategoria: e.target.value })}
            required
            fullWidth
          >
            {categorias.map((c) => (
              <MenuItem key={c.id} value={c.id}>
                {c.nombre}
              </MenuItem>
            ))}
          </TextField>
          <TextField
            label="Nombre"
            value={form.nombre}
            onChange={(e) => setForm({ ...form, nombre: e.target.value })}
            fullWidth
            required
          />
          <TextField
            label="Descripción corta"
            value={form.descripcionCorta}
            onChange={(e) => setForm({ ...form, descripcionCorta: e.target.value })}
            fullWidth
            multiline
            rows={2}
          />
          <Box sx={{ display: "flex", gap: 2 }}>
            <TextField
              label="Precio (S/)"
              type="number"
              value={form.precio}
              onChange={(e) => setForm({ ...form, precio: e.target.value })}
              fullWidth
              required
            />
            <TextField
              label="Stock (opcional)"
              type="number"
              value={form.stock}
              onChange={(e) => setForm({ ...form, stock: e.target.value })}
              fullWidth
            />
          </Box>
          <FormControlLabel
            control={
              <Checkbox
                checked={form.disponible}
                onChange={(e) => setForm({ ...form, disponible: e.target.checked })}
              />
            }
            label="Disponible para agendar"
          />
          <Typography variant="subtitle2">Profesionales que lo ofrecen</Typography>
          <FormGroup>
            {profesionales.map((p) => (
              <FormControlLabel
                key={p.id}
                control={
                  <Checkbox checked={form.profesionalIds.includes(p.id)} onChange={() => alternarProfesional(p.id)} />
                }
                label={`${p.nombres} ${p.apellidos}`}
              />
            ))}
            {profesionales.length === 0 && (
              <Typography variant="body2" color="text.secondary">
                Aún no hay profesionales registrados.
              </Typography>
            )}
          </FormGroup>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDialogoAbierto(false)}>Cancelar</Button>
          <Button onClick={guardar} variant="contained" disabled={guardando || !form.nombre || !form.idCategoria}>
            Guardar
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
