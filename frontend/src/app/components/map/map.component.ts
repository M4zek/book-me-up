import {Component, Input} from '@angular/core';
import {LeafletDirective, LeafletLayersDirective} from "@bluehalo/ngx-leaflet";
import {FormsModule} from "@angular/forms";
import {icon, latLng, Map, marker, Marker, tileLayer} from "leaflet";
import {HttpClient} from "@angular/common/http";

@Component({
  selector: 'app-map',
  imports: [
    LeafletDirective,
    LeafletLayersDirective,
    FormsModule
  ],
  templateUrl: './map.component.html',
  styleUrl: './map.component.css'
})
export class MapComponent {
  @Input() address = '';
  map!: Map;

  options = {
    layers: [
      tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 19,
        attribution: '&copy; OpenStreetMap contributors'
      })
    ],
    zoom: 13,
    center: latLng(52.2297, 21.0122) // Default Warsaw
  };
  layers: Marker[] = [];

  constructor(private http: HttpClient) {}

  onMapReady(map: Map) {
    this.map = map;
    this.searchAddress();
  }

  searchAddress() {
    if (!this.address.trim()) return;
    const url = `https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(this.address)}`;
    this.http.get<any[]>(url).subscribe({
      next: results => {
        if (results.length > 0) {
          const place = results[0];
          const lat = parseFloat(place.lat);
          const lon = parseFloat(place.lon);

          this.map.setView([lat, lon], 15);

          this.layers = [
            marker([lat, lon], {
              icon: icon({
                iconUrl: 'icons/localization_icon.svg',
              })
            })
          ];
        } else {
          alert('Address not found!');
        }
      },
      error: err => {
        console.error(err);
        alert('Error searching address!');
      },
      complete: () => {
        console.log('Searching address complete');
      }
    });
  }
}
