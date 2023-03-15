/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import VueRouter from 'vue-router';

import * as config from '@/shared/config/config';
import InstructionDetailComponent from '@/entities/instruction/instruction-details.vue';
import InstructionClass from '@/entities/instruction/instruction-details.component';
import InstructionService from '@/entities/instruction/instruction.service';
import router from '@/router';
import AlertService from '@/shared/alert/alert.service';

const localVue = createLocalVue();
localVue.use(VueRouter);

config.initVueApp(localVue);
const store = config.initVueXStore(localVue);
localVue.component('font-awesome-icon', {});
localVue.component('router-link', {});

describe('Component Tests', () => {
  describe('Instruction Management Detail Component', () => {
    let wrapper: Wrapper<InstructionClass>;
    let comp: InstructionClass;
    let instructionServiceStub: SinonStubbedInstance<InstructionService>;

    beforeEach(() => {
      instructionServiceStub = sinon.createStubInstance<InstructionService>(InstructionService);

      wrapper = shallowMount<InstructionClass>(InstructionDetailComponent, {
        store,
        localVue,
        router,
        provide: { instructionService: () => instructionServiceStub, alertService: () => new AlertService() },
      });
      comp = wrapper.vm;
    });

    describe('OnInit', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        const foundInstruction = { id: 123 };
        instructionServiceStub.find.resolves(foundInstruction);

        // WHEN
        comp.retrieveInstruction(123);
        await comp.$nextTick();

        // THEN
        expect(comp.instruction).toBe(foundInstruction);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundInstruction = { id: 123 };
        instructionServiceStub.find.resolves(foundInstruction);

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
