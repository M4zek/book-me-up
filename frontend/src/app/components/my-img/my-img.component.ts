import {Component, computed, input, Signal} from '@angular/core';
import {FileType} from "../../model/http/company.model";
import {NgOptimizedImage} from "@angular/common";

@Component({
  selector: 'app-my-img',
  imports: [
    NgOptimizedImage
  ],
  templateUrl: './my-img.component.html',
  styleUrl: './my-img.component.css'
})
export class MyImgComponent {
  protected readonly FileType = FileType;

  src = input<string | undefined>('test');
  alt = input<string>("Image");
  type = input<FileType | undefined>(undefined);


  constructor() {
  }

  finalSrc: Signal<string> = computed(() => {
    let url = this.src();
    const type = this.type();

    if(!url || url.trim() === ''){
      switch (type){
        case FileType.IMG_USER_AVATAR:
          return '/images/user_default_avatar.png';

        case FileType.IMG_COMPANY_LOGO:
          return '/images/default_logo_company.png';

        default:
          return '';
      }
    }
    return url;
  });



}
