/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import { ToastPlugin } from 'bootstrap-vue';

import * as config from '@/shared/config/config';
import RecipeComponent from '@/entities/recipe/recipe.vue';
import RecipeClass from '@/entities/recipe/recipe.component';
import RecipeService from '@/entities/recipe/recipe.service';
import AlertService from '@/shared/alert/alert.service';

const localVue = createLocalVue();
localVue.use(ToastPlugin);

config.initVueApp(localVue);
const store = config.initVueXStore(localVue);
localVue.component('font-awesome-icon', {});
localVue.component('b-badge', {});
localVue.directive('b-modal', {});
localVue.component('b-button', {});
localVue.component('router-link', {});

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  describe('Recipe Management Component', () => {
    let wrapper: Wrapper<RecipeClass>;
    let comp: RecipeClass;
    let recipeServiceStub: SinonStubbedInstance<RecipeService>;

    beforeEach(() => {
      recipeServiceStub = sinon.createStubInstance<RecipeService>(RecipeService);
      recipeServiceStub.retrieve.resolves({ headers: {} });

      wrapper = shallowMount<RecipeClass>(RecipeComponent, {
        store,
        localVue,
        stubs: { bModal: bModalStub as any },
        provide: {
          recipeService: () => recipeServiceStub,
          alertService: () => new AlertService(),
        },
      });
      comp = wrapper.vm;
    });

    it('Should call load all on init', async () => {
      // GIVEN
      recipeServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

      // WHEN
      comp.retrieveAllRecipes();
      await comp.$nextTick();

      // THEN
      expect(recipeServiceStub.retrieve.called).toBeTruthy();
      expect(comp.recipes[0]).toEqual(expect.objectContaining({ id: 123 }));
    });
    it('Should call delete service on confirmDelete', async () => {
      // GIVEN
      recipeServiceStub.delete.resolves({});

      // WHEN
      comp.prepareRemove({ id: 123 });
      expect(recipeServiceStub.retrieve.callCount).toEqual(1);

      comp.removeRecipe();
      await comp.$nextTick();

      // THEN
      expect(recipeServiceStub.delete.called).toBeTruthy();
      expect(recipeServiceStub.retrieve.callCount).toEqual(2);
    });
  });
});
