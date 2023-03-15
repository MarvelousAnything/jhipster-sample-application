import { Component, Vue, Inject } from 'vue-property-decorator';

import AlertService from '@/shared/alert/alert.service';

import IngredientService from '@/entities/ingredient/ingredient.service';
import { IIngredient } from '@/shared/model/ingredient.model';

import MeasuringUnitService from '@/entities/measuring-unit/measuring-unit.service';
import { IMeasuringUnit } from '@/shared/model/measuring-unit.model';

import RecipeService from '@/entities/recipe/recipe.service';
import { IRecipe } from '@/shared/model/recipe.model';

import { IRecipeIngredient, RecipeIngredient } from '@/shared/model/recipe-ingredient.model';
import RecipeIngredientService from './recipe-ingredient.service';

const validations: any = {
  recipeIngredient: {
    amount: {},
  },
};

@Component({
  validations,
})
export default class RecipeIngredientUpdate extends Vue {
  @Inject('recipeIngredientService') private recipeIngredientService: () => RecipeIngredientService;
  @Inject('alertService') private alertService: () => AlertService;

  public recipeIngredient: IRecipeIngredient = new RecipeIngredient();

  @Inject('ingredientService') private ingredientService: () => IngredientService;

  public ingredients: IIngredient[] = [];

  @Inject('measuringUnitService') private measuringUnitService: () => MeasuringUnitService;

  public measuringUnits: IMeasuringUnit[] = [];

  @Inject('recipeService') private recipeService: () => RecipeService;

  public recipes: IRecipe[] = [];
  public isSaving = false;
  public currentLanguage = '';

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.recipeIngredientId) {
        vm.retrieveRecipeIngredient(to.params.recipeIngredientId);
      }
      vm.initRelationships();
    });
  }

  created(): void {
    this.currentLanguage = this.$store.getters.currentLanguage;
    this.$store.watch(
      () => this.$store.getters.currentLanguage,
      () => {
        this.currentLanguage = this.$store.getters.currentLanguage;
      }
    );
  }

  public save(): void {
    this.isSaving = true;
    if (this.recipeIngredient.id) {
      this.recipeIngredientService()
        .update(this.recipeIngredient)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = 'A RecipeIngredient is updated with identifier ' + param.id;
          return (this.$root as any).$bvToast.toast(message.toString(), {
            toaster: 'b-toaster-top-center',
            title: 'Info',
            variant: 'info',
            solid: true,
            autoHideDelay: 5000,
          });
        })
        .catch(error => {
          this.isSaving = false;
          this.alertService().showHttpError(this, error.response);
        });
    } else {
      this.recipeIngredientService()
        .create(this.recipeIngredient)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = 'A RecipeIngredient is created with identifier ' + param.id;
          (this.$root as any).$bvToast.toast(message.toString(), {
            toaster: 'b-toaster-top-center',
            title: 'Success',
            variant: 'success',
            solid: true,
            autoHideDelay: 5000,
          });
        })
        .catch(error => {
          this.isSaving = false;
          this.alertService().showHttpError(this, error.response);
        });
    }
  }

  public retrieveRecipeIngredient(recipeIngredientId): void {
    this.recipeIngredientService()
      .find(recipeIngredientId)
      .then(res => {
        this.recipeIngredient = res;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public previousState(): void {
    this.$router.go(-1);
  }

  public initRelationships(): void {
    this.ingredientService()
      .retrieve()
      .then(res => {
        this.ingredients = res.data;
      });
    this.measuringUnitService()
      .retrieve()
      .then(res => {
        this.measuringUnits = res.data;
      });
    this.recipeService()
      .retrieve()
      .then(res => {
        this.recipes = res.data;
      });
  }
}
