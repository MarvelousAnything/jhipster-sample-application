/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import Router from 'vue-router';
import { ToastPlugin } from 'bootstrap-vue';

import * as config from '@/shared/config/config';
import InstructionUpdateComponent from '@/entities/instruction/instruction-update.vue';
import InstructionClass from '@/entities/instruction/instruction-update.component';
import InstructionService from '@/entities/instruction/instruction.service';

import RecipeService from '@/entities/recipe/recipe.service';
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
  describe('Instruction Management Update Component', () => {
    let wrapper: Wrapper<InstructionClass>;
    let comp: InstructionClass;
    let instructionServiceStub: SinonStubbedInstance<InstructionService>;

    beforeEach(() => {
      instructionServiceStub = sinon.createStubInstance<InstructionService>(InstructionService);

      wrapper = shallowMount<InstructionClass>(InstructionUpdateComponent, {
        store,
        localVue,
        router,
        provide: {
          instructionService: () => instructionServiceStub,
          alertService: () => new AlertService(),

          recipeService: () =>
            sinon.createStubInstance<RecipeService>(RecipeService, {
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
        comp.instruction = entity;
        instructionServiceStub.update.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(instructionServiceStub.update.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        comp.instruction = entity;
        instructionServiceStub.create.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(instructionServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundInstruction = { id: 123 };
        instructionServiceStub.find.resolves(foundInstruction);
        instructionServiceStub.retrieve.resolves([foundInstruction]);

        // WHEN
        comp.beforeRouteEnter({ params: { instructionId: 123 } }, null, cb => cb(comp));
        await comp.$nextTick();

        // THEN
        expect(comp.instruction).toBe(foundInstruction);
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
