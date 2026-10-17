import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";

export default function PageHeader({ titulo, subtitulo, acciones }) {
  return (
    <Box
      sx={{
        display: "flex",
        justifyContent: "space-between",
        alignItems: "center",
        flexWrap: "wrap",
        gap: 2,
        mb: 3,
      }}
    >
      <Box>
        <Typography variant="h5" fontWeight={600}>
          {titulo}
        </Typography>
        {subtitulo && (
          <Typography variant="body2" color="text.secondary">
            {subtitulo}
          </Typography>
        )}
      </Box>
      {acciones && <Box sx={{ display: "flex", gap: 1 }}>{acciones}</Box>}
    </Box>
  );
}
