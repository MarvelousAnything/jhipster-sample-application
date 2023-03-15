/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import VueRouter from 'vue-router';

import * as config from '@/shared/config/config';
import RecipeIngredientDetailComponent from '@/entities/recipe-ingredient/recipe-ingredient-details.vue';
import RecipeIngredientClass from '@/entities/recipe-ingredient/recipe-ingredient-details.component';
import RecipeIngredientService from '@/entities/recipe-ingredient/recipe-ingredient.service';
import router from '@/router';
import AlertService from '@/shared/alert/alert.service';

const localVue = createLocalVue();
localVue.use(VueRouter);

config.initVueApp(localVue);
const store = config.initVueXStore(localVue);
localVue.component('font-awesome-icon', {});
localVue.component('router-link', {});

describe('Component Tests', () => {
  describe('RecipeIngredient Management Detail Component', () => {
    let wrapper: Wrapper<RecipeIngredientClass>;
    let comp: RecipeIngredientClass;
    let recipeIngredientServiceStub: SinonStubbedInstance<RecipeIngredientService>;

    beforeEach(() => {
      recipeIngredientServiceStub = sinon.createStubInstance<RecipeIngredientService>(RecipeIngredientService);

      wrapper = shallowMount<RecipeIngredientClass>(RecipeIngredientDetailComponent, {
        store,
        localVue,
        router,
        provide: { recipeIngredientService: () => recipeIngredientServiceStub, alertService: () => new AlertService() },
      });
      comp = wrapper.vm;
    });

    describe('OnInit', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        const foundRecipeIngredient = { id: 123 };
        recipeIngredientServiceStub.find.resolves(foundRecipeIngredient);

        // WHEN
        comp.retrieveRecipeIngredient(123);
        await comp.$nextTick();

        // THEN
        expect(comp.recipeIngredient).toBe(foundRecipeIngredient);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundRecipeIngredient = { id: 123 };
        recipeIngredientServiceStub.find.resolves(foundRecipeIngredient);

        // WHEN
        comp.beforeRouteEnter({ params: { recipeIngredientId: 123 } }, null, cb => cb(comp));
        await comp.$nextTick();

        // THEN
        expect(comp.recipeIngredient).toBe(foundRecipeIngredient);
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
