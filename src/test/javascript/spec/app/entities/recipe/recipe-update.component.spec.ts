/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import Router from 'vue-router';
import { ToastPlugin } from 'bootstrap-vue';

import * as config from '@/shared/config/config';
import RecipeUpdateComponent from '@/entities/recipe/recipe-update.vue';
import RecipeClass from '@/entities/recipe/recipe-update.component';
import RecipeService from '@/entities/recipe/recipe.service';

import RecipeIngredientService from '@/entities/recipe-ingredient/recipe-ingredient.service';

import InstructionService from '@/entities/instruction/instruction.service';

import AuthorService from '@/entities/author/author.service';
import AlertService from '@/shared/alert/alert.service';

const localVue = createLocalVue();

config.initVueApp(localVue);
const store = config.initVueXStore(localVue);
const router = new Router();
localVue.use(Router);
localVue.use(ToastPlugin);
localVue.component('font-awesome-icon', {});
localVue.component('b-input-group', {});
localVue.component('b-input-group-prepend', {});
localVue.component('b-form-datepicker', {});
localVue.component('b-form-input', {});

describe('Component Tests', () => {
  describe('Recipe Management Update Component', () => {
    let wrapper: Wrapper<RecipeClass>;
    let comp: RecipeClass;
    let recipeServiceStub: SinonStubbedInstance<RecipeService>;

    beforeEach(() => {
      recipeServiceStub = sinon.createStubInstance<RecipeService>(RecipeService);

      wrapper = shallowMount<RecipeClass>(RecipeUpdateComponent, {
        store,
        localVue,
        router,
        provide: {
          recipeService: () => recipeServiceStub,
          alertService: () => new AlertService(),

          recipeIngredientService: () =>
            sinon.createStubInstance<RecipeIngredientService>(RecipeIngredientService, {
              retrieve: sinon.stub().resolves({}),
            } as any),

          instructionService: () =>
            sinon.createStubInstance<InstructionService>(InstructionService, {
              retrieve: sinon.stub().resolves({}),
            } as any),

          authorService: () =>
            sinon.createStubInstance<AuthorService>(AuthorService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
        },
      });
      comp = wrapper.vm;
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const entity = { id: 123 };
        comp.recipe = entity;
        recipeServiceStub.update.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(recipeServiceStub.update.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        comp.recipe = entity;
        recipeServiceStub.create.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(recipeServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundRecipe = { id: 123 };
        recipeServiceStub.find.resolves(foundRecipe);
        recipeServiceStub.retrieve.resolves([foundRecipe]);

        // WHEN
        comp.beforeRouteEnter({ params: { recipeId: 123 } }, null, cb => cb(comp));
        await comp.$nextTick();

        // THEN
        expect(comp.recipe).toBe(foundRecipe);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        comp.previousState();
        await comp.$nextTick();

        expect(comp.$router.currentRoute.fullPath).toContain('/');
      });
    });
  });
});
