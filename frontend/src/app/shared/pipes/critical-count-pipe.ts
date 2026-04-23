import { Pipe, PipeTransform } from '@angular/core';
import {CriticalCase} from "../../models/case-reports";

@Pipe({
  name: 'criticalCount',
  standalone: true
})
export class CriticalCountPipe implements PipeTransform {

  transform(cases: CriticalCase[] | undefined | null, level: string): number {
    if (!cases) return 0;
    return cases.filter(c => c.riskLevel === level).length;
  }

}
