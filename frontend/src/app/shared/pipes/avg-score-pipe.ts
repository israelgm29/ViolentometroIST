import { Pipe, PipeTransform } from '@angular/core';
import {CriticalCase} from "../../models/case-reports";

@Pipe({
  name: 'avgScore',
  standalone: true
})
export class AvgScorePipe implements PipeTransform {

  transform(cases: CriticalCase[] | undefined | null): number {
    if (!cases || cases.length === 0) return 0;
    const total = cases.reduce((acc, c) => acc + c.riskScore, 0);
    return Math.round(total / cases.length);
  }

}
