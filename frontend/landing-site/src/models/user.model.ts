export interface IUser {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  mfaEnabled: boolean;
  status: EUserStatus;
  address: IUserAddress;
}

export interface IUserAddress {
  street1: string;
  street2: string;
  postalCode: string;
  city: string;
  state: string;
  country: string;
}

export enum EUserStatus {
  PENDING_MFA,
  ACTIVE,
  SUSPENDED,
  BANNED,
}
