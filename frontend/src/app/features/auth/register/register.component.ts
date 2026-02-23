import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);

  form = this.fb.nonNullable.group({
    username: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(50)]],
    password: ['', [Validators.required, Validators.minLength(4)]],
    role: ['']
  });
  error = '';
  success = false;
  loading = false;

  onSubmit(): void {
    if (this.form.invalid || this.loading) return;
    this.error = '';
    this.success = false;
    this.loading = true;
    const payload = {
      username: this.form.get('username')!.value,
      password: this.form.get('password')!.value,
      ...(this.form.get('role')?.value ? { role: this.form.get('role')!.value } : {})
    };
    this.auth.register(payload).subscribe({
      next: () => {
        this.loading = false;
        this.success = true;
      },
      error: (err: { error?: string }) => {
        this.loading = false;
        this.error = err.error && typeof err.error === 'string' ? err.error : 'Registracija nije uspela.';
      }
    });
  }
}
