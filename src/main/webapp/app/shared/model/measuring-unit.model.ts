export interface IMeasuringUnit {
  id?: number;
  name?: string;
  abbreviation?: string | null;
}

export class MeasuringUnit implements IMeasuringUnit {
  constructor(public id?: number, public name?: string, public abbreviation?: string | null) {}
}
