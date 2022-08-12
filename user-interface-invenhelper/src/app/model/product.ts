export class Product {

  identifier: string;
  name: string;
  description: string;
  quantity: number;

  constructor(id: string, name: string, description: string, qt: number) {
    this.identifier = id;
    this.name = name;
    this.description = description;
    this.quantity = qt;
  }
}
