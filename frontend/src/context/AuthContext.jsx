import { createContext, useContext, useMemo, useState } from "react";
import api from "../api/client";

const AuthContext = createContext(null);

function leerUsuarioGuardado() {
  try {
    const raw = localStorage.getItem("usuario");
    return raw ? JSON.parse(raw) : null;
  } catch {
    return null;
  }
}

export function AuthProvider({ children }) {
  const [usuario, setUsuario] = useState(leerUsuarioGuardado);
  const [token, setToken] = useState(() => localStorage.getItem("token"));

  const login = async (correo, contrasena) => {
    const { data } = await api.post("/auth/login", { correo, contrasena });
    const datosUsuario = {
      correo: data.correo,
      nombreCompleto: data.nombreCompleto,
      rol: data.rol,
      permisos: data.permisos || [],
    };
    localStorage.setItem("token", data.token);
    localStorage.setItem("usuario", JSON.stringify(datosUsuario));
    setToken(data.token);
    setUsuario(datosUsuario);
  };

  const logout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("usuario");
    setToken(null);
    setUsuario(null);
  };

  /**
   * El Administrador siempre tiene acceso a todo el panel. Para el resto de
   * perfiles, solo a los modulos que su perfil tenga marcados (ver Perfiles).
   */
  const tienePermiso = (modulo) => {
    if (!modulo) return true;
    if (!usuario) return false;
    if (usuario.rol === "Administrador") return true;
    return (usuario.permisos || []).includes(modulo);
  };

  const value = useMemo(
    () => ({ usuario, token, autenticado: Boolean(token), login, logout, tienePermiso }),
    [usuario, token]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) {
    throw new Error("useAuth debe usarse dentro de un AuthProvider");
  }
  return ctx;
}
