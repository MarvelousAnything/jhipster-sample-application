<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="recipe">
        <h2 class="jh-entity-heading" data-cy="recipeDetailsHeading"><span>Recipe</span> {{ recipe.id }}</h2>
        <dl class="row jh-entity-details">
          <dt>
            <span>Name</span>
          </dt>
          <dd>
            <span>{{ recipe.name }}</span>
          </dd>
          <dt>
            <span>Description</span>
          </dt>
          <dd>
            <span>{{ recipe.description }}</span>
          </dd>
          <dt>
            <span>Prep Time</span>
          </dt>
          <dd>
            <span>{{ recipe.prepTime }}</span>
          </dd>
          <dt>
            <span>Cook Time</span>
          </dt>
          <dd>
            <span>{{ recipe.cookTime }}</span>
          </dd>
          <dt>
            <span>Servings</span>
          </dt>
          <dd>
            <span>{{ recipe.servings }}</span>
          </dd>
          <dt>
            <span>Image Url</span>
          </dt>
          <dd>
            <span>{{ recipe.imageUrl }}</span>
          </dd>
          <dt>
            <span>Author</span>
          </dt>
          <dd>
            <div v-if="recipe.author">
              <router-link :to="{ name: 'AuthorView', params: { authorId: recipe.author.id } }">{{ recipe.author.id }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span> Back</span>
        </button>
        <router-link v-if="recipe.id" :to="{ name: 'RecipeEdit', params: { recipeId: recipe.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span> Edit</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./recipe-details.component.ts"></script>
