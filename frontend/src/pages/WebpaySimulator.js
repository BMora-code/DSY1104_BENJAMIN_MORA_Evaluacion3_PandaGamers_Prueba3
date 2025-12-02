import React, { useEffect, useState, useContext } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import { CartContext } from "../context/CartContext.js";
import api from "../services/api.js";

const WebpaySimulator = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const { clearCart } = useContext(CartContext);
  const [isProcessing, setIsProcessing] = useState(false);
  const [orderDetails, setOrderDetails] = useState(null);

  const token = searchParams.get("token");

  useEffect(() => {
    if (!token) {
      navigate("/");
      return;
    }

    // Aquí podrías cargar detalles de la orden si es necesario
    // Por ahora solo mostramos el simulador
  }, [token, navigate]);

  const handlePayment = async (status) => {
    setIsProcessing(true);

    try {
      const response = await api.post('/pago/confirmar', {
        token: token,
        status: status
      });

      if (response.data.redirect) {
        // Limpiar carrito solo si el pago fue exitoso
        if (status === "AUTHORIZED") {
          clearCart();
          // Notificar a otros componentes que las órdenes se actualizaron
          window.dispatchEvent(new CustomEvent('ordersUpdated'));
          window.dispatchEvent(new CustomEvent('productsUpdated'));
        }
        navigate(response.data.redirect);
      } else {
        navigate("/");
      }
    } catch (error) {
      console.error("Error confirming payment:", error);
      navigate("/checkout/error");
    } finally {
      setIsProcessing(false);
    }
  };

  return (
    <div style={{
      minHeight: '100vh',
      background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      padding: '20px'
    }}>
      <div className="container">
        <div className="row justify-content-center">
          <div className="col-md-6 col-lg-4">
            <div className="card shadow-lg" style={{
              background: 'white',
              border: 'none',
              borderRadius: '15px',
              boxShadow: '0 20px 40px rgba(0,0,0,0.1)'
            }}>
              <div className="card-body text-center p-5">
                {/* Logo Webpay */}
                <div className="mb-4">
                  <div style={{
                    width: '80px',
                    height: '80px',
                    background: 'linear-gradient(135deg, #0033A0, #00A3E0)',
                    borderRadius: '50%',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    margin: '0 auto 20px',
                    boxShadow: '0 8px 20px rgba(0,0,0,0.2)'
                  }}>
                    <span style={{
                      color: 'white',
                      fontSize: '24px',
                      fontWeight: 'bold'
                    }}>W</span>
                  </div>
                  <h2 style={{
                    color: '#0033A0',
                    fontWeight: 'bold',
                    marginBottom: '10px'
                  }}>Webpay</h2>
                  <p style={{
                    color: '#666',
                    marginBottom: '30px'
                  }}>Simulador de Pago</p>
                </div>

                {/* Información del pago */}
                <div className="mb-4" style={{
                  background: '#f8f9fa',
                  padding: '20px',
                  borderRadius: '10px',
                  marginBottom: '30px'
                }}>
                  <h5 style={{ color: '#0033A0', marginBottom: '15px' }}>
                    Confirmar Pago
                  </h5>
                  <div className="text-start">
                    <p className="mb-2" style={{ color: '#666', fontSize: '14px' }}>
                      <strong>Token de transacción:</strong>
                    </p>
                    <code style={{
                      background: '#e9ecef',
                      padding: '5px 10px',
                      borderRadius: '4px',
                      fontSize: '12px',
                      wordBreak: 'break-all',
                      display: 'block'
                    }}>
                      {token}
                    </code>
                  </div>
                </div>

                {/* Botones de acción */}
                <div className="d-grid gap-3">
                  <button
                    className="btn btn-success btn-lg"
                    onClick={() => handlePayment("AUTHORIZED")}
                    disabled={isProcessing}
                    style={{
                      background: 'linear-gradient(135deg, #28a745, #20c997)',
                      border: 'none',
                      borderRadius: '10px',
                      padding: '15px',
                      fontSize: '16px',
                      fontWeight: 'bold',
                      boxShadow: '0 4px 15px rgba(40, 167, 69, 0.3)'
                    }}
                  >
                    {isProcessing ? (
                      <>
                        <span className="spinner-border spinner-border-sm me-2" role="status"></span>
                        Procesando...
                      </>
                    ) : (
                      <>
                        <i className="bi bi-check-circle me-2"></i>
                        Aprobar Pago
                      </>
                    )}
                  </button>

                  <button
                    className="btn btn-danger btn-lg"
                    onClick={() => handlePayment("FAILED")}
                    disabled={isProcessing}
                    style={{
                      background: 'linear-gradient(135deg, #dc3545, #fd7e14)',
                      border: 'none',
                      borderRadius: '10px',
                      padding: '15px',
                      fontSize: '16px',
                      fontWeight: 'bold',
                      boxShadow: '0 4px 15px rgba(220, 53, 69, 0.3)'
                    }}
                  >
                    <i className="bi bi-x-circle me-2"></i>
                    Rechazar Pago
                  </button>
                </div>

                {/* Información adicional */}
                <div className="mt-4 pt-4 border-top" style={{ borderColor: '#e9ecef !important' }}>
                  <small style={{ color: '#666', fontSize: '12px' }}>
                    <i className="bi bi-info-circle me-1"></i>
                    Este es un simulador de Webpay para fines de desarrollo.
                    En producción, este paso sería manejado por Transbank.
                  </small>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default WebpaySimulator;