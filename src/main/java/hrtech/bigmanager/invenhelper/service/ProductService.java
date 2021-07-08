package hrtech.bigmanager.invenhelper.service;

import hrtech.bigmanager.invenhelper.exception.InvalidBusinessIdentifier;
import hrtech.bigmanager.invenhelper.exception.InvalidQuantity;
import hrtech.bigmanager.invenhelper.exception.InvalidRepresentationOfConceptOnJSON;
import hrtech.bigmanager.invenhelper.exception.InvalidText;
import hrtech.bigmanager.invenhelper.model.DomainKey;
import hrtech.bigmanager.invenhelper.model.Product;
import hrtech.bigmanager.invenhelper.model.ProductKey;
import hrtech.bigmanager.invenhelper.model.Response;
import hrtech.bigmanager.invenhelper.repository.ProductRepository;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * Product service
 */
@Service
public class ProductService implements IService<Product, ProductKey> {

    private final Logger logger = LoggerFactory.getLogger(ProductService.class);
    private ProductRepository productRepository;

    @Autowired
    public void setProductRepository(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Method that creates a new entity
     *
     * @param objectToSave entity to be created
     * @return entity created
     */
    @Override
    public boolean insert(Product objectToSave) {
        return productRepository.insert(objectToSave);
    }

    /**
     * Method that updates an entity
     *
     * @param objectToSave entity to be updated
     * @return entity updated
     */
    @Override
    public boolean save(Product objectToSave) {
        return productRepository.save(objectToSave);
    }

    /**
     * Method that, given a key object, returns the corresponding Entity on an Optional
     *
     * @param keyToSearch valid key object to search the entity
     * @return Option that contains the entity, if such ID exists on the database
     */
    @Override
    public Optional<Product> findById(Object keyToSearch) {
        try {
            ProductKey key = new ProductKey(DomainKey.convertStringToUUID(keyToSearch.toString()), keyToSearch.toString());
            return productRepository.findById(key);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid key: " + keyToSearch);
            return Optional.empty();
        }
    }

    /**
     * Method that, given a key object, returns the corresponding object using the business identifier
     *
     * @param keyToSearch valid key object to search the entity
     * @return Option that contains the entity, if such ID exists on the database
     */
    public Optional<Product> findByBusinessKey(String keyToSearch) {
        try {
            ProductKey key = new ProductKey(UUID.randomUUID(), keyToSearch);
            return productRepository.findByBusinessId(key);
        } catch (InvalidBusinessIdentifier ibi) {
            return Optional.empty();
        }
    }

    /**
     * Method that, receiving a valid JSON object, creates a new Product and saves it on the database
     *
     * @param objectOnBody JSON body with info about the product for the creation
     * @return Response object with info about the success of the operation
     */
    public Response<Product> createNewProduct(JSONObject objectOnBody) {
        try {
            Product product = Product.convertFromJSONToCreate(objectOnBody);
            if (this.findByBusinessKey(product.getProductBusinessKey()).isPresent()) {
                throw new IllegalArgumentException("A product with the same business identifier is already registered");
            }

            boolean answer = this.insert(product);
            return new Response<>(answer, (answer ? "Success creating the product" : "Error saving on the database"), (answer ? product : null));
        } catch (InvalidRepresentationOfConceptOnJSON e) {
            logger.error("Invalid JSON object to be converted to Product: " + e.getLocalizedMessage());
            return new Response<>(false, "Error converting the JSON into a Product. Check the request");
        } catch (IllegalArgumentException e) {
            logger.error(e.getLocalizedMessage());
            return new Response<>(false, e.getLocalizedMessage());
        }
    }

    /**
     * Method that obtains a product by its business identifier and increases by the quantity passed by parameter
     *
     * @param businessIdentifier product identifier
     * @param quantity           quantity to be increased
     * @return Response with info about the success of the operation
     */
    public Response<Product> increaseQuantity(String businessIdentifier, int quantity) {
        Optional<Product> optionalProduct = findByBusinessKey(businessIdentifier);
        if (optionalProduct.isEmpty()) {
            return new Response<>(false, "Product not found");
        }

        Product product = optionalProduct.get();
        try {
            product.increaseQuantity(quantity);
        } catch (InvalidQuantity iq) {
            return new Response<>(false, "Invalid quantity obtained while trying to increase");
        }

        if (this.save(product)) {
            return new Response<>(true, "Quantity updated", product);
        } else {
            return new Response<>(false, "Error updating database", product);
        }
    }

    /**
     * Method that obtains a product by its business identifier and decreases by the quantity passed by parameter
     *
     * @param businessIdentifier product identifier
     * @param quantity           quantity to be increased
     * @return Response with info about the success of the operation
     */
    public Response<Product> decreaseQuantity(String businessIdentifier, int quantity) {
        Optional<Product> optionalProduct = findByBusinessKey(businessIdentifier);
        if (optionalProduct.isEmpty()) {
            return new Response<>(false, "Product not found");
        }

        Product product = optionalProduct.get();
        try {
            product.decreaseQuantity(quantity);
        } catch (InvalidQuantity iq) {
            return new Response<>(false, "Invalid quantity obtained while trying to decrease");
        }

        if (this.save(product)) {
            return new Response<>(true, "Quantity updated", product);
        } else {
            return new Response<>(false, "Error updating database", product);
        }
    }

    /**
     * Method that updates the product information (name and description).
     *
     * @param info JSON object with info to update. It must contain the 'identifier' obligatory, 'name' and 'description' are optional
     * @return Response with info about the success of the operation
     */
    public Response<Product> updateProductInformation(JSONObject info) {
        String businessIdentifier = info.optString("identifier", "");
        Optional<Product> productToUpdate = findByBusinessKey(businessIdentifier);
        if (productToUpdate.isEmpty()) {
            return new Response<>(false, "Product not found");
        }

        try {
            boolean hasChanges = false;

            Product oldProduct = productToUpdate.get();
            if (info.has("name")) {
                oldProduct.changeName(info.optString("name", ""));
                hasChanges = true;
            }
            if (info.has("description")) {
                oldProduct.changeDescription(info.optString("description", ""));
                hasChanges = true;
            }

            if (!hasChanges) {
                return new Response<>(true, "No information to update product");
            }
            if (this.save(oldProduct)) {
                return new Response<>(true, "Product updated", oldProduct);
            } else {
                return new Response<>(false, "Error updating database", productToUpdate.get());
            }
        } catch (InvalidText it) {
            return new Response<>(false, it.getLocalizedMessage());
        } catch (IllegalArgumentException e) {
            logger.error(e.getLocalizedMessage());
            return new Response<>(false, "Error while updating product information");
        }
    }
}
