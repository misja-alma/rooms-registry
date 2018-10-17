import { Injectable } from '@angular/core';
import { HttpClient} from  '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class RestService {

  constructor(private  httpClient:  HttpClient) {}

  getRooms(){
    return  this.httpClient.get(`http://localhost:9000/api/rooms`);
  }
}

