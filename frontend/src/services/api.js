import axios from 'axios'

// Detectar URL según entorno (CRA usa process.env)
const API_BASE_URL =
  process.env.REACT_APP_API_URL || 'http://localhost:8080/api/v1'

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Attach JWT token from localStorage to every request if present
api.interceptors.request.use(
  (config) => {
    try {
      const url = config.url || ''

      // Do not attach Authorization header for login/register
      if (
        url.includes('/auth/login') ||
        url.includes('/auth/register') ||
        url.includes('/auth/')
      ) {
        return config
      }

      const token = localStorage.getItem('token')
      if (token) {
        config.headers = config.headers || {}
        config.headers.Authorization = `Bearer ${token}`
      }
    } catch (e) {
      // ignore
    }
    return config
  },
  (error) => Promise.reject(error)
)

// Handle 401/403
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (
      error.response &&
      (error.response.status === 401 || error.response.status === 403)
    ) {
      localStorage.removeItem('token')
      localStorage.removeItem('auth_user')
      console.warn('Token expirado o inválido — inicia sesión nuevamente')
    }
    return Promise.reject(error)
  }
)

export default api