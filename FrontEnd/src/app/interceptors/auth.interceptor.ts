import { HttpHeaders, HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  let modifiedReq: any;
  const token = localStorage.getItem('token');
  // console.log('Interceptor called');
  let headers = new HttpHeaders();
  if (token) {
    modifiedReq = req.clone({
      headers: headers.set('Authorization', `Bearer ${token}`),
      // .set('Content-Type', 'application/json'),
    });
    // console.log('modifiedReq', modifiedReq);
    return next(modifiedReq);
  } else {
    return next(req);
  }
};
