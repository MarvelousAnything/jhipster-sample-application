/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import Router from 'vue-router';
import { ToastPlugin } from 'bootstrap-vue';

import * as config from '@/shared/config/config';
import MeasuringUnitUpdateComponent from '@/entities/measuring-unit/measuring-unit-update.vue';
import MeasuringUnitClass from '@/entities/measuring-unit/measuring-unit-update.component';
import MeasuringUnitService from '@/entities/measuring-unit/measuring-unit.service';

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
  describe('MeasuringUnit Management Update Component', () => {
    let wrapper: Wrapper<MeasuringUnitClass>;
    let comp: MeasuringUnitClass;
    let measuringUnitServiceStub: SinonStubbedInstance<MeasuringUnitService>;

    beforeEach(() => {
      measuringUnitServiceStub = sinon.createStubInstance<MeasuringUnitService>(MeasuringUnitService);

      wrapper = shallowMount<MeasuringUnitClass>(MeasuringUnitUpdateComponent, {
        store,
        localVue,
        router,
        provide: {
          measuringUnitService: () => measuringUnitServiceStub,
          alertService: () => new AlertService(),
        },
      });
      comp = wrapper.vm;
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const entity = { id: 123 };
        comp.measuringUnit = entity;
        measuringUnitServiceStub.update.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(measuringUnitServiceStub.update.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        comp.measuringUnit = entity;
        measuringUnitServiceStub.create.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(measuringUnitServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundMeasuringUnit = { id: 123 };
        measuringUnitServiceStub.find.resolves(foundMeasuringUnit);
        measuringUnitServiceStub.retrieve.resolves([foundMeasuringUnit]);

        // WHEN
        comp.beforeRouteEnter({ params: { measuringUnitId: 123 } }, null, cb => cb(comp));
        await comp.$nextTick();

        // THEN
        expect(comp.measuringUnit).toBe(foundMeasuringUnit);
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
