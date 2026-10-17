import Button from "@mui/material/Button";
import Dialog from "@mui/material/Dialog";
import DialogActions from "@mui/material/DialogActions";
import DialogContent from "@mui/material/DialogContent";
import DialogContentText from "@mui/material/DialogContentText";
import DialogTitle from "@mui/material/DialogTitle";

export default function ConfirmDialog({ open, titulo, mensaje, onConfirmar, onCancelar, cargando }) {
  return (
    <Dialog open={open} onClose={onCancelar}>
      <DialogTitle>{titulo}</DialogTitle>
      <DialogContent>
        <DialogContentText>{mensaje}</DialogContentText>
      </DialogContent>
      <DialogActions>
        <Button onClick={onCancelar} disabled={cargando}>
          Cancelar
        </Button>
        <Button onClick={onConfirmar} color="primary" variant="contained" disabled={cargando}>
          Confirmar
        </Button>
      </DialogActions>
    </Dialog>
  );
}
