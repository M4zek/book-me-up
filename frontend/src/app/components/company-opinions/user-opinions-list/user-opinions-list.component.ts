import {Component} from '@angular/core';
import {NgForOf} from "@angular/common";

@Component({
  selector: 'app-user-opinions-list',
  imports: [
    NgForOf
  ],
  templateUrl: './user-opinions-list.component.html',
  styleUrl: './user-opinions-list.component.css'
})
export class UserOpinionsListComponent {
  loremIpsum: string = 'Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry\'s standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.\n' +
      '\n'

  opinions = [
    {
      user: 'John Doe 1',
      date: '2025-10-13',
      rating: 3,
      offerName: 'Offer name test 1',
      opinionMessage: this.loremIpsum,
    },
    {
      user: 'John Doe 2',
      date: '2025-10-13',
      rating: 1,
      offerName: 'Offer name test 2',
      opinionMessage: this.loremIpsum,
    },
    {
      user: 'John Doe 3',
      date: '2025-10-13',
      rating: 5,
      offerName: 'Offer name test 3',
      opinionMessage: this.loremIpsum,
    },
    {
      user: 'John Doe 4',
      date: '2025-10-13',
      rating: 2,
      offerName: 'Offer name test 4',
      opinionMessage: this.loremIpsum,
    },
  ]

  rows(n: number): number[] {
    return Array(n).fill(0).map((_, i) => i);
  }



}
