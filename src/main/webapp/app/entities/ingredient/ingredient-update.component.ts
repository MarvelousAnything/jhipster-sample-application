import { Component, Vue, Inject } from 'vue-property-decorator';

import { required } from 'vuelidate/lib/validators';

import AlertService from '@/shared/alert/alert.service';

import { IIngredient, Ingredient } from '@/shared/model/ingredient.model';
import IngredientService from './ingredient.service';

const validations: any = {
  ingredient: {
    name: {
      required,
    },
  },
};

@Component({
  validations,
})
export default class IngredientUpdate extends Vue {
  @Inject('ingredientService') private ingredientService: () => IngredientService;
  @Inject('alertService') private alertService: () => AlertService;

  public ingredient: IIngredient = new Ingredient();
  public isSaving = false;
  public currentLanguage = '';

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.ingredientId) {
        vm.retrieveIngredient(to.params.ingredientId);
      }
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
    if (this.ingredient.id) {
      this.ingredientService()
        .update(this.ingredient)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = 'A Ingredient is updated with identifier ' + param.id;
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
      this.ingredientService()
        .create(this.ingredient)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = 'A Ingredient is created with identifier ' + param.id;
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

  public retrieveIngredient(ingredientId): void {
    this.ingredientService()
      .find(ingredientId)
      .then(res => {
        this.ingredient = res;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public previousState(): void {
    this.$router.go(-1);
  }

  public initRelationships(): void {}
}
