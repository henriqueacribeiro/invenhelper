package hrtech.bigmanager.invenhelper.controller;

import hrtech.bigmanager.invenhelper.model.Product;
import hrtech.bigmanager.invenhelper.model.Response;
import hrtech.bigmanager.invenhelper.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller for product manipulation
 */
@RestController
@RequestScope
@RequestMapping("/product")
public class ProductController {

    private ProductService service;

    @Autowired
    public void setService(ProductService service) {
        this.service = service;
    }

    @GetMapping(value = "/getAllIdentifiers", produces = "application/json")
    @Operation(summary = "Obtain list of product identifiers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List obtained", content = @Content(schema = @Schema(implementation = ArrayList.class)))
    })
    public ResponseEntity<String> getBusinessIdentifiers() {
        List<String> identifiers = service.findListOfIdentifiers();
        return new ResponseEntity<>(new JSONArray(identifiers).toString(), HttpStatus.OK);
    }

    @GetMapping(value = "/getByID", produces = "application/json")
    @Operation(summary = "Obtain product by its identifier", parameters = {
            @Parameter(in = ParameterIn.PATH, name = "identifier", description = "Product business identifier")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product created", content = @Content(schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "400", description = "Error obtaining product", content = @Content(schema = @Schema(implementation = Response.class))),
    })
    public ResponseEntity<String> getProductByBusinessIdentifier(@RequestParam(value = "identifier") String businessIdentifier) {
        Optional<Product> productOpt = service.findByBusinessKey(businessIdentifier);
        return productOpt.map(product -> new ResponseEntity<>(product.convertToJSON().toString(), HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(new Response<Product>(false, "Product not found").obtainJSONWithAdditionalInformation().toString(), HttpStatus.BAD_REQUEST));
    }

    @PostMapping(value = "/create", produces = "application/json")
    @Operation(summary = "Creates a new product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product created", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "400", description = "Error while creating product", content = @Content(schema = @Schema(implementation = Response.class))),
    })
    public ResponseEntity<String> createProduct(@RequestBody String informationAboutProductOnString) {
        JSONObject obj = new JSONObject(informationAboutProductOnString);
        try {
            Response<Product> response = service.createNewProduct(obj);
            if (response.isSuccess()) {
                return new ResponseEntity<>(response.obtainJSONWithAllInfo().toString(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(response.obtainJSONWithAdditionalInformation().toString(), HttpStatus.BAD_REQUEST);
            }
        } catch (JSONException je) {
            return new ResponseEntity<>(new Response<Product>(false, "Error on JSON body. Check the information").obtainJSONWithAdditionalInformation().toString(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(value = "updateProduct", produces = "application/json")
    @Operation(summary = "Updates the product name and/or description")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "400", description = "Error while updating product", content = @Content(schema = @Schema(implementation = Response.class)))
    })
    public ResponseEntity<String> updateProduct(@RequestBody String informationAboutProductOnString) {
        try {
            JSONObject object = new JSONObject(informationAboutProductOnString);
            Response<Product> response = service.updateProductInformation(object);
            if (response.isSuccess()) {
                return new ResponseEntity<>(response.obtainJSONWithAllInfo().toString(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(response.obtainJSONWithAdditionalInformation().toString(), HttpStatus.BAD_REQUEST);
            }
        } catch (JSONException je) {
            return new ResponseEntity<>(new Response<Product>(false, "Error on JSON body. Check the information").obtainJSONWithAdditionalInformation().toString(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(value = "increaseQuantity", produces = "application/json")
    @Operation(summary = "Increases the quantity of a product, given the quantity and the business identifier", parameters = {
            @Parameter(in = ParameterIn.PATH, name = "quantity", description = "Quantity to increase. Must be positive"),
            @Parameter(in = ParameterIn.PATH, name = "identifier", description = "Product business identifier")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quantity increased", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "400", description = "Error increasing quantity", content = @Content(schema = @Schema(implementation = Response.class)))
    })
    public ResponseEntity<String> increaseQuantity(@RequestParam(value = "identifier") String businessIdentifier, @RequestParam(value = "quantity") int quantityToIncrease) {
        Response<Product> response = service.increaseQuantity(businessIdentifier, quantityToIncrease);
        if (response.isSuccess()) {
            return new ResponseEntity<>(response.obtainJSONWithAllInfo().toString(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response.obtainJSONWithAdditionalInformation().toString(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(value = "decreaseQuantity", produces = "application/json")
    @Operation(summary = "Decreases the quantity of a product, given the quantity and the business identifier", parameters = {
            @Parameter(in = ParameterIn.PATH, name = "quantity", description = "Quantity to decrease. Must be positive"),
            @Parameter(in = ParameterIn.PATH, name = "businessIdentifier", description = "Product business identifier")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quantity decreased", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "400", description = "Error decreasing quantity", content = @Content(schema = @Schema(implementation = Response.class)))
    })
    public ResponseEntity<String> decreaseQuantity(@RequestParam(value = "identifier") String businessIdentifier, @RequestParam(value = "quantity") int quantityToDecrease) {
        Response<Product> response = service.decreaseQuantity(businessIdentifier, quantityToDecrease);
        if (response.isSuccess()) {
            return new ResponseEntity<>(response.obtainJSONWithAllInfo().toString(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response.obtainJSONWithAdditionalInformation().toString(), HttpStatus.BAD_REQUEST);
        }
    }


}
