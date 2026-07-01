export default interface User {
  id: string;
  email: string;
  name?: string;
  enabled: boolean;
  image?: string;
  roles?: [];
  updatedAt?: string;
  createdAt?: string;
  provider: string;
}