/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import VueRouter from 'vue-router';

import * as config from '@/shared/config/config';
import MeasuringUnitDetailComponent from '@/entities/measuring-unit/measuring-unit-details.vue';
import MeasuringUnitClass from '@/entities/measuring-unit/measuring-unit-details.component';
import MeasuringUnitService from '@/entities/measuring-unit/measuring-unit.service';
import router from '@/router';
import AlertService from '@/shared/alert/alert.service';

const localVue = createLocalVue();
localVue.use(VueRouter);

config.initVueApp(localVue);
const store = config.initVueXStore(localVue);
localVue.component('font-awesome-icon', {});
localVue.component('router-link', {});

describe('Component Tests', () => {
  describe('MeasuringUnit Management Detail Component', () => {
    let wrapper: Wrapper<MeasuringUnitClass>;
    let comp: MeasuringUnitClass;
    let measuringUnitServiceStub: SinonStubbedInstance<MeasuringUnitService>;

    beforeEach(() => {
      measuringUnitServiceStub = sinon.createStubInstance<MeasuringUnitService>(MeasuringUnitService);

      wrapper = shallowMount<MeasuringUnitClass>(MeasuringUnitDetailComponent, {
        store,
        localVue,
        router,
        provide: { measuringUnitService: () => measuringUnitServiceStub, alertService: () => new AlertService() },
      });
      comp = wrapper.vm;
    });

    describe('OnInit', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        const foundMeasuringUnit = { id: 123 };
        measuringUnitServiceStub.find.resolves(foundMeasuringUnit);

        // WHEN
        comp.retrieveMeasuringUnit(123);
        await comp.$nextTick();

        // THEN
        expect(comp.measuringUnit).toBe(foundMeasuringUnit);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundMeasuringUnit = { id: 123 };
        measuringUnitServiceStub.find.resolves(foundMeasuringUnit);

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
