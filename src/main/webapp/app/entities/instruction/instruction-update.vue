<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2 id="jhipsterSampleApplicationApp.instruction.home.createOrEditLabel" data-cy="InstructionCreateUpdateHeading">
          Create or edit a Instruction
        </h2>
        <div>
          <div class="form-group" v-if="instruction.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="instruction.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="instruction-stepNumber">Step Number</label>
            <input
              type="number"
              class="form-control"
              name="stepNumber"
              id="instruction-stepNumber"
              data-cy="stepNumber"
              :class="{ valid: !$v.instruction.stepNumber.$invalid, invalid: $v.instruction.stepNumber.$invalid }"
              v-model.number="$v.instruction.stepNumber.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="instruction-description">Description</label>
            <input
              type="text"
              class="form-control"
              name="description"
              id="instruction-description"
              data-cy="description"
              :class="{ valid: !$v.instruction.description.$invalid, invalid: $v.instruction.description.$invalid }"
              v-model="$v.instruction.description.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="instruction-recipe">Recipe</label>
            <select class="form-control" id="instruction-recipe" data-cy="recipe" name="recipe" v-model="instruction.recipe">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="instruction.recipe && recipeOption.id === instruction.recipe.id ? instruction.recipe : recipeOption"
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
            :disabled="$v.instruction.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span>Save</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./instruction-update.component.ts"></script>
