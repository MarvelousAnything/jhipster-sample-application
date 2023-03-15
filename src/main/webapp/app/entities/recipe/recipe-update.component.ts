import { Component, Vue, Inject } from 'vue-property-decorator';

import { required } from 'vuelidate/lib/validators';

import AlertService from '@/shared/alert/alert.service';

import RecipeIngredientService from '@/entities/recipe-ingredient/recipe-ingredient.service';
import { IRecipeIngredient } from '@/shared/model/recipe-ingredient.model';

import InstructionService from '@/entities/instruction/instruction.service';
import { IInstruction } from '@/shared/model/instruction.model';

import AuthorService from '@/entities/author/author.service';
import { IAuthor } from '@/shared/model/author.model';

import { IRecipe, Recipe } from '@/shared/model/recipe.model';
import RecipeService from './recipe.service';

const validations: any = {
  recipe: {
    name: {
      required,
    },
    description: {},
    prepTime: {},
    cookTime: {},
    servings: {},
    imageUrl: {},
  },
};

@Component({
  validations,
})
export default class RecipeUpdate extends Vue {
  @Inject('recipeService') private recipeService: () => RecipeService;
  @Inject('alertService') private alertService: () => AlertService;

  public recipe: IRecipe = new Recipe();

  @Inject('recipeIngredientService') private recipeIngredientService: () => RecipeIngredientService;

  public recipeIngredients: IRecipeIngredient[] = [];

  @Inject('instructionService') private instructionService: () => InstructionService;

  public instructions: IInstruction[] = [];

  @Inject('authorService') private authorService: () => AuthorService;

  public authors: IAuthor[] = [];
  public isSaving = false;
  public currentLanguage = '';

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.recipeId) {
        vm.retrieveRecipe(to.params.recipeId);
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
    if (this.recipe.id) {
      this.recipeService()
        .update(this.recipe)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = 'A Recipe is updated with identifier ' + param.id;
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
      this.recipeService()
        .create(this.recipe)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = 'A Recipe is created with identifier ' + param.id;
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

  public retrieveRecipe(recipeId): void {
    this.recipeService()
      .find(recipeId)
      .then(res => {
        this.recipe = res;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public previousState(): void {
    this.$router.go(-1);
  }

  public initRelationships(): void {
    this.recipeIngredientService()
      .retrieve()
      .then(res => {
        this.recipeIngredients = res.data;
      });
    this.instructionService()
      .retrieve()
      .then(res => {
        this.instructions = res.data;
      });
    this.authorService()
      .retrieve()
      .then(res => {
        this.authors = res.data;
      });
  }
}
