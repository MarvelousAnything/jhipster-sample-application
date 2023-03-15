import { Component, Vue, Inject } from 'vue-property-decorator';

import { IAuthor } from '@/shared/model/author.model';
import AuthorService from './author.service';
import AlertService from '@/shared/alert/alert.service';

@Component
export default class AuthorDetails extends Vue {
  @Inject('authorService') private authorService: () => AuthorService;
  @Inject('alertService') private alertService: () => AlertService;

  public author: IAuthor = {};

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.authorId) {
        vm.retrieveAuthor(to.params.authorId);
      }
    });
  }

  public retrieveAuthor(authorId) {
    this.authorService()
      .find(authorId)
      .then(res => {
        this.author = res;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public previousState() {
    this.$router.go(-1);
  }
}
