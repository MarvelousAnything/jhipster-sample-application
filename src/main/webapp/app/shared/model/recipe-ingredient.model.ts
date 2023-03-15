import { IIngredient } from '@/shared/model/ingredient.model';
import { IMeasuringUnit } from '@/shared/model/measuring-unit.model';
import { IRecipe } from '@/shared/model/recipe.model';

export interface IRecipeIngredient {
  id?: number;
  amount?: number | null;
  ingredient?: IIngredient | null;
  unit?: IMeasuringUnit | null;
  recipe?: IRecipe | null;
}

export class RecipeIngredient implements IRecipeIngredient {
  constructor(
    public id?: number,
    public amount?: number | null,
    public ingredient?: IIngredient | null,
    public unit?: IMeasuringUnit | null,
    public recipe?: IRecipe | null
  ) {}
}
