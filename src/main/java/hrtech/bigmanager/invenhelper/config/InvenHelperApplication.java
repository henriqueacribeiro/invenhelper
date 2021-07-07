package hrtech.bigmanager.invenhelper.config;

import hrtech.bigmanager.invenhelper.model.Product;
import hrtech.bigmanager.invenhelper.model.ProductInformation;
import hrtech.bigmanager.invenhelper.model.ProductKey;
import hrtech.bigmanager.invenhelper.model.Quantity;
import hrtech.bigmanager.invenhelper.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@SpringBootApplication(scanBasePackages = {"hrtech.bigmanager.invenhelper.*"})
public class InvenHelperApplication {

    private final Logger logger = LoggerFactory.getLogger(InvenHelperApplication.class);
    private ProductRepository repo;

    @Autowired
    public void setRepo(ProductRepository repo) {
        this.repo = repo;
    }

    public static void main(String[] args) {
        SpringApplication.run(InvenHelperApplication.class, args);
    }

    @Bean
    @Profile("dev")
    public CommandLineRunner bootstrap() {
        return (args) -> {
            logger.info("Creating Product mock data");

            ProductInformation info = new ProductInformation("TestName", "This is a test description");
            Quantity quantity = new Quantity(4);
            ProductKey key = new ProductKey("TestProduct");
            Product product = new Product(key, info, quantity);
            repo.createProduct(product);

            logger.info("Finished product mock data creation");
        };
    }

}
