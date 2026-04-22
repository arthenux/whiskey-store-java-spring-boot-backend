package com.alan.whiskey_store_java_spring_boot_backend.config;

import com.alan.whiskey_store_java_spring_boot_backend.entity.Product;
import com.alan.whiskey_store_java_spring_boot_backend.entity.UserAccount;
import com.alan.whiskey_store_java_spring_boot_backend.entity.enums.Role;
import com.alan.whiskey_store_java_spring_boot_backend.repository.ProductRepository;
import com.alan.whiskey_store_java_spring_boot_backend.repository.UserAccountRepository;
import com.alan.whiskey_store_java_spring_boot_backend.service.SlugUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Component
public class DataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final ProductRepository productRepository;
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminEmail;
    private final String adminPassword;
    private final String userEmail;
    private final String userPassword;

    public DataInitializer(
            ProductRepository productRepository,
            UserAccountRepository userAccountRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.seed.admin-email:admin@whiskeystore.com}") String adminEmail,
            @Value("${app.seed.admin-password:}") String adminPassword,
            @Value("${app.seed.user-email:user@whiskeystore.com}") String userEmail,
            @Value("${app.seed.user-password:}") String userPassword
    ) {
        this.productRepository = productRepository;
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
    }

    @Override
    public void run(ApplicationArguments args) {
        seedUsers();
        seedProducts();
    }

    private void seedUsers() {
        if (userAccountRepository.count() > 0) {
            return;
        }
        String resolvedAdminPassword = resolveSeedPassword(adminPassword, adminEmail, "admin");
        String resolvedUserPassword = resolveSeedPassword(userPassword, userEmail, "customer");
        userAccountRepository.saveAll(List.of(
                createUser("Admin", "User", adminEmail, resolvedAdminPassword, Role.ADMIN),
                createUser("Sample", "Customer", userEmail, resolvedUserPassword, Role.USER)
        ));
    }

    private String resolveSeedPassword(String configuredPassword, String email, String label) {
        if (configuredPassword != null && !configuredPassword.isBlank()) {
            log.info("Seeded {} account '{}' using password from configuration.", label, email);
            return configuredPassword;
        }

        String generatedPassword = UUID.randomUUID() + "A1!";
        log.info("Seeded {} account '{}' with generated password: {}", label, email, generatedPassword);
        return generatedPassword;
    }

    private void seedProducts() {
        if (productRepository.count() > 0) {
            return;
        }

        productRepository.saveAll(List.of(
                product(
                        "Highland Ember 12",
                        "Single Malt",
                        "Highlands",
                        "Ben Calder",
                        "12 Years",
                        new BigDecimal("46.00"),
                        new BigDecimal("68.00"),
                        "#c8862b",
                        "Heather honey, orchard fruit and a polished oak finish.",
                        "A rounded Highland single malt matured in first-fill bourbon casks and finished in toasted virgin oak.",
                        "Honeycomb, baked apple, vanilla pod and warm nutmeg.",
                        true
                ),
                product(
                        "Islay Smoke Reserve",
                        "Peated Malt",
                        "Islay",
                        "Kilmara",
                        "10 Years",
                        new BigDecimal("48.00"),
                        new BigDecimal("82.00"),
                        "#c46a1f",
                        "Bonfire smoke wrapped around citrus peel and salted caramel.",
                        "A maritime Islay dram with dense peat smoke, coastal salinity and a slow peppery finish.",
                        "Smoked orange, sea salt, toffee and cracked black pepper.",
                        true
                ),
                product(
                        "Speyside Orchard Cask",
                        "Single Malt",
                        "Speyside",
                        "Glen Rowan",
                        "15 Years",
                        new BigDecimal("43.00"),
                        new BigDecimal("95.00"),
                        "#d39b43",
                        "Elegant, fruit-driven Speyside whisky with a silkier finish.",
                        "Matured in ex-bourbon barrels and sherry hogsheads for layered orchard fruit and gentle spice.",
                        "Poached pear, almond cream, sultana and cinnamon.",
                        true
                ),
                product(
                        "Campbeltown Harbour Blend",
                        "Blended Malt",
                        "Campbeltown",
                        "Harbour House",
                        "NAS",
                        new BigDecimal("44.00"),
                        new BigDecimal("58.00"),
                        "#b97b30",
                        "Briny, oily and gently smoky with a long coastal finish.",
                        "A small-batch blend of Campbeltown malts that leans into sea spray, malt loaf and subtle kiln smoke.",
                        "Malt biscuit, sea breeze, orange oils and charred oak.",
                        false
                ),
                product(
                        "Bourbon Barrel Select",
                        "American Whiskey",
                        "Kentucky",
                        "Blue Ash",
                        "8 Years",
                        new BigDecimal("50.00"),
                        new BigDecimal("54.00"),
                        "#ad6a22",
                        "Rich caramel and toasted pecan with a warming spice lift.",
                        "A full-bodied American whiskey aged in heavily charred oak for dense sweetness and clove spice.",
                        "Caramel fudge, pecan pie, clove and toasted coconut.",
                        false
                ),
                product(
                        "Japanese Mizunara Finish",
                        "Japanese Whisky",
                        "Honshu",
                        "Tsuki Distillery",
                        "NAS",
                        new BigDecimal("47.00"),
                        new BigDecimal("110.00"),
                        "#cf9e4f",
                        "Incense, sandalwood and stone fruit in a refined profile.",
                        "A delicate Japanese whisky with a finishing period in mizunara oak for fragrant spice and elegance.",
                        "Apricot, sandalwood, white pepper and delicate oak.",
                        true
                )
        ));
    }

    private UserAccount createUser(String firstName, String lastName, String email, String password, Role role) {
        UserAccount user = new UserAccount();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(role);
        return user;
    }

    private Product product(
            String name,
            String category,
            String region,
            String distillery,
            String ageStatement,
            BigDecimal abv,
            BigDecimal price,
            String accentColor,
            String shortDescription,
            String longDescription,
            String tastingNotes,
            boolean featured
    ) {
        Product product = new Product();
        product.setName(name);
        product.setSlug(SlugUtils.slugify(name));
        product.setCategory(category);
        product.setRegion(region);
        product.setDistillery(distillery);
        product.setAgeStatement(ageStatement);
        product.setAbv(abv);
        product.setPrice(price);
        product.setImageUrl(buildBottleImage(name, accentColor));
        product.setShortDescription(shortDescription);
        product.setLongDescription(longDescription);
        product.setTastingNotes(tastingNotes);
        product.setFeatured(featured);
        product.setActive(true);
        return product;
    }

    private String buildBottleImage(String title, String accentColor) {
        String svg = """
                <svg xmlns='http://www.w3.org/2000/svg' width='720' height='900' viewBox='0 0 720 900'>
                  <defs>
                    <linearGradient id='bg' x1='0' y1='0' x2='1' y2='1'>
                      <stop offset='0%' stop-color='#18110d'/>
                      <stop offset='100%' stop-color='#312015'/>
                    </linearGradient>
                    <linearGradient id='glass' x1='0' y1='0' x2='0' y2='1'>
                      <stop offset='0%' stop-color='__ACCENT__'/>
                      <stop offset='100%' stop-color='#6f3f12'/>
                    </linearGradient>
                  </defs>
                  <rect width='720' height='900' fill='url(#bg)'/>
                  <circle cx='120' cy='110' r='120' fill='rgba(255,255,255,0.05)'/>
                  <circle cx='620' cy='760' r='170' fill='rgba(255,255,255,0.03)'/>
                  <rect x='300' y='110' width='120' height='120' rx='28' fill='#d7c1a1'/>
                  <rect x='255' y='210' width='210' height='490' rx='80' fill='url(#glass)'/>
                  <rect x='280' y='270' width='160' height='180' rx='18' fill='#f5e1bf'/>
                  <text x='360' y='335' fill='#2b1d14' font-size='34' text-anchor='middle' font-family='Georgia, serif'>WHISKEY</text>
                  <text x='360' y='385' fill='#2b1d14' font-size='28' text-anchor='middle' font-family='Georgia, serif'>STORE</text>
                  <text x='360' y='790' fill='#f9e5c7' font-size='34' text-anchor='middle' font-family='Georgia, serif'>__TITLE__</text>
                  <text x='360' y='840' fill='#d8b27a' font-size='20' text-anchor='middle' font-family='Arial, sans-serif'>Curated whisky selection</text>
                </svg>
                """
                .replace("__ACCENT__", accentColor)
                .replace("__TITLE__", escapeXml(title));
        return "data:image/svg+xml;charset=UTF-8," + URLEncoder.encode(svg, StandardCharsets.UTF_8);
    }

    private String escapeXml(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
