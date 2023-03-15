import { Component, Vue, Inject } from 'vue-property-decorator';

import { IRecipeIngredient } from '@/shared/model/recipe-ingredient.model';
import RecipeIngredientService from './recipe-ingredient.service';
import AlertService from '@/shared/alert/alert.service';

@Component
export default class RecipeIngredientDetails extends Vue {
  @Inject('recipeIngredientService') private recipeIngredientService: () => RecipeIngredientService;
  @Inject('alertService') private alertService: () => AlertService;

  public recipeIngredient: IRecipeIngredient = {};

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.recipeIngredientId) {
        vm.retrieveRecipeIngredient(to.params.recipeIngredientId);
      }
    });
  }

  public retrieveRecipeIngredient(recipeIngredientId) {
    this.recipeIngredientService()
      .find(recipeIngredientId)
      .then(res => {
        this.recipeIngredient = res;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public previousState() {
    this.$router.go(-1);
  }
}
