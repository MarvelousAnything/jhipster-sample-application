import { Authority } from '@/shared/security/authority';
/* tslint:disable */
// prettier-ignore
const Entities = () => import('@/entities/entities.vue');

// prettier-ignore
const Author = () => import('@/entities/author/author.vue');
// prettier-ignore
const AuthorUpdate = () => import('@/entities/author/author-update.vue');
// prettier-ignore
const AuthorDetails = () => import('@/entities/author/author-details.vue');
// prettier-ignore
const MeasuringUnit = () => import('@/entities/measuring-unit/measuring-unit.vue');
// prettier-ignore
const MeasuringUnitUpdate = () => import('@/entities/measuring-unit/measuring-unit-update.vue');
// prettier-ignore
const MeasuringUnitDetails = () => import('@/entities/measuring-unit/measuring-unit-details.vue');
// prettier-ignore
const Ingredient = () => import('@/entities/ingredient/ingredient.vue');
// prettier-ignore
const IngredientUpdate = () => import('@/entities/ingredient/ingredient-update.vue');
// prettier-ignore
const IngredientDetails = () => import('@/entities/ingredient/ingredient-details.vue');
// prettier-ignore
const RecipeIngredient = () => import('@/entities/recipe-ingredient/recipe-ingredient.vue');
// prettier-ignore
const RecipeIngredientUpdate = () => import('@/entities/recipe-ingredient/recipe-ingredient-update.vue');
// prettier-ignore
const RecipeIngredientDetails = () => import('@/entities/recipe-ingredient/recipe-ingredient-details.vue');
// prettier-ignore
const Instruction = () => import('@/entities/instruction/instruction.vue');
// prettier-ignore
const InstructionUpdate = () => import('@/entities/instruction/instruction-update.vue');
// prettier-ignore
const InstructionDetails = () => import('@/entities/instruction/instruction-details.vue');
// prettier-ignore
const Recipe = () => import('@/entities/recipe/recipe.vue');
// prettier-ignore
const RecipeUpdate = () => import('@/entities/recipe/recipe-update.vue');
// prettier-ignore
const RecipeDetails = () => import('@/entities/recipe/recipe-details.vue');
// jhipster-needle-add-entity-to-router-import - JHipster will import entities to the router here

export default {
  path: '/',
  component: Entities,
  children: [
    {
      path: 'author',
      name: 'Author',
      component: Author,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'author/new',
      name: 'AuthorCreate',
      component: AuthorUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'author/:authorId/edit',
      name: 'AuthorEdit',
      component: AuthorUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'author/:authorId/view',
      name: 'AuthorView',
      component: AuthorDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'measuring-unit',
      name: 'MeasuringUnit',
      component: MeasuringUnit,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'measuring-unit/new',
      name: 'MeasuringUnitCreate',
      component: MeasuringUnitUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'measuring-unit/:measuringUnitId/edit',
      name: 'MeasuringUnitEdit',
      component: MeasuringUnitUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'measuring-unit/:measuringUnitId/view',
      name: 'MeasuringUnitView',
      component: MeasuringUnitDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'ingredient',
      name: 'Ingredient',
      component: Ingredient,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'ingredient/new',
      name: 'IngredientCreate',
      component: IngredientUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'ingredient/:ingredientId/edit',
      name: 'IngredientEdit',
      component: IngredientUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'ingredient/:ingredientId/view',
      name: 'IngredientView',
      component: IngredientDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'recipe-ingredient',
      name: 'RecipeIngredient',
      component: RecipeIngredient,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'recipe-ingredient/new',
      name: 'RecipeIngredientCreate',
      component: RecipeIngredientUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'recipe-ingredient/:recipeIngredientId/edit',
      name: 'RecipeIngredientEdit',
      component: RecipeIngredientUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'recipe-ingredient/:recipeIngredientId/view',
      name: 'RecipeIngredientView',
      component: RecipeIngredientDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'instruction',
      name: 'Instruction',
      component: Instruction,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'instruction/new',
      name: 'InstructionCreate',
      component: InstructionUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'instruction/:instructionId/edit',
      name: 'InstructionEdit',
      component: InstructionUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'instruction/:instructionId/view',
      name: 'InstructionView',
      component: InstructionDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'recipe',
      name: 'Recipe',
      component: Recipe,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'recipe/new',
      name: 'RecipeCreate',
      component: RecipeUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'recipe/:recipeId/edit',
      name: 'RecipeEdit',
      component: RecipeUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'recipe/:recipeId/view',
      name: 'RecipeView',
      component: RecipeDetails,
      meta: { authorities: [Authority.USER] },
    },
    // jhipster-needle-add-entity-to-router - JHipster will add entities to the router here
  ],
};
