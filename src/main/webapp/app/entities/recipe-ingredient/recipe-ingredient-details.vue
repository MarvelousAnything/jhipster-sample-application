<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="recipeIngredient">
        <h2 class="jh-entity-heading" data-cy="recipeIngredientDetailsHeading"><span>RecipeIngredient</span> {{ recipeIngredient.id }}</h2>
        <dl class="row jh-entity-details">
          <dt>
            <span>Amount</span>
          </dt>
          <dd>
            <span>{{ recipeIngredient.amount }}</span>
          </dd>
          <dt>
            <span>Ingredient</span>
          </dt>
          <dd>
            <div v-if="recipeIngredient.ingredient">
              <router-link :to="{ name: 'IngredientView', params: { ingredientId: recipeIngredient.ingredient.id } }">{{
                recipeIngredient.ingredient.id
              }}</router-link>
            </div>
          </dd>
          <dt>
            <span>Unit</span>
          </dt>
          <dd>
            <div v-if="recipeIngredient.unit">
              <router-link :to="{ name: 'MeasuringUnitView', params: { measuringUnitId: recipeIngredient.unit.id } }">{{
                recipeIngredient.unit.id
              }}</router-link>
            </div>
          </dd>
          <dt>
            <span>Recipe</span>
          </dt>
          <dd>
            <div v-if="recipeIngredient.recipe">
              <router-link :to="{ name: 'RecipeView', params: { recipeId: recipeIngredient.recipe.id } }">{{
                recipeIngredient.recipe.id
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span> Back</span>
        </button>
        <router-link
          v-if="recipeIngredient.id"
          :to="{ name: 'RecipeIngredientEdit', params: { recipeIngredientId: recipeIngredient.id } }"
          custom
          v-slot="{ navigate }"
        >
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span> Edit</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./recipe-ingredient-details.component.ts"></script>
