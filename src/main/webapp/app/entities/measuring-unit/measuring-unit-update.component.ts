import { Component, Vue, Inject } from 'vue-property-decorator';

import { required } from 'vuelidate/lib/validators';

import AlertService from '@/shared/alert/alert.service';

import { IMeasuringUnit, MeasuringUnit } from '@/shared/model/measuring-unit.model';
import MeasuringUnitService from './measuring-unit.service';

const validations: any = {
  measuringUnit: {
    name: {
      required,
    },
    abbreviation: {},
  },
};

@Component({
  validations,
})
export default class MeasuringUnitUpdate extends Vue {
  @Inject('measuringUnitService') private measuringUnitService: () => MeasuringUnitService;
  @Inject('alertService') private alertService: () => AlertService;

  public measuringUnit: IMeasuringUnit = new MeasuringUnit();
  public isSaving = false;
  public currentLanguage = '';

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.measuringUnitId) {
        vm.retrieveMeasuringUnit(to.params.measuringUnitId);
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
    if (this.measuringUnit.id) {
      this.measuringUnitService()
        .update(this.measuringUnit)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = 'A MeasuringUnit is updated with identifier ' + param.id;
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
      this.measuringUnitService()
        .create(this.measuringUnit)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = 'A MeasuringUnit is created with identifier ' + param.id;
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

  public retrieveMeasuringUnit(measuringUnitId): void {
    this.measuringUnitService()
      .find(measuringUnitId)
      .then(res => {
        this.measuringUnit = res;
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
