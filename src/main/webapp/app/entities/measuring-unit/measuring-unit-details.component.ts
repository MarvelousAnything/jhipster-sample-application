import { Component, Vue, Inject } from 'vue-property-decorator';

import { IMeasuringUnit } from '@/shared/model/measuring-unit.model';
import MeasuringUnitService from './measuring-unit.service';
import AlertService from '@/shared/alert/alert.service';

@Component
export default class MeasuringUnitDetails extends Vue {
  @Inject('measuringUnitService') private measuringUnitService: () => MeasuringUnitService;
  @Inject('alertService') private alertService: () => AlertService;

  public measuringUnit: IMeasuringUnit = {};

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.measuringUnitId) {
        vm.retrieveMeasuringUnit(to.params.measuringUnitId);
      }
    });
  }

  public retrieveMeasuringUnit(measuringUnitId) {
    this.measuringUnitService()
      .find(measuringUnitId)
      .then(res => {
        this.measuringUnit = res;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public previousState() {
    this.$router.go(-1);
  }
}
