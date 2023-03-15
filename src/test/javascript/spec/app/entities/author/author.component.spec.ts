/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import { ToastPlugin } from 'bootstrap-vue';

import * as config from '@/shared/config/config';
import AuthorComponent from '@/entities/author/author.vue';
import AuthorClass from '@/entities/author/author.component';
import AuthorService from '@/entities/author/author.service';
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
  describe('Author Management Component', () => {
    let wrapper: Wrapper<AuthorClass>;
    let comp: AuthorClass;
    let authorServiceStub: SinonStubbedInstance<AuthorService>;

    beforeEach(() => {
      authorServiceStub = sinon.createStubInstance<AuthorService>(AuthorService);
      authorServiceStub.retrieve.resolves({ headers: {} });

      wrapper = shallowMount<AuthorClass>(AuthorComponent, {
        store,
        localVue,
        stubs: { bModal: bModalStub as any },
        provide: {
          authorService: () => authorServiceStub,
          alertService: () => new AlertService(),
        },
      });
      comp = wrapper.vm;
    });

    it('Should call load all on init', async () => {
      // GIVEN
      authorServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

      // WHEN
      comp.retrieveAllAuthors();
      await comp.$nextTick();

      // THEN
      expect(authorServiceStub.retrieve.called).toBeTruthy();
      expect(comp.authors[0]).toEqual(expect.objectContaining({ id: 123 }));
    });
    it('Should call delete service on confirmDelete', async () => {
      // GIVEN
      authorServiceStub.delete.resolves({});

      // WHEN
      comp.prepareRemove({ id: 123 });
      expect(authorServiceStub.retrieve.callCount).toEqual(1);

      comp.removeAuthor();
      await comp.$nextTick();

      // THEN
      expect(authorServiceStub.delete.called).toBeTruthy();
      expect(authorServiceStub.retrieve.callCount).toEqual(2);
    });
  });
});
