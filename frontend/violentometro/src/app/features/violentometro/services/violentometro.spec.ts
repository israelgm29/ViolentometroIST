import { TestBed } from '@angular/core/testing';

import { Violentometro } from './violentometro';

describe('Violentometro', () => {
  let service: Violentometro;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Violentometro);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
