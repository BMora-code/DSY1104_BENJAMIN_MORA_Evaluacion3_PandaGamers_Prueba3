import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080/api/v1',
  headers: {
    'Content-Type': 'application/json'
  }
})

// Attach JWT token from localStorage to every request if present
api.interceptors.request.use((config) => {
  try {
    const url = config.url || ''
    // Do not attach Authorization header for auth endpoints (login/register)
    if (url.includes('/auth/login') || url.includes('/auth/register') || url.includes('/auth/')) {
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
}, (error) => Promise.reject(error))

// Handle 401/403 responses (token expired or invalid)
api.interceptors.response.use((response) => {
  return response
}, (error) => {
  if (error.response && (error.response.status === 401 || error.response.status === 403)) {
    // Token expired or invalid - clear local storage and redirect to login
    localStorage.removeItem('token')
    localStorage.removeItem('auth_user')
    // Optional: redirect to login page
    // window.location.href = '/login'
    console.warn('Token expired or invalid - please login again')
  }
  return Promise.reject(error)
})

export default api
