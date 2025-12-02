package com.example.backend.config;

import com.example.backend.model.Oferta;
import com.example.backend.model.Order;
import com.example.backend.model.OrderItem;
import com.example.backend.model.Product;
import com.example.backend.model.Review;
import com.example.backend.model.Role;
import com.example.backend.model.ShippingInfo;
import com.example.backend.model.User;
import com.example.backend.repository.OfertaRepository;
import com.example.backend.repository.OrderRepository;
import com.example.backend.repository.ProductRepository;
import com.example.backend.repository.ReviewRepository;
import com.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Configuration
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OfertaRepository ofertaRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create admin user if not present
        if (!userRepository.existsByEmail("admin@admin.com")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@admin.com");
            admin.setPassword(passwordEncoder.encode("Admin123"));
            HashSet<Role> roles = new HashSet<>();
            roles.add(Role.ADMIN);
            admin.setRoles(roles);
            userRepository.save(admin);
            System.out.println("Admin user created: admin@admin.com / Admin123");
        }

        // Create sample users if not present
        if (userRepository.count() <= 1) { // Only admin
            createSampleUsers();
            System.out.println("Sample users created");
        }

        // Force create sample users for testing
        if (!userRepository.existsByUsername("juan")) {
            createSampleUsers();
            System.out.println("Sample users force created");
        }

        // Initialize products if not present
        if (productRepository.count() == 0) {
            initializeProducts();
            System.out.println("Products initialized");
        } else {
            // For development: reinitialize if no products with categories
            boolean hasCategorizedProducts = productRepository.findAll().stream()
                .anyMatch(p -> p.getCategory() != null && !p.getCategory().isEmpty());
            if (!hasCategorizedProducts) {
                productRepository.deleteAll();
                initializeProducts();
                System.out.println("Products reinitialized");
            }
        }

        // Initialize offers if not present
        if (ofertaRepository.count() == 0) {
            initializeOfertas();
            System.out.println("Offers initialized");
        }

        // Initialize reviews if not present
        if (reviewRepository.count() == 0) {
            initializeReviews();
            System.out.println("Reviews initialized");
        }

        // Initialize sample orders if not present
        if (orderRepository.count() == 0) {
            initializeOrders();
            System.out.println("Sample orders initialized");
        }
    }

    private void initializeProducts() {
        List<Product> products = List.of(
            // Accesorios
            createProduct("1", "Auriculares HyperX", "Auriculares gaming de alta calidad con sonido inmersivo.", BigDecimal.valueOf(79990), 15, "/images/Accesorios/Auriculares HyperX.webp", "Accesorios"),
            createProduct("2", "Control Inalámbrico", "Control inalámbrico para consolas, cómodo y preciso.", BigDecimal.valueOf(59990), 20, "/images/Accesorios/Control inalámbrico.jpg", "Accesorios"),
            createProduct("3", "Mousepad RGB", "Mousepad con iluminación RGB para setups gaming.", BigDecimal.valueOf(29990), 25, "/images/Accesorios/Mousepad RGB.webp", "Accesorios"),
            createProduct("4", "Teclado Razer", "Teclado mecánico RGB con switches ópticos.", BigDecimal.valueOf(149990), 10, "/images/Accesorios/Teclado Razer.webp", "Accesorios"),
            // Consolas
            createProduct("5", "Nintendo Switch", "Consola híbrida para gaming en casa o movilidad.", BigDecimal.valueOf(349990), 8, "/images/Consolas/Nintendo Switch.png", "Consolas"),
            createProduct("6", "PlayStation 4 Pro", "Consola de última generación con 4K HDR.", BigDecimal.valueOf(399990), 5, "/images/Consolas/PlayStation 4 Pro.avif", "Consolas"),
            createProduct("7", "PlayStation 5", "La consola más potente con ray tracing y SSD ultra rápido.", BigDecimal.valueOf(599990), 3, "/images/Consolas/PlayStation 5.webp", "Consolas"),
            createProduct("8", "Xbox Series X", "Consola Xbox de nueva generación con 4K gaming.", BigDecimal.valueOf(549990), 4, "/images/Consolas/Xbox Series X.jpg", "Consolas"),
            // Juegos de mesa
            createProduct("9", "Carcassonne", "Juego de estrategia medieval para construir ciudades.", BigDecimal.valueOf(49990), 12, "/images/Juegos de mesa/Carcassonne.jpg", "Juegos de mesa"),
            createProduct("10", "Catan", "Juego de colonización y comercio en una isla.", BigDecimal.valueOf(59990), 10, "/images/Juegos de mesa/Catán.webp", "Juegos de mesa"),
            createProduct("11", "Monopoly", "Clásico juego de propiedades y negocios.", BigDecimal.valueOf(39990), 15, "/images/Juegos de mesa/Monopoly.jpg", "Juegos de mesa"),
            createProduct("12", "Risk", "Juego de estrategia global de conquista territorial.", BigDecimal.valueOf(54990), 8, "/images/Juegos de mesa/Risk.jpg", "Juegos de mesa"),
            // Mouses
            createProduct("13", "HyperX Pulsefire", "Mouse gaming ergonómico con sensor óptico preciso.", BigDecimal.valueOf(69990), 18, "/images/Mouses/HyperX Pulsefire.webp", "Mouses"),
            createProduct("14", "Logitech G502", "Mouse gaming con 11 botones programables.", BigDecimal.valueOf(89990), 14, "/images/Mouses/Logitech G502.webp", "Mouses"),
            createProduct("15", "Razer DeathAdder", "Mouse ergonómico con sensor óptico de 16,000 DPI.", BigDecimal.valueOf(79990), 16, "/images/Mouses/Razer DeathAdder.webp", "Mouses"),
            createProduct("16", "SteelSeries Rival 3", "Mouse gaming ligero con iluminación RGB.", BigDecimal.valueOf(64990), 20, "/images/Mouses/SteelSeries Rival 3 –.webp", "Mouses"),
            // Pc Gamers
            createProduct("17", "PC Alienware", "PC gaming de alto rendimiento con RTX 3080.", BigDecimal.valueOf(1999990), 2, "/images/Pc Gamers/PC Alienware.webp", "Pc Gamers"),
            createProduct("18", "PC ASUS ROG Strix", "PC gaming con componentes premium y RGB.", BigDecimal.valueOf(1799990), 3, "/images/Pc Gamers/PC ASUS ROG Strix.png", "Pc Gamers"),
            createProduct("19", "PC HP Omen", "PC gaming equilibrado para gaming competitivo.", BigDecimal.valueOf(1499990), 4, "/images/Pc Gamers/PC HP Omen.jpg", "Pc Gamers"),
            createProduct("20", "PC MSI Gaming", "PC gaming con enfriamiento avanzado.", BigDecimal.valueOf(1699990), 3, "/images/Pc Gamers/PC MSI Gaming.jpg", "Pc Gamers"),
            // Poleras
            createProduct("21", "Polera Gamer 1", "Polera cómoda para gamers con diseño único.", BigDecimal.valueOf(29990), 30, "/images/Poleras/640 (1).webp", "Poleras"),
            createProduct("22", "Polera Gamer 2", "Polera con estampado de juegos.", BigDecimal.valueOf(34990), 25, "/images/Poleras/3396_1.png", "Poleras"),
            createProduct("23", "Polera God of War", "Polera inspirada en God of War.", BigDecimal.valueOf(39990), 20, "/images/Poleras/PLR-GOW.jpg", "Poleras"),
            createProduct("24", "Polera Papa Gamer", "Polera divertida para papás gamers.", BigDecimal.valueOf(32990), 22, "/images/Poleras/polera-papa-de-dia-gamer-de-noche.jpg", "Poleras"),
            // Polerones
            createProduct("25", "Polerón Gamer 1", "Polerón abrigado para sesiones largas de gaming.", BigDecimal.valueOf(59990), 15, "/images/Polerones/1132_9.png", "Polerones"),
            createProduct("26", "Polerón Gamer 2", "Polerón con capucha y diseño moderno.", BigDecimal.valueOf(64990), 12, "/images/Polerones/9704_9.png", "Polerones"),
            createProduct("27", "Polerón Smash Bros Vintage", "Polerón inspirado en Super Smash Bros con colores vintage.", BigDecimal.valueOf(69990), 10, "/images/Polerones/poleron-smash-bros-vintage-colors.jpg", "Polerones"),
            createProduct("28", "Polerón Smash Ultimate", "Polerón de Super Smash Bros Ultimate.", BigDecimal.valueOf(74990), 8, "/images/Polerones/poleron-smash-ultimate-2.jpg", "Polerones"),
            // Portamouse
            createProduct("29", "HyperX Fury S", "Portamouse gaming con diseño ergonómico.", BigDecimal.valueOf(39990), 18, "/images/Portamouse/HyperX Fury S.avif", "Portamouse"),
            createProduct("30", "Logitech G640", "Portamouse de tela para precisión máxima.", BigDecimal.valueOf(49990), 16, "/images/Portamouse/Logitech G640.jpg", "Portamouse"),
            createProduct("31", "Razer Goliathus", "Portamouse con superficie de control óptima.", BigDecimal.valueOf(44990), 20, "/images/Portamouse/Razer Goliathus.png", "Portamouse"),
            createProduct("32", "SteelSeries QcK", "Portamouse profesional para esports.", BigDecimal.valueOf(52990), 14, "/images/Portamouse/SteelSeries QcK.jpg", "Portamouse"),
            // Sillas
            createProduct("33", "Silla Cougar", "Silla gaming ergonómica con soporte lumbar.", BigDecimal.valueOf(299990), 5, "/images/Sillas/Silla Cougar.webp", "Sillas"),
            createProduct("34", "Silla DXRacer", "Silla premium para gaming con ajuste completo.", BigDecimal.valueOf(399990), 4, "/images/Sillas/Silla DXRacer.jpg", "Sillas"),
            createProduct("35", "Silla GT Omega", "Silla gaming cómoda con diseño moderno.", BigDecimal.valueOf(349990), 6, "/images/Sillas/Silla GT Omega.jpg", "Sillas"),
            createProduct("36", "Silla SecretLab", "Silla de alta gama con materiales premium.", BigDecimal.valueOf(499990), 3, "/images/Sillas/Silla SecretLab.webp", "Sillas")
        );

        productRepository.saveAll(products);
    }

    private void createSampleUsers() {
        List<User> users = List.of(
            createUser("juan", "juan@example.com", "password123", Role.CLIENTE),
            createUser("maria", "maria@example.com", "password123", Role.CLIENTE),
            createUser("carlos", "carlos@example.com", "password123", Role.CLIENTE)
        );
        userRepository.saveAll(users);
    }

    private User createUser(String username, String email, String password, Role role) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        HashSet<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);
        return user;
    }

    private void initializeOfertas() {
        List<Oferta> ofertas = List.of(
            new Oferta("Descuento en Accesorios", "20% de descuento en todos los accesorios gaming", 20.0, "/images/oferta-accesorios.jpg"),
            new Oferta("Oferta en Consolas", "15% off en consolas PlayStation y Xbox", 15.0, "/images/oferta-consolas.jpg"),
            new Oferta("Promoción Pc Gamers", "10% de descuento en PCs gaming de alto rendimiento", 10.0, "/images/oferta-pcs.jpg")
        );
        ofertaRepository.saveAll(ofertas);
    }

    private void initializeReviews() {
        List<Review> reviews = List.of(
            new Review("1", "Juan Pérez", "juan@example.com", 5, "Excelente producto, muy recomendado"),
            new Review("2", "María García", "maria@example.com", 4, "Buena calidad, llegó rápido"),
            new Review("5", "Carlos López", "carlos@example.com", 5, "Increíble consola, vale cada peso")
        );
        reviewRepository.saveAll(reviews);
    }

    private void initializeOrders() {
        List<Order> orders = List.of(
            // Orden completada de Juan
            createSampleOrder("juan", "completed", 79990.0, 6399.2, 2500.0, 87189.2,
                List.of(
                    createOrderItem("1", "Auriculares HyperX", 79990.0, 1, "/images/Accesorios/Auriculares HyperX.webp")
                ),
                createShippingInfo("Juan", "Pérez", "juan@example.com", "+56912345678",
                    "Av. Providencia 123", "Santiago", "Metropolitana", "7500000", "standard"),
                Instant.now().minusSeconds(86400 * 7) // 7 días atrás
            ),

            // Orden completada de María
            createSampleOrder("maria", "completed", 349990.0, 27999.2, 2500.0, 397489.2,
                List.of(
                    createOrderItem("5", "Nintendo Switch", 349990.0, 1, "/images/Consolas/Nintendo Switch.png")
                ),
                createShippingInfo("María", "García", "maria@example.com", "+56987654321",
                    "Calle Las Condes 456", "Las Condes", "Metropolitana", "7550000", "express"),
                Instant.now().minusSeconds(86400 * 3) // 3 días atrás
            ),

            // Orden pendiente de Carlos
            createSampleOrder("carlos", "pending", 149990.0, 11999.2, 5000.0, 167989.2,
                List.of(
                    createOrderItem("4", "Teclado Razer", 149990.0, 1, "/images/Accesorios/Teclado Razer.webp")
                ),
                createShippingInfo("Carlos", "López", "carlos@example.com", "+56911223344",
                    "Paseo Ahumada 789", "Valparaíso", "Valparaíso", "2340000", "pickup"),
                Instant.now().minusSeconds(86400) // 1 día atrás
            ),

            // Orden completada con múltiples productos
            createSampleOrder("juan", "completed", 169980.0, 13598.4, 2500.0, 187078.4,
                List.of(
                    createOrderItem("13", "HyperX Pulsefire", 69990.0, 1, "/images/Mouses/HyperX Pulsefire.webp"),
                    createOrderItem("14", "Logitech G502", 89990.0, 1, "/images/Mouses/Logitech G502.webp"),
                    createOrderItem("3", "Mousepad RGB", 29990.0, 1, "/images/Accesorios/Mousepad RGB.webp")
                ),
                createShippingInfo("Juan", "Pérez", "juan@example.com", "+56912345678",
                    "Av. Apoquindo 234", "Las Condes", "Metropolitana", "7550000", "standard"),
                Instant.now().minusSeconds(86400 * 14) // 14 días atrás
            ),

            // Orden fallida por fondos insuficientes
            createSampleOrder("maria", "failed", 599990.0, 47999.2, 2500.0, 649489.2,
                List.of(
                    createOrderItem("7", "PlayStation 5", 599990.0, 1, "/images/Consolas/PlayStation 5.webp")
                ),
                createShippingInfo("María", "García", "maria@example.com", "+56987654321",
                    "Calle Los Leones 567", "Providencia", "Metropolitana", "7500000", "standard"),
                Instant.now().minusSeconds(86400 * 2) // 2 días atrás
            ),

            // Orden cancelada por tarjeta rechazada
            createSampleOrder("carlos", "cancelled", 399990.0, 31999.2, 2500.0, 433489.2,
                List.of(
                    createOrderItem("6", "PlayStation 4 Pro", 399990.0, 1, "/images/Consolas/PlayStation 4 Pro.avif")
                ),
                createShippingInfo("Carlos", "López", "carlos@example.com", "+56911223344",
                    "Av. Pedro de Valdivia 890", "Ñuñoa", "Metropolitana", "7750000", "express"),
                Instant.now().minusSeconds(86400 * 5) // 5 días atrás
            ),

            // Orden fallida por datos inválidos
            createSampleOrder("juan", "failed", 299990.0, 23999.2, 5000.0, 327989.2,
                List.of(
                    createOrderItem("34", "Silla DXRacer", 299990.0, 1, "/images/Sillas/Silla DXRacer.jpg")
                ),
                createShippingInfo("Juan", "Pérez", "juan@example.com", "+56912345678",
                    "Paseo Las Palmas 123", "Vitacura", "Metropolitana", "7630000", "pickup"),
                Instant.now().minusSeconds(86400 * 10) // 10 días atrás
            )
        );

        orderRepository.saveAll(orders);
    }

    private Product createProduct(String id, String name, String description, BigDecimal price, Integer stock, String imageUrl, String category) {
        Product p = new Product(name, description, price, stock, imageUrl, category);
        p.setId(id);
        return p;
    }

    private Order createSampleOrder(String userId, String status, double subtotal, double iva, double shippingCost, double total,
                                   List<OrderItem> items, ShippingInfo shippingInfo, Instant createdAt) {
        Order order = new Order();
        order.setUserId(userId);
        order.setItems(items);
        order.setSubtotal(subtotal);
        order.setIva(iva);
        order.setShippingCost(shippingCost);
        order.setTotal(total);
        order.setStatus(status);
        order.setDeliveryOption(shippingInfo.getDeliveryOption() != null ? shippingInfo.getDeliveryOption() : "standard");
        order.setShippingInfo(shippingInfo);
        order.setCreatedAt(createdAt);
        return order;
    }

    private OrderItem createOrderItem(String productId, String name, double price, int quantity, String image) {
        OrderItem item = new OrderItem();
        item.setProductId(productId);
        item.setName(name);
        item.setPrice(price);
        item.setQuantity(quantity);
        item.setImage(image);
        return item;
    }

    private ShippingInfo createShippingInfo(String firstName, String lastName, String email, String phone,
                                           String address, String city, String region, String postalCode, String deliveryOption) {
        ShippingInfo info = new ShippingInfo();
        info.setFirstName(firstName);
        info.setLastName(lastName);
        info.setEmail(email);
        info.setPhone(phone);
        info.setAddress(address);
        info.setCity(city);
        info.setRegion(region);
        info.setPostalCode(postalCode);
        info.setDeliveryOption(deliveryOption);
        return info;
    }
}
