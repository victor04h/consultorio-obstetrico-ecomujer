import { Navigate, Outlet } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

/**
 * Bloquea el acceso directo (por URL) a una seccion del panel cuando el
 * perfil de la cuenta no tiene ese modulo marcado en Perfiles. El menu
 * lateral (DashboardLayout) ya oculta la opcion, pero esto evita que alguien
 * entre escribiendo la ruta a mano.
 *
 * - modulo: codigo del permiso requerido (ej. "CITAS"). Si se omite, la ruta
 *   queda abierta a cualquier cuenta autenticada (ej. Inicio).
 * - soloAdministrador: para secciones que no se controlan por checkbox
 *   (ej. Usuarios), solo el perfil Administrador puede entrar.
 */
export default function PermisoRoute({ modulo, soloAdministrador = false }) {
  const { usuario, tienePermiso } = useAuth();

  if (soloAdministrador && usuario?.rol !== "Administrador") {
    return <Navigate to="/" replace />;
  }

  if (!soloAdministrador && !tienePermiso(modulo)) {
    return <Navigate to="/" replace />;
  }

  return <Outlet />;
}
