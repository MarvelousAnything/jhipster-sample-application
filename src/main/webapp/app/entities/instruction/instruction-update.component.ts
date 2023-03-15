import { Component, Vue, Inject } from 'vue-property-decorator';

import AlertService from '@/shared/alert/alert.service';

import RecipeService from '@/entities/recipe/recipe.service';
import { IRecipe } from '@/shared/model/recipe.model';

import { IInstruction, Instruction } from '@/shared/model/instruction.model';
import InstructionService from './instruction.service';

const validations: any = {
  instruction: {
    stepNumber: {},
    description: {},
  },
};

@Component({
  validations,
})
export default class InstructionUpdate extends Vue {
  @Inject('instructionService') private instructionService: () => InstructionService;
  @Inject('alertService') private alertService: () => AlertService;

  public instruction: IInstruction = new Instruction();

  @Inject('recipeService') private recipeService: () => RecipeService;

  public recipes: IRecipe[] = [];
  public isSaving = false;
  public currentLanguage = '';

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.instructionId) {
        vm.retrieveInstruction(to.params.instructionId);
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
    if (this.instruction.id) {
      this.instructionService()
        .update(this.instruction)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = 'A Instruction is updated with identifier ' + param.id;
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
      this.instructionService()
        .create(this.instruction)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = 'A Instruction is created with identifier ' + param.id;
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

  public retrieveInstruction(instructionId): void {
    this.instructionService()
      .find(instructionId)
      .then(res => {
        this.instruction = res;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public previousState(): void {
    this.$router.go(-1);
  }

  public initRelationships(): void {
    this.recipeService()
      .retrieve()
      .then(res => {
        this.recipes = res.data;
      });
  }
}
