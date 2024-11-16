export interface MyResponse<T> {
  status: string;
  data: T;
  message: string;
}
