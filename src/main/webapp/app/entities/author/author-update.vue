<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2 id="jhipsterSampleApplicationApp.author.home.createOrEditLabel" data-cy="AuthorCreateUpdateHeading">Create or edit a Author</h2>
        <div>
          <div class="form-group" v-if="author.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="author.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="author-username">Username</label>
            <input
              type="text"
              class="form-control"
              name="username"
              id="author-username"
              data-cy="username"
              :class="{ valid: !$v.author.username.$invalid, invalid: $v.author.username.$invalid }"
              v-model="$v.author.username.$model"
              required
            />
            <div v-if="$v.author.username.$anyDirty && $v.author.username.$invalid">
              <small class="form-text text-danger" v-if="!$v.author.username.required"> This field is required. </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="author-email">Email</label>
            <input
              type="text"
              class="form-control"
              name="email"
              id="author-email"
              data-cy="email"
              :class="{ valid: !$v.author.email.$invalid, invalid: $v.author.email.$invalid }"
              v-model="$v.author.email.$model"
              required
            />
            <div v-if="$v.author.email.$anyDirty && $v.author.email.$invalid">
              <small class="form-text text-danger" v-if="!$v.author.email.required"> This field is required. </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="author-password">Password</label>
            <input
              type="text"
              class="form-control"
              name="password"
              id="author-password"
              data-cy="password"
              :class="{ valid: !$v.author.password.$invalid, invalid: $v.author.password.$invalid }"
              v-model="$v.author.password.$model"
              required
            />
            <div v-if="$v.author.password.$anyDirty && $v.author.password.$invalid">
              <small class="form-text text-danger" v-if="!$v.author.password.required"> This field is required. </small>
            </div>
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
            :disabled="$v.author.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span>Save</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./author-update.component.ts"></script>
