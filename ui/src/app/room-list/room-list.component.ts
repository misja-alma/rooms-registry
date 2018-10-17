import { Component, OnInit } from '@angular/core';
import { RestService } from  '../rest.service';

@Component({
  selector: 'app-room-list',
  templateUrl: './room-list.component.html',
  styleUrls: ['./room-list.component.css']
})
export class RoomListComponent implements OnInit {
  private rooms:  Array<object> = [];

  constructor(private roomsService: RestService) { }

  ngOnInit() {
    this.getRooms();
  }

  public getRooms() {
    this.roomsService.getRooms().subscribe((data: Array<object>) => {
      this.rooms = data;
      console.log(data);
    });
  }

}

