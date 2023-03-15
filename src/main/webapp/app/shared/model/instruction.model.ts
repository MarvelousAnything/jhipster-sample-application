import { IRecipe } from '@/shared/model/recipe.model';

export interface IInstruction {
  id?: number;
  stepNumber?: number | null;
  description?: string | null;
  recipe?: IRecipe | null;
}

export class Instruction implements IInstruction {
  constructor(public id?: number, public stepNumber?: number | null, public description?: string | null, public recipe?: IRecipe | null) {}
}
