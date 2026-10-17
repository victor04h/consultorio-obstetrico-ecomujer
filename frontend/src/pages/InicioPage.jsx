import { useEffect, useState } from "react";
import Alert from "@mui/material/Alert";
import Box from "@mui/material/Box";
import Card from "@mui/material/Card";
import CardContent from "@mui/material/CardContent";
import Grid from "@mui/material/Grid";
import Paper from "@mui/material/Paper";
import Skeleton from "@mui/material/Skeleton";
import Typography from "@mui/material/Typography";

import PeopleIcon from "@mui/icons-material/People";
import LocalHospitalIcon from "@mui/icons-material/LocalHospital";
import EventAvailableIcon from "@mui/icons-material/EventAvailable";
import PaymentsIcon from "@mui/icons-material/Payments";

import api, { mensajeError } from "../api/client";
import PageHeader from "../components/PageHeader";
import EstadoChip from "../components/EstadoChip";

function TarjetaEstadistica({ icono, etiqueta, valor, color }) {
  return (
    <Card elevation={2}>
      <CardContent sx={{ display: "flex", alignItems: "center", gap: 2 }}>
        <Box
          sx={{
            width: 48,
            height: 48,
            borderRadius: "50%",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            bgcolor: `${color}.light`,
            color: `${color}.dark`,
          }}
        >
          {icono}
        </Box>
        <Box>
          <Typography variant="h5" fontWeight={700}>
            {valor}
          </Typography>
          <Typography variant="body2" color="text.secondary">
            {etiqueta}
          </Typography>
        </Box>
      </CardContent>
    </Card>
  );
}

export default function InicioPage() {
  const [resumen, setResumen] = useState(null);
  const [error, setError] = useState("");
  const [cargando, setCargando] = useState(true);

  useEffect(() => {
    api
      .get("/admin/resumen")
      .then(({ data }) => setResumen(data))
      .catch((err) => setError(mensajeError(err, "No se pudo cargar el resumen del dashboard")))
      .finally(() => setCargando(false));
  }, []);

  return (
    <Box>
      <PageHeader titulo="Inicio" subtitulo="Resumen general del consultorio" />

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      {cargando ? (
        <Grid container spacing={2}>
          {[1, 2, 3, 4].map((n) => (
            <Grid key={n} size={{ xs: 12, sm: 6, md: 3 }}>
              <Skeleton variant="rounded" height={96} />
            </Grid>
          ))}
        </Grid>
      ) : (
        resumen && (
          <>
            <Grid container spacing={2} sx={{ mb: 3 }}>
              <Grid size={{ xs: 12, sm: 6, md: 3 }}>
                <TarjetaEstadistica
                  icono={<PeopleIcon />}
                  etiqueta="Pacientes registrados"
                  valor={resumen.totalPacientes}
                  color="primary"
                />
              </Grid>
              <Grid size={{ xs: 12, sm: 6, md: 3 }}>
                <TarjetaEstadistica
                  icono={<LocalHospitalIcon />}
                  etiqueta="Profesionales activos"
                  valor={resumen.totalProfesionalesActivos}
                  color="success"
                />
              </Grid>
              <Grid size={{ xs: 12, sm: 6, md: 3 }}>
                <TarjetaEstadistica
                  icono={<EventAvailableIcon />}
                  etiqueta="Citas para hoy"
                  valor={resumen.totalCitasHoy}
                  color="warning"
                />
              </Grid>
              <Grid size={{ xs: 12, sm: 6, md: 3 }}>
                <TarjetaEstadistica
                  icono={<PaymentsIcon />}
                  etiqueta="Ingresos del mes"
                  valor={`S/ ${Number(resumen.ingresosMesActual || 0).toFixed(2)}`}
                  color="secondary"
                />
              </Grid>
            </Grid>

            <Paper sx={{ p: 3 }}>
              <Typography variant="h6" fontWeight={600} sx={{ mb: 2 }}>
                Citas por estado
              </Typography>
              <Box sx={{ display: "flex", flexWrap: "wrap", gap: 1.5 }}>
                {Object.entries(resumen.citasPorEstado || {}).map(([estado, cantidad]) => (
                  <Box
                    key={estado}
                    sx={{
                      display: "flex",
                      alignItems: "center",
                      gap: 1,
                      p: 1,
                      px: 1.5,
                      border: "1px solid",
                      borderColor: "divider",
                      borderRadius: 2,
                    }}
                  >
                    <EstadoChip estado={estado} />
                    <Typography variant="body2">{cantidad}</Typography>
                  </Box>
                ))}
                {Object.keys(resumen.citasPorEstado || {}).length === 0 && (
                  <Typography variant="body2" color="text.secondary">
                    Aún no hay citas registradas.
                  </Typography>
                )}
              </Box>
            </Paper>
          </>
        )
      )}
    </Box>
  );
}
