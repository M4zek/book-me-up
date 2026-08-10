import {Component, signal} from '@angular/core';
import {DatePipe} from "@angular/common";

interface Company {
  id:number;
  name:string;
  logo:string|null;
  owner:{
    name:string;
    avatar:string|null;
  };
  status: string;
  offersCount:number;
  reservationsCount:number;
  category:string;
  createdAt:Date;
  updatedAt:Date;
}

@Component({
  selector: 'app-company-management-list',
  imports: [
    DatePipe
  ],
  templateUrl: './company-management-list.component.html',
  styleUrl: './company-management-list.component.css'
})
export class CompanyManagementListComponent {

  isLoading = signal(true);
  companies = signal<Company[]>([]);

  ngOnInit(){
    this.loadCompanies();
  }


  loadCompanies(){
    this.isLoading.set(true);

    let test = []

    setTimeout(()=>{

      for (let i = 1; i < 20; i++) {
        test.push(
            {
              id:i,
              name:'Beauty Studio',
              logo:null,
              owner:{
                name:'Emma Wilson',
                avatar:null
              },
              status: i % 2 === 0 ? 'active' : 'not_active',
              offersCount:24,
              reservationsCount:1250,
              category:'Beauty',
              createdAt:new Date('2025-01-10'),
              updatedAt:new Date('2026-08-01')
            }
        );
      }

      this.companies.set(test);

      this.isLoading.set(false);
    },5000);
  }

  getInitials(value:string){
    return value
        .split(' ')
        .map(x=>x[0])
        .join('')
        .substring(0,2)
        .toUpperCase();
  }
}
