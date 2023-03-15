/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import VueRouter from 'vue-router';

import * as config from '@/shared/config/config';
import AuthorDetailComponent from '@/entities/author/author-details.vue';
import AuthorClass from '@/entities/author/author-details.component';
import AuthorService from '@/entities/author/author.service';
import router from '@/router';
import AlertService from '@/shared/alert/alert.service';

const localVue = createLocalVue();
localVue.use(VueRouter);

config.initVueApp(localVue);
const store = config.initVueXStore(localVue);
localVue.component('font-awesome-icon', {});
localVue.component('router-link', {});

describe('Component Tests', () => {
  describe('Author Management Detail Component', () => {
    let wrapper: Wrapper<AuthorClass>;
    let comp: AuthorClass;
    let authorServiceStub: SinonStubbedInstance<AuthorService>;

    beforeEach(() => {
      authorServiceStub = sinon.createStubInstance<AuthorService>(AuthorService);

      wrapper = shallowMount<AuthorClass>(AuthorDetailComponent, {
        store,
        localVue,
        router,
        provide: { authorService: () => authorServiceStub, alertService: () => new AlertService() },
      });
      comp = wrapper.vm;
    });

    describe('OnInit', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        const foundAuthor = { id: 123 };
        authorServiceStub.find.resolves(foundAuthor);

        // WHEN
        comp.retrieveAuthor(123);
        await comp.$nextTick();

        // THEN
        expect(comp.author).toBe(foundAuthor);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundAuthor = { id: 123 };
        authorServiceStub.find.resolves(foundAuthor);

        // WHEN
        comp.beforeRouteEnter({ params: { authorId: 123 } }, null, cb => cb(comp));
        await comp.$nextTick();

        // THEN
        expect(comp.author).toBe(foundAuthor);
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
