<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2 id="jhipsterSampleApplicationApp.recipeIngredient.home.createOrEditLabel" data-cy="RecipeIngredientCreateUpdateHeading">
          Create or edit a RecipeIngredient
        </h2>
        <div>
          <div class="form-group" v-if="recipeIngredient.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="recipeIngredient.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="recipe-ingredient-amount">Amount</label>
            <input
              type="number"
              class="form-control"
              name="amount"
              id="recipe-ingredient-amount"
              data-cy="amount"
              :class="{ valid: !$v.recipeIngredient.amount.$invalid, invalid: $v.recipeIngredient.amount.$invalid }"
              v-model.number="$v.recipeIngredient.amount.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="recipe-ingredient-ingredient">Ingredient</label>
            <select
              class="form-control"
              id="recipe-ingredient-ingredient"
              data-cy="ingredient"
              name="ingredient"
              v-model="recipeIngredient.ingredient"
            >
              <option v-bind:value="null"></option>
              <option
                v-bind:value="
                  recipeIngredient.ingredient && ingredientOption.id === recipeIngredient.ingredient.id
                    ? recipeIngredient.ingredient
                    : ingredientOption
                "
                v-for="ingredientOption in ingredients"
                :key="ingredientOption.id"
              >
                {{ ingredientOption.id }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="recipe-ingredient-unit">Unit</label>
            <select class="form-control" id="recipe-ingredient-unit" data-cy="unit" name="unit" v-model="recipeIngredient.unit">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="
                  recipeIngredient.unit && measuringUnitOption.id === recipeIngredient.unit.id ? recipeIngredient.unit : measuringUnitOption
                "
                v-for="measuringUnitOption in measuringUnits"
                :key="measuringUnitOption.id"
              >
                {{ measuringUnitOption.id }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="recipe-ingredient-recipe">Recipe</label>
            <select class="form-control" id="recipe-ingredient-recipe" data-cy="recipe" name="recipe" v-model="recipeIngredient.recipe">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="
                  recipeIngredient.recipe && recipeOption.id === recipeIngredient.recipe.id ? recipeIngredient.recipe : recipeOption
                "
                v-for="recipeOption in recipes"
                :key="recipeOption.id"
              >
                {{ recipeOption.id }}
              </option>
            </select>
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" v-on:click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span>Cancel</span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="$v.recipeIngredient.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span>Save</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./recipe-ingredient-update.component.ts"></script>
