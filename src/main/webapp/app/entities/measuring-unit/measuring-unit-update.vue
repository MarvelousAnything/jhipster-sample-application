<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2 id="jhipsterSampleApplicationApp.measuringUnit.home.createOrEditLabel" data-cy="MeasuringUnitCreateUpdateHeading">
          Create or edit a MeasuringUnit
        </h2>
        <div>
          <div class="form-group" v-if="measuringUnit.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="measuringUnit.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="measuring-unit-name">Name</label>
            <input
              type="text"
              class="form-control"
              name="name"
              id="measuring-unit-name"
              data-cy="name"
              :class="{ valid: !$v.measuringUnit.name.$invalid, invalid: $v.measuringUnit.name.$invalid }"
              v-model="$v.measuringUnit.name.$model"
              required
            />
            <div v-if="$v.measuringUnit.name.$anyDirty && $v.measuringUnit.name.$invalid">
              <small class="form-text text-danger" v-if="!$v.measuringUnit.name.required"> This field is required. </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="measuring-unit-abbreviation">Abbreviation</label>
            <input
              type="text"
              class="form-control"
              name="abbreviation"
              id="measuring-unit-abbreviation"
              data-cy="abbreviation"
              :class="{ valid: !$v.measuringUnit.abbreviation.$invalid, invalid: $v.measuringUnit.abbreviation.$invalid }"
              v-model="$v.measuringUnit.abbreviation.$model"
            />
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
            :disabled="$v.measuringUnit.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span>Save</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./measuring-unit-update.component.ts"></script>
