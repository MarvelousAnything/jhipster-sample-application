import { IRecipeIngredient } from '@/shared/model/recipe-ingredient.model';
import { IInstruction } from '@/shared/model/instruction.model';
import { IAuthor } from '@/shared/model/author.model';

export interface IRecipe {
  id?: number;
  name?: string;
  description?: string | null;
  prepTime?: number | null;
  cookTime?: number | null;
  servings?: number | null;
  imageUrl?: string | null;
  ingredients?: IRecipeIngredient[] | null;
  instructions?: IInstruction[] | null;
  author?: IAuthor | null;
}

export class Recipe implements IRecipe {
  constructor(
    public id?: number,
    public name?: string,
    public description?: string | null,
    public prepTime?: number | null,
    public cookTime?: number | null,
    public servings?: number | null,
    public imageUrl?: string | null,
    public ingredients?: IRecipeIngredient[] | null,
    public instructions?: IInstruction[] | null,
    public author?: IAuthor | null
  ) {}
}
