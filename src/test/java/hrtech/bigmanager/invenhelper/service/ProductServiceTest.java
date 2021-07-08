package hrtech.bigmanager.invenhelper.service;

import hrtech.bigmanager.invenhelper.config.InvenHelperApplication;
import hrtech.bigmanager.invenhelper.model.*;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Sql(scripts = "classpath:databaseinit.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@SpringBootTest(classes = InvenHelperApplication.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductServiceTest {

    @Autowired
    private ProductService service;

    private UUID defaultDatabaseCode;
    private String defaultCode;
    private ProductKey defaultKey;

    private String defaultName;
    private String defaultDescription;
    private ProductInformation defaultInformation;

    private int defaultGoodQuantity;
    private int randomNumberToIncrease;
    private int randomNumberToDecrease;
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
        randomNumberToIncrease = random.ints(1, 1000).findFirst().getAsInt();
        randomNumberToDecrease = random.ints(0, defaultGoodQuantity).findFirst().getAsInt();
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
    void findById() {
        assertTrue(service.insert(product));
        Optional<Product> obtainedProduct = service.findById(defaultKey.getDatabaseKey());
        assertTrue(obtainedProduct.isPresent());
        assertEquals(product, obtainedProduct.get());
        assertTrue(product.sameAs(obtainedProduct.get()));
    }

    @Test
    void findByBusinessKey() {
        assertTrue(service.insert(product));
        Optional<Product> obtainedProduct = service.findByBusinessKey(defaultKey.getInternalKey());
        assertTrue(obtainedProduct.isPresent());
        assertEquals(product, obtainedProduct.get());
        assertTrue(product.sameAs(obtainedProduct.get()));
    }

    @Test
    void createNewProductValidInformation() {
        JSONObject objectToInject = new JSONObject();
        objectToInject.put("identifier", defaultCode);
        objectToInject.put("name", defaultName);
        objectToInject.put("description", defaultDescription);

        Response<Product> response = service.createNewProduct(objectToInject);
        assertTrue(response.isSuccess());
        assertEquals("Success creating the product", response.getAdditionalInformation());

        Product productObtained = response.getObjectToReturn();
        Optional<Product> productOnDatabase = service.findById(productObtained.getProductID());
        assertTrue(productOnDatabase.isPresent());
        assertEquals(productOnDatabase.get(), productObtained);
    }

    @Test
    void createNewProductNoIdentifier() {
        JSONObject objectToInject = new JSONObject();
        objectToInject.put("identifier", "");
        objectToInject.put("name", defaultName);
        objectToInject.put("description", defaultDescription);

        Response<Product> response = service.createNewProduct(objectToInject);
        assertFalse(response.isSuccess());
        assertEquals("Error converting the JSON into a Product. Check the request", response.getAdditionalInformation());
    }

    @Test
    void createNewProductNoName() {
        JSONObject objectToInject = new JSONObject();
        objectToInject.put("identifier", defaultCode);
        objectToInject.put("name", "");
        objectToInject.put("description", defaultDescription);

        Response<Product> response = service.createNewProduct(objectToInject);
        assertFalse(response.isSuccess());
        assertEquals("Error converting the JSON into a Product. Check the request", response.getAdditionalInformation());
    }

    @Test
    void createNewProductNoDescription() {
        JSONObject objectToInject = new JSONObject();
        objectToInject.put("identifier", defaultCode);
        objectToInject.put("name", defaultName);
        objectToInject.put("description", "");

        Response<Product> response = service.createNewProduct(objectToInject);
        assertFalse(response.isSuccess());
        assertEquals("Error converting the JSON into a Product. Check the request", response.getAdditionalInformation());
    }

    @Test
    void createNewProductBusinessIDAlreadyUsed() {
        JSONObject objectToInject = new JSONObject();
        objectToInject.put("identifier", defaultCode);
        objectToInject.put("name", defaultName);
        objectToInject.put("description", defaultDescription);

        Response<Product> response = service.createNewProduct(objectToInject);
        assertTrue(response.isSuccess());
        assertEquals("Success creating the product", response.getAdditionalInformation());

        Product productObtained = response.getObjectToReturn();
        Optional<Product> productOnDatabase = service.findById(productObtained.getProductID());
        assertTrue(productOnDatabase.isPresent());
        assertEquals(productOnDatabase.get(), productObtained);

        response = service.createNewProduct(objectToInject);
        assertFalse(response.isSuccess());
        assertEquals("A product with the same business identifier is already registered", response.getAdditionalInformation());
    }

    @Test
    void increaseQuantityValid() {
        assertTrue(service.insert(product));
        product.increaseQuantity(randomNumberToIncrease);
        Response<Product> methodResponse = service.increaseQuantity(defaultCode, randomNumberToIncrease);
        assertTrue(methodResponse.isSuccess());
        assertEquals(product, methodResponse.getObjectToReturn());
    }

    @Test
    void increaseQuantityNoProductFound() {
        assertTrue(service.insert(product));
        product.increaseQuantity(randomNumberToIncrease);
        Response<Product> methodResponse = service.increaseQuantity("CODE", randomNumberToIncrease);
        assertFalse(methodResponse.isSuccess());
        assertEquals("Product not found", methodResponse.getAdditionalInformation());
    }

    @Test
    void decreaseQuantityValid() {
        assertTrue(service.insert(product));
        product.decreaseQuantity(randomNumberToDecrease);
        Response<Product> methodResponse = service.decreaseQuantity(defaultCode, randomNumberToDecrease);
        assertTrue(methodResponse.isSuccess());
        assertEquals(product, methodResponse.getObjectToReturn());
    }

    @Test
    void decreaseQuantityNoProductFound() {
        assertTrue(service.insert(product));
        product.decreaseQuantity(randomNumberToDecrease);
        Response<Product> methodResponse = service.decreaseQuantity("TEST", randomNumberToDecrease);
        assertFalse(methodResponse.isSuccess());
        assertEquals("Product not found", methodResponse.getAdditionalInformation());
    }

    @Test
    void decreaseQuantityInvalidQuantity() {
        assertTrue(service.insert(product));
        product.decreaseQuantity(randomNumberToDecrease);
        Response<Product> methodResponse = service.decreaseQuantity(defaultCode, defaultGoodQuantity + 5);
        assertFalse(methodResponse.isSuccess());
        assertEquals("Invalid quantity obtained while trying to decrease", methodResponse.getAdditionalInformation());
    }

    @Test
    void updateProductInformationValidInformation() {
        JSONObject objectToInject = new JSONObject();
        String newName = generateString();
        String newDescription = generateString();
        objectToInject.put("identifier", defaultCode);
        objectToInject.put("name", newName);
        objectToInject.put("description", newDescription);

        assertTrue(service.insert(product));
        Response<Product> updateInfo = service.updateProductInformation(objectToInject);
        assertTrue(updateInfo.isSuccess());

        Optional<Product> productOnDatabase = service.findByBusinessKey(defaultCode);
        assertTrue(productOnDatabase.isPresent());
        Product productRetrieved = productOnDatabase.get();
        assertEquals(newName, productRetrieved.getName());
        assertEquals(newDescription, productRetrieved.getDescription());
    }

    @Test
    void updateProductInformationValidInformationNoIdentifier() {
        JSONObject objectToInject = new JSONObject();
        objectToInject.put("identifier", "");
        objectToInject.put("name", defaultName);
        objectToInject.put("description", defaultDescription);

        assertTrue(service.insert(product));

        Response<Product> response = service.updateProductInformation(objectToInject);
        assertFalse(response.isSuccess());
        assertEquals("Product not found", response.getAdditionalInformation());
    }

    @Test
    void updateProductInformationValidInformationNoInfoToChange() {
        JSONObject objectToInject = new JSONObject();
        objectToInject.put("identifier", defaultCode);

        assertTrue(service.insert(product));

        Response<Product> response = service.updateProductInformation(objectToInject);
        assertTrue(response.isSuccess());
        assertEquals("No information to update product", response.getAdditionalInformation());

        Optional<Product> productOnDatabase = service.findByBusinessKey(defaultCode);
        assertTrue(productOnDatabase.isPresent());
        Product productRetrieved = productOnDatabase.get();
        assertEquals(defaultName, productRetrieved.getName());
        assertEquals(defaultDescription, productRetrieved.getDescription());
    }

    @Test
    void updateProductInformationValidInformationInvalidName() {
        JSONObject objectToInject = new JSONObject();
        objectToInject.put("identifier", defaultCode);
        objectToInject.put("name", "");
        objectToInject.put("description", "Test");

        assertTrue(service.insert(product));

        Response<Product> response = service.updateProductInformation(objectToInject);
        assertFalse(response.isSuccess());
        assertEquals("Invalid product name: ", response.getAdditionalInformation());

        Optional<Product> productOnDatabase = service.findByBusinessKey(defaultCode);
        assertTrue(productOnDatabase.isPresent());
        Product productRetrieved = productOnDatabase.get();
        assertEquals(defaultName, productRetrieved.getName());
        assertEquals(defaultDescription, productRetrieved.getDescription());
    }

    @Test
    void updateProductInformationValidInformationInvalidDescription() {
        JSONObject objectToInject = new JSONObject();
        objectToInject.put("identifier", defaultCode);
        objectToInject.put("name", defaultName);
        objectToInject.put("description", "");

        assertTrue(service.insert(product));

        Response<Product> response = service.updateProductInformation(objectToInject);
        assertFalse(response.isSuccess());
        assertEquals("Invalid product description: ", response.getAdditionalInformation());

        Optional<Product> productOnDatabase = service.findByBusinessKey(defaultCode);
        assertTrue(productOnDatabase.isPresent());
        Product productRetrieved = productOnDatabase.get();
        assertEquals(defaultName, productRetrieved.getName());
        assertEquals(defaultDescription, productRetrieved.getDescription());
    }
}