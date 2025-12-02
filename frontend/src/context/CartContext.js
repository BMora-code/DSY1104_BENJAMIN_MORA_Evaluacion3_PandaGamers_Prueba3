import React, { createContext, useState, useEffect, useCallback, useContext } from 'react';
import { AuthContext } from './AuthContext.js';

export const CartContext = createContext({
  cart: [],
  addToCart: () => {},
  agregarAlCarrito: () => {},
  removeFromCart: () => {},
  eliminarDelCarrito: () => {},
  updateQuantity: () => {},
  actualizarCantidad: () => {},
  clearCart: () => {},
  getTotal: () => 0
});

// Función para obtener la key del carrito específica del usuario
const getCartStorageKey = (userId) => userId ? `cart_${userId}` : 'cart_guest';

const loadCart = (userId) => {
  try {
    const key = getCartStorageKey(userId);
    const raw = localStorage.getItem(key);
    return raw ? JSON.parse(raw) : [];
  } catch (e) {
    return [];
  }
};

export const CartProvider = ({ children }) => {
  const { user } = useContext(AuthContext);
  const userId = user?.username || null;

  const [cart, setCart] = useState(() => loadCart(userId));

  // Cambiar carrito cuando el usuario cambia
  useEffect(() => {
    setCart(loadCart(userId));
  }, [userId]);

  // Persistir cart cuando cambie
  useEffect(() => {
    try {
      const key = getCartStorageKey(userId);
      localStorage.setItem(key, JSON.stringify(cart));
    } catch (e) { /* ignore */ }
  }, [cart, userId]);

  // Sincronizar entre pestañas (solo para el usuario actual)
  useEffect(() => {
    const onStorage = (e) => {
      const currentKey = getCartStorageKey(userId);
      if (e.key === currentKey) {
        try {
          setCart(e.newValue ? JSON.parse(e.newValue) : []);
        } catch (err) {
          setCart([]);
        }
      }
    };
    window.addEventListener('storage', onStorage);
    return () => window.removeEventListener('storage', onStorage);
  }, [userId]);

  const addToCartInternal = useCallback((product, cantidad = 1) => {
    setCart(prev => {
      const exists = prev.find(item => item.id === product.id);
      if (exists) {
        return prev.map(item =>
          item.id === product.id ? { ...item, cantidad: (Number(item.cantidad) || 0) + Number(cantidad) } : item
        );
      }
      const newItem = { ...product, cantidad: Number(cantidad) || 1 };
      return [...prev, newItem];
    });
  }, []);

  // Eliminar completamente un item del carrito (borrar todo)
  const removeFromCart = useCallback((id) => {
    setCart(prev => prev.filter(item => item.id !== id));
  }, []);

  // Eliminar N unidades de un item. Si la cantidad resultante es 0, se elimina el item.
  const removeUnitsFromCart = useCallback((id, cantidad = 1) => {
    setCart(prev => {
      return prev.reduce((acc, item) => {
        if (item.id !== id) {
          acc.push(item);
          return acc;
        }
        const current = Number(item.cantidad) || 0;
        const newQty = Math.max(0, current - Number(cantidad || 1));
        if (newQty > 0) {
          acc.push({ ...item, cantidad: newQty });
        }
        return acc;
      }, []);
    });
  }, []);

  const updateQuantity = useCallback((id, cantidad) => {
    setCart(prev =>
      prev.map(item => (item.id === id ? { ...item, cantidad: Math.max(0, Number(cantidad) || 0) } : item))
    );
  }, []);

  const clearCart = useCallback(() => setCart([]), []);

  const getTotal = useCallback(() =>
    cart.reduce((sum, item) => sum + (Number(item.price) || item.precio || 0) * (Number(item.cantidad) || 0), 0)
  , [cart]);

  // Aliases en español para compatibilidad con la base de código
  const addToCart = addToCartInternal;
  const agregarAlCarrito = addToCartInternal;
  // eliminarDelCarrito ahora reduce la cantidad (por defecto 1). Para eliminar todo usar removeFromCart.
  const eliminarDelCarrito = (id, cantidad = 1) => removeUnitsFromCart(id, cantidad);
  const actualizarCantidad = (id, cantidad) => updateQuantity(id, cantidad);

  return (
    <CartContext.Provider value={{
      cart,
      addToCart,
      agregarAlCarrito,
      removeFromCart,
      eliminarDelCarrito,
      updateQuantity,
      actualizarCantidad,
      clearCart,
      getTotal
    }}>
      {children}
    </CartContext.Provider>
  );
};

export default CartContext;
