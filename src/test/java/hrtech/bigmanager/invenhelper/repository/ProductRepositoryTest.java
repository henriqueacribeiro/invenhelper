package hrtech.bigmanager.invenhelper.repository;

import hrtech.bigmanager.invenhelper.config.InvenHelperApplication;
import hrtech.bigmanager.invenhelper.model.Product;
import hrtech.bigmanager.invenhelper.model.ProductInformation;
import hrtech.bigmanager.invenhelper.model.ProductKey;
import hrtech.bigmanager.invenhelper.model.Quantity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Sql(scripts = "classpath:databaseinit.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@SpringBootTest(classes = InvenHelperApplication.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryTest {

    @Autowired
    private ProductRepository repository;

    private UUID defaultDatabaseCode;
    private String defaultCode;
    private ProductKey defaultKey;
    private String defaultName;
    private String defaultDescription;
    private ProductInformation defaultInformation;
    private int defaultGoodQuantity;
    private Quantity defaultQuantity;
    private Product product;

    private String generateString() {
        //Baeldung Random String generation
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    @BeforeEach
    void setUp() {
        Random random = new Random();
        defaultGoodQuantity = random.ints(1, 1000).findFirst().getAsInt();
        defaultQuantity = new Quantity(defaultGoodQuantity);

        defaultName = generateString();
        defaultDescription = generateString();
        defaultInformation = new ProductInformation(defaultName, defaultDescription);

        defaultCode = generateString();
        defaultDatabaseCode = UUID.randomUUID();
        defaultKey = new ProductKey(defaultDatabaseCode, defaultCode);

        product = new Product(defaultKey, defaultInformation, defaultQuantity);
    }

    @Test
    void save() {
        assertTrue(repository.insert(product));
        Optional<Product> obtainedProduct = repository.findById(defaultKey);
        assertTrue(obtainedProduct.isPresent());
        assertEquals(product, obtainedProduct.get());

        product.increaseQuantity(4);
        assertTrue(repository.save(product));
        obtainedProduct = repository.findById(defaultKey);
        assertTrue(obtainedProduct.isPresent());
        assertEquals(product, obtainedProduct.get());
        assertTrue(product.sameAs(obtainedProduct.get()));
    }

    @Test
    void findById() {
        assertTrue(repository.insert(product));
        Optional<Product> obtainedProduct = repository.findById(defaultKey);
        assertTrue(obtainedProduct.isPresent());
        assertEquals(product, obtainedProduct.get());
        assertTrue(product.sameAs(obtainedProduct.get()));
    }

    @Test
    void findByBusinessId() {
        assertTrue(repository.insert(product));
        Optional<Product> obtainedProduct = repository.findByBusinessId(defaultKey);
        assertTrue(obtainedProduct.isPresent());
        assertEquals(product, obtainedProduct.get());
        assertTrue(product.sameAs(obtainedProduct.get()));
    }
}