export interface IIngredient {
  id?: number;
  name?: string;
}

export class Ingredient implements IIngredient {
  constructor(public id?: number, public name?: string) {}
}
