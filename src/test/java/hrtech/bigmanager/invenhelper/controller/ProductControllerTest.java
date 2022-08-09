package hrtech.bigmanager.invenhelper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hrtech.bigmanager.invenhelper.config.InvenHelperApplication;
import hrtech.bigmanager.invenhelper.model.*;
import hrtech.bigmanager.invenhelper.service.ProductService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {InvenHelperApplication.class, ProductController.class})
@AutoConfigureMockMvc
@Sql(scripts = "classpath:databaseinit.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@TestPropertySource(locations = {"classpath:application.properties"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ProductControllerTest {

    private final ObjectMapper mapper = new ObjectMapper();
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
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ProductController productController;
    @Autowired
    private ProductService productService;

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
    void getBusinessIdentifiers() throws Exception {
        MvcResult requestResponse = mvc.perform(get("/product/getAllIdentifiers")).andExpect(status().is2xxSuccessful()).andReturn();
        JSONArray response = new JSONArray(requestResponse.getResponse().getContentAsString());
        assertTrue(response.isEmpty());

        assertTrue(productService.insert(product));

        requestResponse = mvc.perform(get("/product/getAllIdentifiers")).andExpect(status().is2xxSuccessful()).andReturn();
        response = new JSONArray(requestResponse.getResponse().getContentAsString());
        assertEquals(new JSONArray(Arrays.asList(product.getProductBusinessKey())).toList(), response.toList());
    }

    @Test
    void getProductByBusinessIdentifier() throws Exception {
        JSONObject notFound = new JSONObject();
        notFound.put("success", false);
        notFound.put("information", "Product not found");

        MvcResult requestResponse = mvc.perform(get("/product/getByID").param("identifier", "")).andExpect(status().is4xxClientError()).andReturn();
        JSONObject response = new JSONObject(requestResponse.getResponse().getContentAsString());
        assertEquals(notFound.toString(), response.toString());

        assertTrue(productService.insert(product));

        requestResponse = mvc.perform(get("/product/getByID").param("identifier", product.getProductBusinessKey())).andExpect(status().is2xxSuccessful()).andReturn();
        JSONObject otherResponse = new JSONObject(requestResponse.getResponse().getContentAsString());
        assertEquals(product.convertToJSON().toString(), otherResponse.toString());
    }

    @Test
    void createProduct() throws Exception {
        JSONObject objectToInject = new JSONObject();
        objectToInject.put("identifier", defaultCode);
        objectToInject.put("name", defaultName);
        objectToInject.put("description", defaultDescription);
        objectToInject.put("quantity", defaultQuantity);

        MvcResult requestResponse = mvc.perform(post("/product/create").content(objectToInject.toString())).andExpect(status().is2xxSuccessful()).andReturn();
        JSONObject response = new JSONObject(requestResponse.getResponse().getContentAsString());
        product.decreaseQuantity(product.getQuantity());
        Response<Product> genericResponse = new Response<>(true, "Success creating the product", product);
        assertEquals(genericResponse.obtainJSONWithAllInfo().toString(), response.toString());

        requestResponse = mvc.perform(post("/product/create").content(objectToInject.toString())).andExpect(status().is4xxClientError()).andReturn();
        response = new JSONObject(requestResponse.getResponse().getContentAsString());
        genericResponse = new Response<>(false, "A product with the same business identifier is already registered");
        assertEquals(genericResponse.obtainJSONWithAllInfo().toString(), response.toString());

        objectToInject.remove("name");
        requestResponse = mvc.perform(post("/product/create").content(objectToInject.toString())).andExpect(status().is4xxClientError()).andReturn();
        response = new JSONObject(requestResponse.getResponse().getContentAsString());
        genericResponse = new Response<>(false, "Error converting the JSON into a Product. Check the request");
        assertEquals(genericResponse.obtainJSONWithAllInfo().toString(), response.toString());
    }

    @Test
    void increaseQuantity() throws Exception {
        assertTrue(productService.insert(product));

        Response<Product> invalidProduct = new Response<>(false, "Product not found");
        MvcResult requestResponse = mvc.perform(put("/product/increaseQuantity").param("identifier", "")
                .param("quantity", String.valueOf(randomNumberToIncrease))).andExpect(status().is4xxClientError()).andReturn();
        JSONObject response = new JSONObject(requestResponse.getResponse().getContentAsString());
        assertEquals(invalidProduct.obtainJSONWithAllInfo().toString(), response.toString());

        Response<Product> negativeQuantity = new Response<>(false, "The number must be positive");
        requestResponse = mvc.perform(put("/product/increaseQuantity").param("identifier", product.getProductBusinessKey())
                .param("quantity", String.valueOf(-5))).andExpect(status().is4xxClientError()).andReturn();
        response = new JSONObject(requestResponse.getResponse().getContentAsString());
        assertEquals(negativeQuantity.obtainJSONWithAllInfo().toString(), response.toString());

        Response<Product> finalResponse = new Response<>(true, "Quantity updated", product);
        requestResponse = mvc.perform(put("/product/increaseQuantity").param("identifier", product.getProductBusinessKey())
                .param("quantity", String.valueOf(randomNumberToIncrease))).andExpect(status().is2xxSuccessful()).andReturn();
        response = new JSONObject(requestResponse.getResponse().getContentAsString());
        product.increaseQuantity(randomNumberToIncrease);
        assertEquals(finalResponse.obtainJSONWithAllInfo().toString(), response.toString());
    }

    @Test
    void decreaseQuantity() throws Exception {
        assertTrue(productService.insert(product));

        Response<Product> invalidProduct = new Response<>(false, "Product not found");
        MvcResult requestResponse = mvc.perform(put("/product/decreaseQuantity").param("identifier", "")
                .param("quantity", String.valueOf(randomNumberToDecrease))).andExpect(status().is4xxClientError()).andReturn();
        JSONObject response = new JSONObject(requestResponse.getResponse().getContentAsString());
        assertEquals(invalidProduct.obtainJSONWithAllInfo().toString(), response.toString());

        Response<Product> negativeQuantity = new Response<>(false, "The number must be positive");
        requestResponse = mvc.perform(put("/product/decreaseQuantity").param("identifier", product.getProductBusinessKey())
                .param("quantity", String.valueOf(-5))).andExpect(status().is4xxClientError()).andReturn();
        response = new JSONObject(requestResponse.getResponse().getContentAsString());
        assertEquals(negativeQuantity.obtainJSONWithAllInfo().toString(), response.toString());

        Response<Product> invalidQuantity = new Response<>(false, "Invalid quantity obtained while trying to decrease");
        requestResponse = mvc.perform(put("/product/decreaseQuantity").param("identifier", product.getProductBusinessKey())
                .param("quantity", String.valueOf(product.getQuantity() + 20))).andExpect(status().is4xxClientError()).andReturn();
        response = new JSONObject(requestResponse.getResponse().getContentAsString());
        assertEquals(invalidQuantity.obtainJSONWithAllInfo().toString(), response.toString());

        Response<Product> finalResponse = new Response<>(true, "Quantity updated", product);
        requestResponse = mvc.perform(put("/product/decreaseQuantity").param("identifier", product.getProductBusinessKey())
                .param("quantity", String.valueOf(randomNumberToDecrease))).andExpect(status().is2xxSuccessful()).andReturn();
        response = new JSONObject(requestResponse.getResponse().getContentAsString());
        product.decreaseQuantity(randomNumberToDecrease);
        assertEquals(finalResponse.obtainJSONWithAllInfo().toString(), response.toString());
    }
}
