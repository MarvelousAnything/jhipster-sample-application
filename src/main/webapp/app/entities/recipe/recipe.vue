<template>
  <div>
    <h2 id="page-heading" data-cy="RecipeHeading">
      <span id="recipe-heading">Recipes</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon> <span>Refresh List</span>
        </button>
        <router-link :to="{ name: 'RecipeCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-recipe"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span> Create a new Recipe </span>
          </button>
        </router-link>
      </div>
    </h2>
    <div class="row">
      <div class="col-sm-12">
        <form name="searchForm" class="form-inline" v-on:submit.prevent="search(currentSearch)">
          <div class="input-group w-100 mt-3">
            <input type="text" class="form-control" name="currentSearch" id="currentSearch" v-model="currentSearch" />
            <button type="button" id="launch-search" class="btn btn-primary" v-on:click="search(currentSearch)">
              <font-awesome-icon icon="search"></font-awesome-icon>
            </button>
            <button type="button" id="clear-search" class="btn btn-secondary" v-on:click="clear()" v-if="currentSearch">
              <font-awesome-icon icon="trash"></font-awesome-icon>
            </button>
          </div>
        </form>
      </div>
    </div>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && recipes && recipes.length === 0">
      <span>No recipes found</span>
    </div>
    <div class="table-responsive" v-if="recipes && recipes.length > 0">
      <table class="table table-striped" aria-describedby="recipes">
        <thead>
          <tr>
            <th scope="row"><span>ID</span></th>
            <th scope="row"><span>Name</span></th>
            <th scope="row"><span>Description</span></th>
            <th scope="row"><span>Prep Time</span></th>
            <th scope="row"><span>Cook Time</span></th>
            <th scope="row"><span>Servings</span></th>
            <th scope="row"><span>Image Url</span></th>
            <th scope="row"><span>Author</span></th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="recipe in recipes" :key="recipe.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'RecipeView', params: { recipeId: recipe.id } }">{{ recipe.id }}</router-link>
            </td>
            <td>{{ recipe.name }}</td>
            <td>{{ recipe.description }}</td>
            <td>{{ recipe.prepTime }}</td>
            <td>{{ recipe.cookTime }}</td>
            <td>{{ recipe.servings }}</td>
            <td>{{ recipe.imageUrl }}</td>
            <td>
              <div v-if="recipe.author">
                <router-link :to="{ name: 'AuthorView', params: { authorId: recipe.author.id } }">{{ recipe.author.id }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'RecipeView', params: { recipeId: recipe.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline">View</span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'RecipeEdit', params: { recipeId: recipe.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline">Edit</span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(recipe)"
                  variant="danger"
                  class="btn btn-sm"
                  data-cy="entityDeleteButton"
                  v-b-modal.removeEntity
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                  <span class="d-none d-md-inline">Delete</span>
                </b-button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <b-modal ref="removeEntity" id="removeEntity">
      <span slot="modal-title"
        ><span id="jhipsterSampleApplicationApp.recipe.delete.question" data-cy="recipeDeleteDialogHeading"
          >Confirm delete operation</span
        ></span
      >
      <div class="modal-body">
        <p id="jhi-delete-recipe-heading">Are you sure you want to delete this Recipe?</p>
      </div>
      <div slot="modal-footer">
        <button type="button" class="btn btn-secondary" v-on:click="closeDialog()">Cancel</button>
        <button
          type="button"
          class="btn btn-primary"
          id="jhi-confirm-delete-recipe"
          data-cy="entityConfirmDeleteButton"
          v-on:click="removeRecipe()"
        >
          Delete
        </button>
      </div>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./recipe.component.ts"></script>
