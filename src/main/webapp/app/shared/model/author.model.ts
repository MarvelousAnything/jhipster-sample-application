import { IRecipe } from '@/shared/model/recipe.model';

export interface IAuthor {
  id?: number;
  username?: string;
  email?: string;
  password?: string;
  recipes?: IRecipe[] | null;
}

export class Author implements IAuthor {
  constructor(
    public id?: number,
    public username?: string,
    public email?: string,
    public password?: string,
    public recipes?: IRecipe[] | null
  ) {}
}
