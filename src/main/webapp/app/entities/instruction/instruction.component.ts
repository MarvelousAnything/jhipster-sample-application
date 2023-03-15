import { Component, Vue, Inject } from 'vue-property-decorator';
import Vue2Filters from 'vue2-filters';
import { IInstruction } from '@/shared/model/instruction.model';

import InstructionService from './instruction.service';
import AlertService from '@/shared/alert/alert.service';

@Component({
  mixins: [Vue2Filters.mixin],
})
export default class Instruction extends Vue {
  @Inject('instructionService') private instructionService: () => InstructionService;
  @Inject('alertService') private alertService: () => AlertService;

  public currentSearch = '';
  private removeId: number = null;

  public instructions: IInstruction[] = [];

  public isFetching = false;

  public mounted(): void {
    this.retrieveAllInstructions();
  }

  public search(query): void {
    if (!query) {
      return this.clear();
    }
    this.currentSearch = query;
    this.retrieveAllInstructions();
  }

  public clear(): void {
    this.currentSearch = '';
    this.retrieveAllInstructions();
  }

  public retrieveAllInstructions(): void {
    this.isFetching = true;
    if (this.currentSearch) {
      this.instructionService()
        .search(this.currentSearch)
        .then(
          res => {
            this.instructions = res;
            this.isFetching = false;
          },
          err => {
            this.isFetching = false;
            this.alertService().showHttpError(this, err.response);
          }
        );
      return;
    }
    this.instructionService()
      .retrieve()
      .then(
        res => {
          this.instructions = res.data;
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

  public prepareRemove(instance: IInstruction): void {
    this.removeId = instance.id;
    if (<any>this.$refs.removeEntity) {
      (<any>this.$refs.removeEntity).show();
    }
  }

  public removeInstruction(): void {
    this.instructionService()
      .delete(this.removeId)
      .then(() => {
        const message = 'A Instruction is deleted with identifier ' + this.removeId;
        this.$bvToast.toast(message.toString(), {
          toaster: 'b-toaster-top-center',
          title: 'Info',
          variant: 'danger',
          solid: true,
          autoHideDelay: 5000,
        });
        this.removeId = null;
        this.retrieveAllInstructions();
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
