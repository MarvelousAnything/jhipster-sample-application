/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import VueRouter from 'vue-router';

import * as config from '@/shared/config/config';
import RecipeDetailComponent from '@/entities/recipe/recipe-details.vue';
import RecipeClass from '@/entities/recipe/recipe-details.component';
import RecipeService from '@/entities/recipe/recipe.service';
import router from '@/router';
import AlertService from '@/shared/alert/alert.service';

const localVue = createLocalVue();
localVue.use(VueRouter);

config.initVueApp(localVue);
const store = config.initVueXStore(localVue);
localVue.component('font-awesome-icon', {});
localVue.component('router-link', {});

describe('Component Tests', () => {
  describe('Recipe Management Detail Component', () => {
    let wrapper: Wrapper<RecipeClass>;
    let comp: RecipeClass;
    let recipeServiceStub: SinonStubbedInstance<RecipeService>;

    beforeEach(() => {
      recipeServiceStub = sinon.createStubInstance<RecipeService>(RecipeService);

      wrapper = shallowMount<RecipeClass>(RecipeDetailComponent, {
        store,
        localVue,
        router,
        provide: { recipeService: () => recipeServiceStub, alertService: () => new AlertService() },
      });
      comp = wrapper.vm;
    });

    describe('OnInit', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        const foundRecipe = { id: 123 };
        recipeServiceStub.find.resolves(foundRecipe);

        // WHEN
        comp.retrieveRecipe(123);
        await comp.$nextTick();

        // THEN
        expect(comp.recipe).toBe(foundRecipe);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundRecipe = { id: 123 };
        recipeServiceStub.find.resolves(foundRecipe);

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
