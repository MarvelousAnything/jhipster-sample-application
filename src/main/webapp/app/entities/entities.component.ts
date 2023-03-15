import { Component, Provide, Vue } from 'vue-property-decorator';

import UserService from '@/entities/user/user.service';
import AuthorService from './author/author.service';
import MeasuringUnitService from './measuring-unit/measuring-unit.service';
import IngredientService from './ingredient/ingredient.service';
import RecipeIngredientService from './recipe-ingredient/recipe-ingredient.service';
import InstructionService from './instruction/instruction.service';
import RecipeService from './recipe/recipe.service';
// jhipster-needle-add-entity-service-to-entities-component-import - JHipster will import entities services here

@Component
export default class Entities extends Vue {
  @Provide('userService') private userService = () => new UserService();
  @Provide('authorService') private authorService = () => new AuthorService();
  @Provide('measuringUnitService') private measuringUnitService = () => new MeasuringUnitService();
  @Provide('ingredientService') private ingredientService = () => new IngredientService();
  @Provide('recipeIngredientService') private recipeIngredientService = () => new RecipeIngredientService();
  @Provide('instructionService') private instructionService = () => new InstructionService();
  @Provide('recipeService') private recipeService = () => new RecipeService();
  // jhipster-needle-add-entity-service-to-entities-component - JHipster will import entities services here
}
