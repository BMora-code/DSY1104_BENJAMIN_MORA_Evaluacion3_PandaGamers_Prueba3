import React from "react";
import { Link } from "react-router-dom";

const CheckoutError = () => {
  return (
    <div style={{ minHeight: '100vh', color: 'var(--text)' }}>
      <div className="container mt-5">
        <div className="row justify-content-center">
          <div className="col-lg-6">
            <div className="text-center">
              {/* Icono de error */}
              <div className="mb-4">
                <i className="bi bi-x-circle-fill" style={{
                  fontSize: '5rem',
                  color: '#dc3545'
                }}></i>
              </div>

              {/* Título */}
              <h1 style={{
                color: 'var(--text)',
                fontFamily: 'var(--font-head)',
                marginBottom: '1rem'
              }}>
                Pago Rechazado
              </h1>

              {/* Mensaje */}
              <p style={{
                color: 'var(--muted)',
                fontSize: '1.1rem',
                marginBottom: '2rem'
              }}>
                Lo sentimos, no se pudo procesar tu pago. Por favor, verifica tus datos e intenta nuevamente.
              </p>

              {/* Información adicional */}
              <div className="alert" style={{
                background: 'rgba(220, 53, 69, 0.1)',
                border: '2px solid #dc3545',
                color: 'var(--text)',
                marginBottom: '2rem'
              }}>
                <h6 style={{ color: '#dc3545', marginBottom: '0.5rem' }}>
                  <i className="bi bi-info-circle me-2"></i>
                  Posibles causas del rechazo:
                </h6>
                <ul style={{ textAlign: 'left', margin: 0, paddingLeft: '1.5rem' }}>
                  <li>Datos de tarjeta incorrectos</li>
                  <li>Fondos insuficientes</li>
                  <li>Tarjeta expirada</li>
                  <li>Tarjeta bloqueada o reportada</li>
                </ul>
              </div>

              {/* Información de seguridad */}
              <div className="alert" style={{
                background: 'rgba(13, 110, 253, 0.1)',
                border: '2px solid #0dcaf0',
                color: 'var(--text)',
                marginBottom: '2rem'
              }}>
                <i className="bi bi-shield-check me-2"></i>
                <strong>Información de Seguridad:</strong> No se realizó ningún cargo a tu tarjeta.
                Puedes intentar el pago nuevamente de forma segura.
              </div>

              {/* Botones de acción */}
              <div className="d-flex gap-3 justify-content-center">
                <Link
                  to="/checkout"
                  className="btn btn-primary btn-lg"
                  style={{
                    background: 'linear-gradient(135deg, #007bff, #0056b3)',
                    border: 'none',
                    padding: '12px 30px'
                  }}
                >
                  <i className="bi bi-arrow-repeat me-2"></i>
                  Reintentar Pago
                </Link>

                <Link
                  to="/carrito"
                  className="btn btn-outline-secondary btn-lg"
                  style={{
                    border: '2px solid var(--border)',
                    color: 'var(--text)',
                    padding: '12px 30px'
                  }}
                >
                  <i className="bi bi-cart me-2"></i>
                  Revisar Carrito
                </Link>
              </div>

              {/* Enlace adicional */}
              <div className="mt-4">
                <Link
                  to="/productos"
                  style={{ color: 'var(--accent)', textDecoration: 'none' }}
                >
                  <i className="bi bi-arrow-left me-1"></i>
                  Continuar Comprando
                </Link>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default CheckoutError;