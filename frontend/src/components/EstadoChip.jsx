import Chip from "@mui/material/Chip";

const COLORES = {
  ACTIVO: "success",
  INACTIVO: "default",
  AGENDADA: "info",
  PAGADA: "primary",
  CONFIRMADA: "success",
  REASIGNADA: "warning",
  RECHAZADA: "error",
  CANCELADA: "default",
  PENDIENTE: "warning",
  APROBADO: "success",
  APROBADA: "success",
  LIBRE: "success",
  OCUPADO: "default",
};

export default function EstadoChip({ estado }) {
  if (!estado) return null;
  return <Chip label={estado} color={COLORES[estado] || "default"} size="small" />;
}
