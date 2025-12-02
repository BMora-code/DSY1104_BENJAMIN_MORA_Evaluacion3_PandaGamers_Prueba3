import React, { useContext, useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { AuthContext } from "../context/AuthContext.js";
import dataStore from "../data/dataStore";
import api from "../services/api";

const MisCompras = () => {
  const { user } = useContext(AuthContext);
  const [userOrders, setUserOrders] = useState([]);

  useEffect(() => {
    if (user) {
      // Intentar cargar órdenes desde backend para el usuario autenticado
      (async () => {
        try {
          const res = await api.get('/orders');
          const orders = Array.isArray(res.data) ? res.data : [];
          const sorted = orders.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt)).map((order, index, arr) => ({
            ...order,
            displayId: arr.length - index
          }));
          setUserOrders(sorted);
        } catch (err) {
          console.warn('Falling back to dataStore orders for MisCompras', err);
          const orders = dataStore.getOrders()
            .filter(order => order.userId === user.username)
            .sort((a, b) => new Date(b.createdAt || b.date) - new Date(a.createdAt || a.date))
            .map((order, index, arr) => ({
              ...order,
              displayId: arr.length - index // La más reciente tiene el número más alto
            }));
          setUserOrders(orders);
        }
      })();
    }
  }, [user]);

  // Actualizar las órdenes cuando se complete una compra
  useEffect(() => {
    const handleOrdersUpdate = () => {
      if (user) {
        (async () => {
          try {
            const res = await api.get('/orders');
            const orders = Array.isArray(res.data) ? res.data : [];
            const sorted = orders.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt)).map((order, index, arr) => ({
              ...order,
              displayId: arr.length - index
            }));
            setUserOrders(sorted);
          } catch (err) {
            const orders = dataStore.getOrders()
              .filter(order => order.userId === user.username)
              .sort((a, b) => new Date(b.createdAt || b.date) - new Date(a.createdAt || a.date))
              .map((order, index, arr) => ({
                ...order,
                displayId: arr.length - index // La más reciente tiene el número más alto
              }));
            setUserOrders(orders);
          }
        })();
      }
    };

    window.addEventListener('ordersUpdated', handleOrdersUpdate);

    return () => {
      window.removeEventListener('ordersUpdated', handleOrdersUpdate);
    };
  }, [user]);

  if (!user) {
    return (
      <div style={{ minHeight: '100vh', color: 'var(--text)' }}>
        <div className="container mt-5">
          <div className="text-center py-5">
            <div className="mb-4">
              <i className="bi bi-shield-lock display-1" style={{ color: 'var(--accent)' }}></i>
            </div>
            <h2 style={{ color: 'var(--text)' }}>Acceso Restringido</h2>
            <p style={{ color: 'var(--muted)' }}>
              Debes iniciar sesión para ver tus compras.
            </p>
            <Link to="/login" className="btn-neon">
              Iniciar Sesión
            </Link>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div style={{ minHeight: '100vh', color: 'var(--text)' }}>
      <div className="container mt-4">
        <div className="d-flex justify-content-between align-items-center mb-4">
          <h2 style={{ color: 'var(--text)', fontFamily: 'var(--font-head)' }}>
            <i className="bi bi-receipt me-2"></i>
            Mis Compras
          </h2>
          <Link to="/" className="btn btn-outline-primary">
            <i className="bi bi-house me-1"></i>
            Volver al Inicio
          </Link>
        </div>

        {userOrders.length === 0 ? (
          <div className="text-center py-5">
            <div className="mb-4">
              <i className="bi bi-receipt-x display-1" style={{ color: 'var(--accent)' }}></i>
            </div>
            <h3 style={{ color: 'var(--text)' }}>No tienes compras realizadas</h3>
            <p style={{ color: 'var(--muted)' }}>
              ¡Realiza tu primera compra para ver tu historial aquí!
            </p>
            <Link to="/productos" className="btn-neon">
              Explorar Productos
            </Link>
          </div>
        ) : (
          <div className="row">
            {userOrders.map(order => (
              <div key={order.id} className="col-lg-6 mb-4">
                <div className="card" style={{ background: 'var(--surface)', border: '2px solid var(--border)', color: 'var(--text)' }}>
                  <div className="card-header" style={{ background: 'var(--surface)', borderBottom: '1px solid var(--border)', color: 'var(--text)' }}>
                    <div className="d-flex justify-content-between align-items-center">
                      <h5 className="mb-0" style={{ fontFamily: 'var(--font-head)', color: 'var(--text)' }}>
                        Orden #{order.displayId}
                      </h5>
                      <span className={`badge ${order.status === 'completed' ? 'bg-success' : 'bg-warning'}`}>
                        {order.status === 'completed' ? 'Completada' : order.status}
                      </span>
                    </div>
                  </div>
                  <div className="card-body">
                    <div className="mb-3">
                      <small style={{ color: 'var(--muted)' }}>
                        Fecha: {new Date(order.createdAt || order.date).toLocaleDateString('es-CL')}
                      </small>
                    </div>

                    <div className="mb-3">
                      <h6 style={{ color: 'var(--text)' }}>Productos:</h6>
                      {order.items.map((item, index) => (
                        <div key={index} className="d-flex justify-content-between align-items-center mb-2" style={{ padding: '8px', background: 'rgba(255,255,255,0.05)', borderRadius: '4px' }}>
                          <div className="d-flex align-items-center">
                            <img
                              src={item.imagen || item.image}
                              alt={item.nombre || item.name}
                              style={{ width: '40px', height: '40px', objectFit: 'cover', borderRadius: '4px', marginRight: '10px' }}
                              onError={(e) => e.target.src = "https://via.placeholder.com/40x40/6c757d/ffffff?text=Img"}
                            />
                            <div>
                              <span style={{ color: 'var(--text)', fontWeight: 'bold' }}>{item.nombre || item.name}</span>
                              <br />
                              <small style={{ color: 'var(--muted)' }}>Cant: {item.cantidad}</small>
                              {item.tieneDescuentoDuoc && (
                                <>
                                  <div style={{ color: 'var(--muted)', fontSize: '0.8em', textDecoration: 'line-through' }}>
                                    Antes: ${(item.precioOriginal || item.price || 0).toLocaleString('es-CL')}
                                  </div>
                                  <div style={{ color: 'var(--accent)', fontSize: '0.8em', fontWeight: 'bold' }}>
                                    🎓 DUOC 20% OFF
                                  </div>
                                </>
                              )}
                            </div>
                          </div>
                          <div style={{ textAlign: 'right' }}>
                            {item.tieneDescuentoDuoc ? (
                              <>
                                <div style={{ textDecoration: 'line-through', color: 'var(--muted)', fontSize: '0.8em' }}>
                                  ${(item.precioOriginal || item.price || 0).toLocaleString('es-CL')}
                                </div>
                                <div style={{ color: 'var(--accent)', fontWeight: 'bold' }}>
                                  ${(item.price || 0).toLocaleString('es-CL')}
                                </div>
                              </>
                            ) : (
                              <span style={{ color: 'var(--text)', fontWeight: 'bold' }}>
                                ${(item.price || 0).toLocaleString('es-CL')}
                              </span>
                            )}
                          </div>
                        </div>
                      ))}
                    </div>

                    <hr style={{ borderColor: 'var(--border)' }} />

                    <div className="d-flex justify-content-between mb-2">
                      <span style={{ color: 'var(--text)' }}>Subtotal:</span>
                      <span style={{ color: 'var(--text)' }}>${order.subtotal?.toLocaleString('es-CL') || '0'}</span>
                    </div>
                    {order.descuentoDuoc > 0 && (
                      <div className="d-flex justify-content-between mb-2">
                        <span style={{ color: 'var(--accent)' }}>Descuento DUOC (20%):</span>
                        <span style={{ color: 'var(--accent)' }}>-${order.descuentoDuoc?.toLocaleString('es-CL') || '0'}</span>
                      </div>
                    )}
                    <div className="d-flex justify-content-between mb-2">
                      <span style={{ color: 'var(--text)' }}>IVA (19%):</span>
                      <span style={{ color: 'var(--text)' }}>${order.iva?.toLocaleString('es-CL') || '0'}</span>
                    </div>
                    <div className="d-flex justify-content-between mb-2">
                      <span style={{ color: 'var(--text)' }}>Envío:</span>
                      <span style={{ color: 'var(--text)' }}>${order.shippingCost?.toLocaleString('es-CL') || '0'}</span>
                    </div>
                    <hr style={{ borderColor: 'var(--border)' }} />
                    <div className="d-flex justify-content-between">
                      <strong style={{ color: 'var(--text)' }}>Total:</strong>
                      <strong style={{ color: 'var(--accent)' }}>${order.total?.toLocaleString('es-CL') || '0'}</strong>
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default MisCompras;