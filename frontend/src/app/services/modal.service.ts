import {Injectable, signal} from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class ModalService {

    showResultsModal = signal<boolean>(false);

    openResultsModal() {
        this.showResultsModal.set(true);
    }

    closeResultsModal() {
        this.showResultsModal.set(false);
    }
}
