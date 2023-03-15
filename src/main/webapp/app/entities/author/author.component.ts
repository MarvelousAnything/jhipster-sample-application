import { Component, Vue, Inject } from 'vue-property-decorator';
import Vue2Filters from 'vue2-filters';
import { IAuthor } from '@/shared/model/author.model';

import AuthorService from './author.service';
import AlertService from '@/shared/alert/alert.service';

@Component({
  mixins: [Vue2Filters.mixin],
})
export default class Author extends Vue {
  @Inject('authorService') private authorService: () => AuthorService;
  @Inject('alertService') private alertService: () => AlertService;

  public currentSearch = '';
  private removeId: number = null;

  public authors: IAuthor[] = [];

  public isFetching = false;

  public mounted(): void {
    this.retrieveAllAuthors();
  }

  public search(query): void {
    if (!query) {
      return this.clear();
    }
    this.currentSearch = query;
    this.retrieveAllAuthors();
  }

  public clear(): void {
    this.currentSearch = '';
    this.retrieveAllAuthors();
  }

  public retrieveAllAuthors(): void {
    this.isFetching = true;
    if (this.currentSearch) {
      this.authorService()
        .search(this.currentSearch)
        .then(
          res => {
            this.authors = res;
            this.isFetching = false;
          },
          err => {
            this.isFetching = false;
            this.alertService().showHttpError(this, err.response);
          }
        );
      return;
    }
    this.authorService()
      .retrieve()
      .then(
        res => {
          this.authors = res.data;
          this.isFetching = false;
        },
        err => {
          this.isFetching = false;
          this.alertService().showHttpError(this, err.response);
        }
      );
  }

  public handleSyncList(): void {
    this.clear();
  }

  public prepareRemove(instance: IAuthor): void {
    this.removeId = instance.id;
    if (<any>this.$refs.removeEntity) {
      (<any>this.$refs.removeEntity).show();
    }
  }

  public removeAuthor(): void {
    this.authorService()
      .delete(this.removeId)
      .then(() => {
        const message = 'A Author is deleted with identifier ' + this.removeId;
        this.$bvToast.toast(message.toString(), {
          toaster: 'b-toaster-top-center',
          title: 'Info',
          variant: 'danger',
          solid: true,
          autoHideDelay: 5000,
        });
        this.removeId = null;
        this.retrieveAllAuthors();
        this.closeDialog();
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public closeDialog(): void {
    (<any>this.$refs.removeEntity).hide();
  }
}
