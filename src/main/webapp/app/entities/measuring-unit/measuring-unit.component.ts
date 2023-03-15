import { Component, Vue, Inject } from 'vue-property-decorator';
import Vue2Filters from 'vue2-filters';
import { IMeasuringUnit } from '@/shared/model/measuring-unit.model';

import MeasuringUnitService from './measuring-unit.service';
import AlertService from '@/shared/alert/alert.service';

@Component({
  mixins: [Vue2Filters.mixin],
})
export default class MeasuringUnit extends Vue {
  @Inject('measuringUnitService') private measuringUnitService: () => MeasuringUnitService;
  @Inject('alertService') private alertService: () => AlertService;

  public currentSearch = '';
  private removeId: number = null;

  public measuringUnits: IMeasuringUnit[] = [];

  public isFetching = false;

  public mounted(): void {
    this.retrieveAllMeasuringUnits();
  }

  public search(query): void {
    if (!query) {
      return this.clear();
    }
    this.currentSearch = query;
    this.retrieveAllMeasuringUnits();
  }

  public clear(): void {
    this.currentSearch = '';
    this.retrieveAllMeasuringUnits();
  }

  public retrieveAllMeasuringUnits(): void {
    this.isFetching = true;
    if (this.currentSearch) {
      this.measuringUnitService()
        .search(this.currentSearch)
        .then(
          res => {
            this.measuringUnits = res;
            this.isFetching = false;
          },
          err => {
            this.isFetching = false;
            this.alertService().showHttpError(this, err.response);
          }
        );
      return;
    }
    this.measuringUnitService()
      .retrieve()
      .then(
        res => {
          this.measuringUnits = res.data;
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

  public prepareRemove(instance: IMeasuringUnit): void {
    this.removeId = instance.id;
    if (<any>this.$refs.removeEntity) {
      (<any>this.$refs.removeEntity).show();
    }
  }

  public removeMeasuringUnit(): void {
    this.measuringUnitService()
      .delete(this.removeId)
      .then(() => {
        const message = 'A MeasuringUnit is deleted with identifier ' + this.removeId;
        this.$bvToast.toast(message.toString(), {
          toaster: 'b-toaster-top-center',
          title: 'Info',
          variant: 'danger',
          solid: true,
          autoHideDelay: 5000,
        });
        this.removeId = null;
        this.retrieveAllMeasuringUnits();
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
