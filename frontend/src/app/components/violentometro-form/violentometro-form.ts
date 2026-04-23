import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { FooterComponent } from '../../shared/footer/footer.component';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

@Component({
  selector: 'app-violentometro-form',
  standalone: true,
  imports: [CommonModule, MatIconModule, FooterComponent],
  templateUrl: './violentometro-form.html',
  styleUrls: ['./violentometro-form.scss']
})
export class ViolentometroFormComponent {
  videoUrl1: SafeResourceUrl;
  videoUrl2: SafeResourceUrl;

  constructor(private sanitizer: DomSanitizer) {
    // Example YouTube videos about violence prevention
    // Replace these with actual institutional videos
    this.videoUrl1 = this.sanitizer.bypassSecurityTrustResourceUrl(
      'https://www.youtube.com/embed/nNjPCvI68fs'
    );
    this.videoUrl2 = this.sanitizer.bypassSecurityTrustResourceUrl(
      'https://www.youtube.com/embed/KQaXPiusEt0'
    );
  }
}
