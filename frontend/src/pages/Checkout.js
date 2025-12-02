import React, { useState, useContext, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { CartContext } from "../context/CartContext.js";
import { AuthContext } from "../context/AuthContext.js";
import dataStore from "../data/dataStore.js";
import api from "../services/api.js";

const Checkout = () => {
  const { cart, eliminarDelCarrito, clearCart } = useContext(CartContext);
  const { user } = useContext(AuthContext);
  const navigate = useNavigate();
  const [isLoading, setIsLoading] = useState(true);

  // Esperar a que el carrito se cargue desde localStorage
  useEffect(() => {
    const timer = setTimeout(() => {
      setIsLoading(false);
    }, 100); // Pequeño delay para permitir inicialización del contexto

    return () => clearTimeout(timer);
  }, []);

  // Verificar carrito vacío después de que se haya cargado
  useEffect(() => {
    if (!isLoading && (!Array.isArray(cart) || cart.length === 0)) {
      alert("El carrito está vacío");
      navigate("/carrito");
      return;
    }
  }, [cart, navigate, isLoading]);

  const [shippingInfo, setShippingInfo] = useState({
    firstName: "",
    lastName: "",
    email: "",
    phone: "",
    address: "",
    city: "",
    region: "",
    postalCode: "",
    deliveryOption: "standard"
  });

  const [paymentInfo, setPaymentInfo] = useState({
    cardNumber: "",
    expiryDate: "",
    cvv: "",
    cardName: ""
  });

  const [errors, setErrors] = useState({});
  const [isProcessing, setIsProcessing] = useState(false);

  // Cargar información del usuario si está logueado
  useEffect(() => {
    if (user) {
      setShippingInfo(prev => ({
        ...prev,
        firstName: user.firstName || "",
        lastName: user.lastName || "",
        email: user.email || ""
      }));
    }
  }, [user]);

  // Calcular totales con descuento aplicado a nivel de producto
  const subtotal = cart.reduce((acc, item) => {
    const precioOriginal = (item.precioOriginal || item.precio || 0);
    const precioFinal = user && user.hasDuocDiscount ? Math.round(precioOriginal * 0.8) : precioOriginal;
    return acc + (precioFinal * item.cantidad);
  }, 0);
  const descuentoDuoc = user && user.hasDuocDiscount ?
    cart.reduce((acc, item) => {
      const precioOriginal = (item.precioOriginal || item.precio || 0);
      const precioConDescuento = Math.round(precioOriginal * 0.8);
      return acc + ((precioOriginal - precioConDescuento) * item.cantidad);
    }, 0) : 0;
  const subtotalConDescuento = subtotal; // Ya incluye el descuento
  const shippingCost = shippingInfo.deliveryOption === "express" ? 5000 : 2500;
  const iva = Math.round(subtotalConDescuento * 0.19); // IVA solo sobre subtotal con descuento
  const total = subtotalConDescuento + iva + shippingCost; // Mismo orden que carrito: subtotal + IVA + envío

  const deliveryOptions = [
    { value: "standard", label: "Envío estándar (2-3 días)", cost: 2500 },
    { value: "express", label: "Envío express (24 horas)", cost: 5000 },
    { value: "pickup", label: "Retiro en tienda", cost: 0 }
  ];

  const handleShippingChange = (e) => {
    const { name, value } = e.target;
    setShippingInfo(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handlePaymentChange = (e) => {
    const { name, value } = e.target;
    setPaymentInfo(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const validateForm = () => {
    const newErrors = {};

    // Validar información de envío
    if (!shippingInfo.firstName.trim()) newErrors.firstName = "Nombre es requerido";
    if (!shippingInfo.lastName.trim()) newErrors.lastName = "Apellido es requerido";
    if (!shippingInfo.email.trim()) newErrors.email = "Email es requerido";
    if (!shippingInfo.phone.trim()) newErrors.phone = "Teléfono es requerido";
    if (!shippingInfo.address.trim()) newErrors.address = "Dirección es requerida";
    if (!shippingInfo.city.trim()) newErrors.city = "Ciudad es requerida";
    if (!shippingInfo.region.trim()) newErrors.region = "Región es requerida";
    if (!shippingInfo.postalCode.trim()) newErrors.postalCode = "Código postal es requerido";

    // Validar información de pago
    if (!paymentInfo.cardNumber.trim()) newErrors.cardNumber = "Número de tarjeta es requerido";
    if (!paymentInfo.expiryDate.trim()) newErrors.expiryDate = "Fecha de expiración es requerida";
    if (!paymentInfo.cvv.trim()) newErrors.cvv = "CVV es requerido";
    if (!paymentInfo.cardName.trim()) newErrors.cardName = "Nombre en la tarjeta es requerido";

    // Validar formato de email
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (shippingInfo.email && !emailRegex.test(shippingInfo.email)) {
      newErrors.email = "Email no válido";
    }

    // Validar formato de tarjeta
    const cardRegex = /^\d{16}$/;
    if (paymentInfo.cardNumber && !cardRegex.test(paymentInfo.cardNumber.replace(/\s/g, ''))) {
      newErrors.cardNumber = "Número de tarjeta debe tener 16 dígitos";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    if (cart.length === 0) {
      alert("El carrito está vacío");
      return;
    }

    setIsProcessing(true);

    try {
      // Build order items from cart with discounted prices
      const items = cart.map(item => {
        const precioOriginal = Number(item.precio || item.price || 0);
        const precioFinal = user && user.hasDuocDiscount ? Math.round(precioOriginal * 0.8) : precioOriginal;

        return {
          productId: String(item.id),
          quantity: Number(item.cantidad || item.quantity || 1),
          name: item.nombre || item.name,
          price: precioFinal, // Precio con descuento aplicado
          precioOriginal: precioOriginal, // Guardar precio original para referencia
          image: item.imagen || item.image,
          tieneDescuentoDuoc: user && user.hasDuocDiscount
        };
      });

      // Build order request with all information
      const orderRequest = {
        items: items,
        shippingInfo: shippingInfo,
        deliveryOption: shippingInfo.deliveryOption,
        subtotal: subtotal,
        descuentoDuoc: descuentoDuoc,
        iva: iva,
        shippingCost: shippingCost,
        total: total
      };

      // Iniciar pago con Webpay
      const paymentResp = await api.post('/pago/iniciar', orderRequest);

      if (paymentResp.data && paymentResp.data.url && paymentResp.data.token) {
        // Redirigir al simulador de Webpay
        window.location.href = `${paymentResp.data.url}?token=${paymentResp.data.token}`;
      } else {
        throw new Error('Error al iniciar el pago');
      }
    } catch (error) {
      console.error("Error al iniciar el pago:", error);
      alert("Error al procesar el pago. Por favor intenta nuevamente.");
    } finally {
      setIsProcessing(false);
    }
  };

  // Mostrar loading mientras se carga el carrito
  if (isLoading) {
    return (
      <div className="container mt-5">
        <div className="text-center">
          <div className="spinner-border" role="status">
            <span className="visually-hidden">Cargando carrito...</span>
          </div>
          <p className="mt-2">Cargando información del carrito...</p>
        </div>
      </div>
    );
  }

  // Esta validación es redundante ahora que usamos useEffect, pero la mantenemos como fallback
  if (cart.length === 0) {
    return (
      <div className="container mt-5">
        <div className="text-center">
          <div className="alert alert-warning">
            <h4>Carrito vacío</h4>
            <p>No hay productos en tu carrito.</p>
            <a href="/productos" className="btn btn-primary">Ver Productos</a>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="container mt-4">
      <div className="row">
        <div className="col-lg-8">
          <h2 className="mb-4">Checkout</h2>

          <form onSubmit={handleSubmit}>
            {/* Información de envío */}
            <div className="card mb-4">
              <div className="card-header">
                <h5 className="mb-0">Información de Envío</h5>
              </div>
              <div className="card-body">
                <div className="row">
                  <div className="col-md-6 mb-3">
                    <label className="form-label">Nombre *</label>
                    <input
                      type="text"
                      className={`form-control ${errors.firstName ? 'is-invalid' : ''}`}
                      name="firstName"
                      value={shippingInfo.firstName}
                      onChange={handleShippingChange}
                      required
                    />
                    {errors.firstName && <div className="invalid-feedback">{errors.firstName}</div>}
                  </div>
                  <div className="col-md-6 mb-3">
                    <label className="form-label">Apellido *</label>
                    <input
                      type="text"
                      className={`form-control ${errors.lastName ? 'is-invalid' : ''}`}
                      name="lastName"
                      value={shippingInfo.lastName}
                      onChange={handleShippingChange}
                      required
                    />
                    {errors.lastName && <div className="invalid-feedback">{errors.lastName}</div>}
                  </div>
                </div>

                <div className="row">
                  <div className="col-md-6 mb-3">
                    <label className="form-label">Email *</label>
                    <input
                      type="email"
                      className={`form-control ${errors.email ? 'is-invalid' : ''}`}
                      name="email"
                      value={shippingInfo.email}
                      onChange={handleShippingChange}
                      required
                    />
                    {errors.email && <div className="invalid-feedback">{errors.email}</div>}
                  </div>
                  <div className="col-md-6 mb-3">
                    <label className="form-label">Teléfono *</label>
                    <input
                      type="tel"
                      className={`form-control ${errors.phone ? 'is-invalid' : ''}`}
                      name="phone"
                      value={shippingInfo.phone}
                      onChange={handleShippingChange}
                      required
                    />
                    {errors.phone && <div className="invalid-feedback">{errors.phone}</div>}
                  </div>
                </div>

                <div className="mb-3">
                  <label className="form-label">Dirección *</label>
                  <input
                    type="text"
                    className={`form-control ${errors.address ? 'is-invalid' : ''}`}
                    name="address"
                    value={shippingInfo.address}
                    onChange={handleShippingChange}
                    placeholder="Calle, número, departamento"
                    required
                  />
                  {errors.address && <div className="invalid-feedback">{errors.address}</div>}
                </div>

                <div className="row">
                  <div className="col-md-4 mb-3">
                    <label className="form-label">Ciudad *</label>
                    <input
                      type="text"
                      className={`form-control ${errors.city ? 'is-invalid' : ''}`}
                      name="city"
                      value={shippingInfo.city}
                      onChange={handleShippingChange}
                      required
                    />
                    {errors.city && <div className="invalid-feedback">{errors.city}</div>}
                  </div>
                  <div className="col-md-4 mb-3">
                    <label className="form-label">Región *</label>
                    <select
                      className={`form-select ${errors.region ? 'is-invalid' : ''}`}
                      name="region"
                      value={shippingInfo.region}
                      onChange={handleShippingChange}
                      required
                    >
                      <option value="">Seleccionar región</option>
                      <option value="arica">Arica y Parinacota</option>
                      <option value="tarapaca">Tarapacá</option>
                      <option value="antofagasta">Antofagasta</option>
                      <option value="atacama">Atacama</option>
                      <option value="coquimbo">Coquimbo</option>
                      <option value="valparaiso">Valparaíso</option>
                      <option value="metropolitana">Metropolitana</option>
                      <option value="ohiggins">O'Higgins</option>
                      <option value="maule">Maule</option>
                      <option value="nuble">Ñuble</option>
                      <option value="biobio">Biobío</option>
                      <option value="araucania">Araucanía</option>
                      <option value="losrios">Los Ríos</option>
                      <option value="loslagos">Los Lagos</option>
                      <option value="aysen">Aysén</option>
                      <option value="magallanes">Magallanes</option>
                    </select>
                    {errors.region && <div className="invalid-feedback">{errors.region}</div>}
                  </div>
                  <div className="col-md-4 mb-3">
                    <label className="form-label">Código Postal *</label>
                    <input
                      type="text"
                      className={`form-control ${errors.postalCode ? 'is-invalid' : ''}`}
                      name="postalCode"
                      value={shippingInfo.postalCode}
                      onChange={handleShippingChange}
                      required
                    />
                    {errors.postalCode && <div className="invalid-feedback">{errors.postalCode}</div>}
                  </div>
                </div>

                <div className="mb-3">
                  <label className="form-label">Opción de entrega *</label>
                  {deliveryOptions.map(option => (
                    <div key={option.value} className="form-check">
                      <input
                        className="form-check-input"
                        type="radio"
                        name="deliveryOption"
                        value={option.value}
                        checked={shippingInfo.deliveryOption === option.value}
                        onChange={handleShippingChange}
                        required
                      />
                      <label className="form-check-label">
                        {option.label} - ${option.cost.toLocaleString()}
                      </label>
                    </div>
                  ))}
                </div>
              </div>
            </div>

            {/* Información de pago */}
            <div className="card mb-4">
              <div className="card-header">
                <h5 className="mb-0">Información de Pago</h5>
              </div>
              <div className="card-body">
                <div className="mb-3">
                  <label className="form-label">Número de Tarjeta *</label>
                  <input
                    type="text"
                    className={`form-control ${errors.cardNumber ? 'is-invalid' : ''}`}
                    name="cardNumber"
                    value={paymentInfo.cardNumber}
                    onChange={handlePaymentChange}
                    placeholder="1234 5678 9012 3456"
                    maxLength="19"
                    required
                  />
                  {errors.cardNumber && <div className="invalid-feedback">{errors.cardNumber}</div>}
                </div>

                <div className="row">
                  <div className="col-md-6 mb-3">
                    <label className="form-label">Fecha de Expiración *</label>
                    <input
                      type="text"
                      className={`form-control ${errors.expiryDate ? 'is-invalid' : ''}`}
                      name="expiryDate"
                      value={paymentInfo.expiryDate}
                      onChange={handlePaymentChange}
                      placeholder="MM/AA"
                      maxLength="5"
                      required
                    />
                    {errors.expiryDate && <div className="invalid-feedback">{errors.expiryDate}</div>}
                  </div>
                  <div className="col-md-6 mb-3">
                    <label className="form-label">CVV *</label>
                    <input
                      type="text"
                      className={`form-control ${errors.cvv ? 'is-invalid' : ''}`}
                      name="cvv"
                      value={paymentInfo.cvv}
                      onChange={handlePaymentChange}
                      placeholder="123"
                      maxLength="4"
                      required
                    />
                    {errors.cvv && <div className="invalid-feedback">{errors.cvv}</div>}
                  </div>
                </div>

                <div className="mb-3">
                  <label className="form-label">Nombre en la Tarjeta *</label>
                  <input
                    type="text"
                    className={`form-control ${errors.cardName ? 'is-invalid' : ''}`}
                    name="cardName"
                    value={paymentInfo.cardName}
                    onChange={handlePaymentChange}
                    placeholder="Como aparece en la tarjeta"
                    required
                  />
                  {errors.cardName && <div className="invalid-feedback">{errors.cardName}</div>}
                </div>
              </div>
            </div>

            <div className="d-flex gap-2 mb-4">
              <button
                type="submit"
                className="btn btn-success btn-lg flex-fill"
                disabled={isProcessing}
              >
                {isProcessing ? (
                  <>
                    <span className="spinner-border spinner-border-sm me-2" role="status"></span>
                    Procesando Pago...
                  </>
                ) : (
                  `Pagar $${total.toLocaleString()}`
                )}
              </button>
              <button
                type="button"
                className="btn btn-secondary btn-lg"
                onClick={() => navigate("/carrito")}
              >
                Volver al Carrito
              </button>
            </div>
          </form>
        </div>

        {/* Resumen de compra */}
        <div className="col-lg-4">
          <div className="card" style={{ position: 'sticky', top: '20px' }}>
            <div className="card-header">
              <h5 className="mb-0">Resumen de Compra</h5>
            </div>
            <div className="card-body">
              {user && user.hasDuocDiscount && (
                <div className="alert mb-4 text-center" style={{
                  background: 'linear-gradient(135deg, rgba(57, 255, 20, 0.2), rgba(57, 255, 20, 0.1))',
                  border: '2px solid var(--accent)',
                  color: 'var(--text)',
                  fontSize: '1.1rem',
                  fontWeight: 'bold',
                  boxShadow: '0 4px 15px rgba(57, 255, 20, 0.3)'
                }}>
                  <i className="bi bi-star-fill me-2" style={{ fontSize: '1.3rem', color: 'var(--accent)' }}></i>
                  🎓 ¡DESCUENTO DUOC UC ACTIVADO! 🎓<br />
                  <span style={{ fontSize: '0.9rem', fontWeight: 'normal' }}>20% OFF en todos tus productos</span>
                </div>
              )}

              {cart.map(item => {
                const precioOriginal = (item.precioOriginal || item.precio || 0);
                const precioConDescuento = user && user.hasDuocDiscount ? Math.round(precioOriginal * 0.8) : precioOriginal;
                const totalOriginal = precioOriginal * item.cantidad;
                const totalConDescuento = precioConDescuento * item.cantidad;

                return (
                  <div key={item.id} className="mb-3 p-3" style={{
                    background: 'rgba(255,255,255,0.05)',
                    borderRadius: '8px',
                    border: '1px solid rgba(255,255,255,0.1)'
                  }}>
                    <div className="d-flex justify-content-between align-items-center">
                      <div>
                        <span style={{ fontWeight: 'bold', color: 'var(--text)' }}>{item.nombre} x{item.cantidad}</span>
                        {user && user.hasDuocDiscount && (
                          <div className="mt-1">
                            <small style={{
                              textDecoration: 'line-through',
                              color: 'var(--muted)',
                              fontSize: '0.85rem'
                            }}>
                              ${precioOriginal.toLocaleString()}
                            </small>
                            <span style={{
                              color: 'var(--accent)',
                              fontWeight: 'bold',
                              fontSize: '0.9rem',
                              marginLeft: '8px'
                            }}>
                              ${precioConDescuento.toLocaleString()} c/u
                            </span>
                          </div>
                        )}
                      </div>
                      <div className="text-end">
                        {user && user.hasDuocDiscount ? (
                          <>
                            <div style={{
                              textDecoration: 'line-through',
                              color: 'var(--muted)',
                              fontSize: '0.85rem'
                            }}>
                              ${totalOriginal.toLocaleString()}
                            </div>
                            <div style={{
                              color: 'var(--accent)',
                              fontWeight: 'bold',
                              fontSize: '1rem'
                            }}>
                              ${totalConDescuento.toLocaleString()}
                            </div>
                          </>
                        ) : (
                          <span style={{ color: 'var(--text)', fontWeight: 'bold' }}>
                            ${totalOriginal.toLocaleString()}
                          </span>
                        )}
                      </div>
                    </div>
                    {user && user.hasDuocDiscount && (
                      <div className="mt-2">
                        <span className="badge" style={{
                          background: 'var(--accent)',
                          color: 'black',
                          fontSize: '0.75rem',
                          fontWeight: 'bold'
                        }}>
                          💰 AHORRAS ${(totalOriginal - totalConDescuento).toLocaleString()}
                        </span>
                      </div>
                    )}
                  </div>
                );
              })}

              <hr />

              <div className="d-flex justify-content-between mb-2">
                <span>Subtotal:</span>
                <span>${subtotal.toLocaleString()}</span>
              </div>

              {user && user.hasDuocDiscount && descuentoDuoc > 0 && (
                <div className="d-flex justify-content-between mb-2">
                  <span style={{ color: 'var(--accent)' }}>Descuento DUOC (20%):</span>
                  <span style={{ color: 'var(--accent)' }}>-${descuentoDuoc.toLocaleString()}</span>
                </div>
              )}

              <div className="d-flex justify-content-between mb-2">
                <span>IVA (19%):</span>
                <span>${iva.toLocaleString()}</span>
              </div>

              <div className="d-flex justify-content-between mb-2">
                <span>Envío:</span>
                <span>${shippingCost.toLocaleString()}</span>
              </div>

              <hr />

              <div className="d-flex justify-content-between mb-3">
                <strong>Total:</strong>
                <strong className="text-success">${total.toLocaleString()}</strong>
              </div>

              <div className="alert alert-info small">
                <i className="bi bi-info-circle me-1"></i>
                Los pagos se procesan de forma segura. Tu información está protegida.
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Checkout;