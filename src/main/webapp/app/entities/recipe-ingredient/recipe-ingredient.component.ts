import { Component, Vue, Inject } from 'vue-property-decorator';
import Vue2Filters from 'vue2-filters';
import { IRecipeIngredient } from '@/shared/model/recipe-ingredient.model';

import RecipeIngredientService from './recipe-ingredient.service';
import AlertService from '@/shared/alert/alert.service';

@Component({
  mixins: [Vue2Filters.mixin],
})
export default class RecipeIngredient extends Vue {
  @Inject('recipeIngredientService') private recipeIngredientService: () => RecipeIngredientService;
  @Inject('alertService') private alertService: () => AlertService;

  public currentSearch = '';
  private removeId: number = null;

  public recipeIngredients: IRecipeIngredient[] = [];

  public isFetching = false;

  public mounted(): void {
    this.retrieveAllRecipeIngredients();
  }

  public search(query): void {
    if (!query) {
      return this.clear();
    }
    this.currentSearch = query;
    this.retrieveAllRecipeIngredients();
  }

  public clear(): void {
    this.currentSearch = '';
    this.retrieveAllRecipeIngredients();
  }

  public retrieveAllRecipeIngredients(): void {
    this.isFetching = true;
    if (this.currentSearch) {
      this.recipeIngredientService()
        .search(this.currentSearch)
        .then(
          res => {
            this.recipeIngredients = res;
            this.isFetching = false;
          },
          err => {
            this.isFetching = false;
            this.alertService().showHttpError(this, err.response);
          }
        );
      return;
    }
    this.recipeIngredientService()
      .retrieve()
      .then(
        res => {
          this.recipeIngredients = res.data;
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

  public prepareRemove(instance: IRecipeIngredient): void {
    this.removeId = instance.id;
    if (<any>this.$refs.removeEntity) {
      (<any>this.$refs.removeEntity).show();
    }
  }

  public removeRecipeIngredient(): void {
    this.recipeIngredientService()
      .delete(this.removeId)
      .then(() => {
        const message = 'A RecipeIngredient is deleted with identifier ' + this.removeId;
        this.$bvToast.toast(message.toString(), {
          toaster: 'b-toaster-top-center',
          title: 'Info',
          variant: 'danger',
          solid: true,
          autoHideDelay: 5000,
        });
        this.removeId = null;
        this.retrieveAllRecipeIngredients();
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
