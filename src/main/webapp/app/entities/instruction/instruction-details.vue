<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="instruction">
        <h2 class="jh-entity-heading" data-cy="instructionDetailsHeading"><span>Instruction</span> {{ instruction.id }}</h2>
        <dl class="row jh-entity-details">
          <dt>
            <span>Step Number</span>
          </dt>
          <dd>
            <span>{{ instruction.stepNumber }}</span>
          </dd>
          <dt>
            <span>Description</span>
          </dt>
          <dd>
            <span>{{ instruction.description }}</span>
          </dd>
          <dt>
            <span>Recipe</span>
          </dt>
          <dd>
            <div v-if="instruction.recipe">
              <router-link :to="{ name: 'RecipeView', params: { recipeId: instruction.recipe.id } }">{{
                instruction.recipe.id
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span> Back</span>
        </button>
        <router-link
          v-if="instruction.id"
          :to="{ name: 'InstructionEdit', params: { instructionId: instruction.id } }"
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

<script lang="ts" src="./instruction-details.component.ts"></script>
