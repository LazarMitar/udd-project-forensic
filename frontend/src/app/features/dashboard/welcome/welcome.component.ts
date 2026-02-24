import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-welcome',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="welcome">
      <h2>Dobrodošli</h2>
      <p>Imate pristup svim funkcionalnostima sistema u skladu sa vašom ulogom.</p>
      <nav class="quick-links">
        <a routerLink="/dashboard/upload" class="link">Upload forenzičkog izveštaja</a>
        <a routerLink="/dashboard/search" class="link">Pretraga izveštaja</a>
      </nav>
      <p class="hint">Ovde će kasnije biti linkovi ka pretrazi i statistici.</p>
    </div>
  `,
  styles: [`
    .welcome {
      padding: 1rem 0;
    }
    .welcome h2 {
      margin: 0 0 0.5rem 0;
      font-size: 1.5rem;
      font-weight: 600;
      color: var(--text-primary, #1a1a1a);
    }
    .welcome p {
      margin: 0 0 0.5rem 0;
      color: var(--text-secondary, #555);
    }
    .quick-links {
      margin-top: 1rem;
    }
    .quick-links .link {
      display: inline-block;
      padding: 0.5rem 0;
      color: var(--primary, #2563eb);
      font-weight: 500;
      text-decoration: none;
    }
    .quick-links .link:hover {
      text-decoration: underline;
    }
    .welcome .hint {
      margin-top: 1rem;
      font-size: 0.9rem;
      color: #94a3b8;
    }
  `]
})
export class WelcomeComponent {}
