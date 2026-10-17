import axios from "axios";

const API_URL = import.meta.env.VITE_API_URL || "http://localhost:8080/api";

const api = axios.create({
  baseURL: API_URL,
});

// Adjunta el token JWT (si existe) a cada peticion.
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Si el backend responde 401 (token vencido/invalido), cierra la sesion
// local y manda al usuario de vuelta al login.
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem("token");
      localStorage.removeItem("usuario");
      if (window.location.pathname !== "/login") {
        window.location.href = "/login";
      }
    }
    return Promise.reject(error);
  }
);

/** Extrae un mensaje de error legible de una respuesta del backend. */
export function mensajeError(error, porDefecto = "Ocurrio un error inesperado") {
  return error?.response?.data?.mensaje || porDefecto;
}

export default api;
