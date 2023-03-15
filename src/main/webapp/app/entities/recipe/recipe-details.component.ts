import { Component, Vue, Inject } from 'vue-property-decorator';

import { IRecipe } from '@/shared/model/recipe.model';
import RecipeService from './recipe.service';
import AlertService from '@/shared/alert/alert.service';

@Component
export default class RecipeDetails extends Vue {
  @Inject('recipeService') private recipeService: () => RecipeService;
  @Inject('alertService') private alertService: () => AlertService;

  public recipe: IRecipe = {};

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.recipeId) {
        vm.retrieveRecipe(to.params.recipeId);
      }
    });
  }

  public retrieveRecipe(recipeId) {
    this.recipeService()
      .find(recipeId)
      .then(res => {
        this.recipe = res;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public previousState() {
    this.$router.go(-1);
  }
}
