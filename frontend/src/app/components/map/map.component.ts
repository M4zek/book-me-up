import {Component, EventEmitter, Input, Output} from '@angular/core';
import {LeafletDirective, LeafletLayersDirective} from "@bluehalo/ngx-leaflet";
import {FormsModule} from "@angular/forms";
import {icon, latLng, Map, marker, Marker, tileLayer} from "leaflet";
import {HttpClient} from "@angular/common/http";
import {Address} from "../../model/gui/gui.model";
import {NgIf} from "@angular/common";
import {ToastService} from "../../service/toast.service";
import {timeout} from "rxjs";

@Component({
  selector: 'app-map',
  imports: [
    LeafletDirective,
    LeafletLayersDirective,
    FormsModule,
    NgIf
  ],
  templateUrl: './map.component.html',
  styleUrl: './map.component.css'
})
export class MapComponent {

  @Output() resultAddressFound = new EventEmitter<boolean>();

  @Input() set currentAddress(value: Address) {
      if(value !== this._currentAddress) {
          this._currentAddress = value;
          this.updateMapWithAddress(value);
      }
  }

  private _currentAddress: Address = {
    postalCode: '',
    city: '',
    street: '',
    buildingNumber: ''
  };

  get currentAddress(): Address {
    return this._currentAddress;
  }

  isAddressFound = false;
  private shouldEmitResult = false;

  map!: Map;
  options = {
    layers: [
      tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 19,
        attribution: '&copy; OpenStreetMap contributors'
      })
    ],
    zoom: 13,
    center: latLng(52.2297, 21.0122)
  };
  layers: Marker[] = [];

  constructor(private http: HttpClient, private toastService: ToastService) {}

  onMapReady(map: Map) {
    this.map = map;

    if (this.isAddressComplete(this.currentAddress)) {
      this.searchAddress(this.currentAddress, false);
    }
  }

  updateAddress(newAddress: Address) {
    this.shouldEmitResult = true;
    this.currentAddress = newAddress;
  }

  private updateMapWithAddress(address: Address) {
    if (this.map && this.isAddressComplete(address)) {
      this.searchAddress(address, this.shouldEmitResult);
      this.shouldEmitResult = false;
    }
  }

  private searchAddress(address: Address, emitResult: boolean) {
    if (!this.isAddressComplete(address)) return;

    const addressText = `${address.city} ${address.postalCode}, ${address.street} ${address.buildingNumber}`;
    const url = `https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(addressText)}`;

    this.http.get<any[]>(url).pipe(timeout(5000)).subscribe({
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

          this.isAddressFound = true;
        } else {
          this.toastService.show('Address not found on the map', 'error');
          this.isAddressFound = false;
        }
      },
      error: err => {
        console.log(err);
        this.toastService.show('Ups... Address on the map not found', 'error');
        this.isAddressFound = false;
      },
      complete: () => {
        if (emitResult) this.sendResult();
        setTimeout(()=>{
          this.map.invalidateSize()
        },100)
      }
    });
  }

  isAddressComplete(address: Address | null): boolean {
    if (!address) return false;
    return Object.values(address).every(value => value.trim() !== '');
  }

  private sendResult() {
    this.resultAddressFound.emit(this.isAddressFound);
  }
}
