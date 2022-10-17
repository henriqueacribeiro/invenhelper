export const environment = {
  production: true,
  backend: {
    host: "http://localhost:8080/",
    getProducts: "product/getAllIdentifiers",
    getProductByID: "product/getByID",
    increaseQuantity: "product/increaseQuantity",
    decreaseQuantity: "product/decreaseQuantity",
  }
};
