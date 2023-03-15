<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2 id="jhipsterSampleApplicationApp.recipe.home.createOrEditLabel" data-cy="RecipeCreateUpdateHeading">Create or edit a Recipe</h2>
        <div>
          <div class="form-group" v-if="recipe.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="recipe.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="recipe-name">Name</label>
            <input
              type="text"
              class="form-control"
              name="name"
              id="recipe-name"
              data-cy="name"
              :class="{ valid: !$v.recipe.name.$invalid, invalid: $v.recipe.name.$invalid }"
              v-model="$v.recipe.name.$model"
              required
            />
            <div v-if="$v.recipe.name.$anyDirty && $v.recipe.name.$invalid">
              <small class="form-text text-danger" v-if="!$v.recipe.name.required"> This field is required. </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="recipe-description">Description</label>
            <input
              type="text"
              class="form-control"
              name="description"
              id="recipe-description"
              data-cy="description"
              :class="{ valid: !$v.recipe.description.$invalid, invalid: $v.recipe.description.$invalid }"
              v-model="$v.recipe.description.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="recipe-prepTime">Prep Time</label>
            <input
              type="number"
              class="form-control"
              name="prepTime"
              id="recipe-prepTime"
              data-cy="prepTime"
              :class="{ valid: !$v.recipe.prepTime.$invalid, invalid: $v.recipe.prepTime.$invalid }"
              v-model.number="$v.recipe.prepTime.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="recipe-cookTime">Cook Time</label>
            <input
              type="number"
              class="form-control"
              name="cookTime"
              id="recipe-cookTime"
              data-cy="cookTime"
              :class="{ valid: !$v.recipe.cookTime.$invalid, invalid: $v.recipe.cookTime.$invalid }"
              v-model.number="$v.recipe.cookTime.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="recipe-servings">Servings</label>
            <input
              type="number"
              class="form-control"
              name="servings"
              id="recipe-servings"
              data-cy="servings"
              :class="{ valid: !$v.recipe.servings.$invalid, invalid: $v.recipe.servings.$invalid }"
              v-model.number="$v.recipe.servings.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="recipe-imageUrl">Image Url</label>
            <input
              type="text"
              class="form-control"
              name="imageUrl"
              id="recipe-imageUrl"
              data-cy="imageUrl"
              :class="{ valid: !$v.recipe.imageUrl.$invalid, invalid: $v.recipe.imageUrl.$invalid }"
              v-model="$v.recipe.imageUrl.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="recipe-author">Author</label>
            <select class="form-control" id="recipe-author" data-cy="author" name="author" v-model="recipe.author">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="recipe.author && authorOption.id === recipe.author.id ? recipe.author : authorOption"
                v-for="authorOption in authors"
                :key="authorOption.id"
              >
                {{ authorOption.id }}
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
            :disabled="$v.recipe.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span>Save</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./recipe-update.component.ts"></script>
