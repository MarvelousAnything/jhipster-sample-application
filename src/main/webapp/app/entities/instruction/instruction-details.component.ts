import { Component, Vue, Inject } from 'vue-property-decorator';

import { IInstruction } from '@/shared/model/instruction.model';
import InstructionService from './instruction.service';
import AlertService from '@/shared/alert/alert.service';

@Component
export default class InstructionDetails extends Vue {
  @Inject('instructionService') private instructionService: () => InstructionService;
  @Inject('alertService') private alertService: () => AlertService;

  public instruction: IInstruction = {};

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.instructionId) {
        vm.retrieveInstruction(to.params.instructionId);
      }
    });
  }

  public retrieveInstruction(instructionId) {
    this.instructionService()
      .find(instructionId)
      .then(res => {
        this.instruction = res;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public previousState() {
    this.$router.go(-1);
  }
}
