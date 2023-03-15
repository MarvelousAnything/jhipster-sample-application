/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import { ToastPlugin } from 'bootstrap-vue';

import * as config from '@/shared/config/config';
import MeasuringUnitComponent from '@/entities/measuring-unit/measuring-unit.vue';
import MeasuringUnitClass from '@/entities/measuring-unit/measuring-unit.component';
import MeasuringUnitService from '@/entities/measuring-unit/measuring-unit.service';
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
  describe('MeasuringUnit Management Component', () => {
    let wrapper: Wrapper<MeasuringUnitClass>;
    let comp: MeasuringUnitClass;
    let measuringUnitServiceStub: SinonStubbedInstance<MeasuringUnitService>;

    beforeEach(() => {
      measuringUnitServiceStub = sinon.createStubInstance<MeasuringUnitService>(MeasuringUnitService);
      measuringUnitServiceStub.retrieve.resolves({ headers: {} });

      wrapper = shallowMount<MeasuringUnitClass>(MeasuringUnitComponent, {
        store,
        localVue,
        stubs: { bModal: bModalStub as any },
        provide: {
          measuringUnitService: () => measuringUnitServiceStub,
          alertService: () => new AlertService(),
        },
      });
      comp = wrapper.vm;
    });

    it('Should call load all on init', async () => {
      // GIVEN
      measuringUnitServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

      // WHEN
      comp.retrieveAllMeasuringUnits();
      await comp.$nextTick();

      // THEN
      expect(measuringUnitServiceStub.retrieve.called).toBeTruthy();
      expect(comp.measuringUnits[0]).toEqual(expect.objectContaining({ id: 123 }));
    });
    it('Should call delete service on confirmDelete', async () => {
      // GIVEN
      measuringUnitServiceStub.delete.resolves({});

      // WHEN
      comp.prepareRemove({ id: 123 });
      expect(measuringUnitServiceStub.retrieve.callCount).toEqual(1);

      comp.removeMeasuringUnit();
      await comp.$nextTick();

      // THEN
      expect(measuringUnitServiceStub.delete.called).toBeTruthy();
      expect(measuringUnitServiceStub.retrieve.callCount).toEqual(2);
    });
  });
});
