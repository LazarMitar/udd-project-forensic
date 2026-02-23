import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { map, take } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';

export const guestGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  return auth.ensureAuthChecked().pipe(
    take(1),
    map(user => {
      if (!user) return true;
      router.navigate(['/']);
      return false;
    })
  );
};
