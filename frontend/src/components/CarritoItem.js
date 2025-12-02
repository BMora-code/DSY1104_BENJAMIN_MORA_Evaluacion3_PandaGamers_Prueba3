import React, { useContext, useState, useEffect } from "react";
import { CartContext } from "../context/CartContext";
import { AuthContext } from "../context/AuthContext.js";

const CarritoItem = ({ item }) => {
  const { eliminarDelCarrito } = useContext(CartContext);
  const { user } = useContext(AuthContext);
  const [currentItem, setCurrentItem] = useState(item);
  const [quantityToRemove, setQuantityToRemove] = useState(1);

  // Actualizar el estado local cuando cambie el item
  useEffect(() => {
    setCurrentItem(item);
  }, [item]);

  const handleRemoveQuantity = (e) => {
    e.preventDefault();
    e.stopPropagation();
    // Remover la cantidad especificada
    for (let i = 0; i < quantityToRemove; i++) {
      eliminarDelCarrito(item.id);
    }
    // Resetear a 1 después de remover
    setQuantityToRemove(1);
  };

  const precioOriginal = Number(currentItem.price) || currentItem.precio || 0;
  const precioFinal = user && user.hasDuocDiscount ? Math.round(precioOriginal * 0.8) : precioOriginal;
  const itemTotal = (precioFinal * currentItem.cantidad).toLocaleString('es-CL', { minimumFractionDigits: 0, maximumFractionDigits: 0 }).replace(/\B(?=(\d{3})+(?!\d))/g, '.');

  return (
    <div className="d-flex align-items-center py-3" style={{ borderBottom: '1px solid var(--border)' }}>
      <img
        src={currentItem.image || currentItem.imagen}
        alt={currentItem.name || currentItem.nombre}
        className="rounded me-3"
        style={{ width: '80px', height: '80px', objectFit: 'cover' }}
        onError={(e) => {
          e.target.src = "https://via.placeholder.com/80x80/6c757d/ffffff?text=Img";
        }}
      />
      <div className="flex-grow-1">
        <div className="d-flex align-items-center mb-1">
          <h6 className="mb-0 me-2" style={{ color: 'var(--text)' }}>{currentItem.name || currentItem.nombre}</h6>
          {user && user.hasDuocDiscount && (
            <span className="badge" style={{
              background: 'var(--accent)',
              color: 'black',
              fontSize: '0.7rem',
              fontWeight: 'bold',
              padding: '2px 6px'
            }}>
              🎓 DUOC 20% OFF
            </span>
          )}
        </div>
        <div className="mb-1">
          {user && user.hasDuocDiscount ? (
            <>
              <p className="small mb-0" style={{ color: 'var(--muted)', textDecoration: 'line-through' }}>
                Precio original: ${precioOriginal.toLocaleString('es-CL', { minimumFractionDigits: 0, maximumFractionDigits: 0 }).replace(/\B(?=(\d{3})+(?!\d))/g, '.')}
              </p>
              <p className="small mb-0" style={{ color: 'var(--accent)' }}>
                🎓 Precio DUOC: ${precioFinal.toLocaleString('es-CL', { minimumFractionDigits: 0, maximumFractionDigits: 0 }).replace(/\B(?=(\d{3})+(?!\d))/g, '.')} (20% OFF)
              </p>
            </>
          ) : (
            <p className="small mb-0" style={{ color: 'var(--muted)' }}>
              Precio unitario: ${precioOriginal.toLocaleString('es-CL', { minimumFractionDigits: 0, maximumFractionDigits: 0 }).replace(/\B(?=(\d{3})+(?!\d))/g, '.')}
            </p>
          )}
        </div>
        <p className="small mb-0 d-flex align-items-center gap-2" style={{ color: 'var(--muted)' }}>
          Cantidad: {currentItem.cantidad}
          {user && user.hasDuocDiscount && (
            <span style={{ color: 'var(--accent)', fontWeight: 'bold', fontSize: '0.8rem' }}>
              💰 Ahorras: ${((precioOriginal - precioFinal) * currentItem.cantidad).toLocaleString('es-CL', { minimumFractionDigits: 0, maximumFractionDigits: 0 }).replace(/\B(?=(\d{3})+(?!\d))/g, '.')}
            </span>
          )}
          <select
            value={quantityToRemove}
            onChange={(e) => setQuantityToRemove(parseInt(e.target.value))}
            className="form-select form-select-sm"
            style={{
              width: '60px',
              fontSize: '0.75rem',
              background: 'var(--surface)',
              border: '1px solid var(--border)',
              color: 'var(--text)'
            }}
          >
            {Array.from({ length: currentItem.cantidad }, (_, i) => i + 1).map(num => (
              <option key={num} value={num}>{num}</option>
            ))}
          </select>
          <button
            className="btn btn-sm"
            onClick={handleRemoveQuantity}
            title={`Quitar ${quantityToRemove} unidad(es)`}
            style={{ border: '1px solid #FF4500', background: 'transparent', color: '#FF4500', padding: '0.125rem 0.25rem' }}
          >
            🗑️
          </button>
        </p>
      </div>
      <div className="text-end me-3">
        <strong style={{ color: 'var(--accent)' }}>${itemTotal}</strong>
      </div>
    </div>
  );
};

export default CarritoItem;
