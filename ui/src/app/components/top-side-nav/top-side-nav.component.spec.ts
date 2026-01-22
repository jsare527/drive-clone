import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TopSideNavComponent } from './top-side-nav.component';

describe('TopSideNavComponent', () => {
  let component: TopSideNavComponent;
  let fixture: ComponentFixture<TopSideNavComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TopSideNavComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(TopSideNavComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
