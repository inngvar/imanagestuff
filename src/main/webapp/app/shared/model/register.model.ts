export interface IRegister {
  login?: string;
  firstName?: string;
  middleName?: string;
  lastName?: string;
  email?: string;
  password?: string;
}

export const defaultRegisterValue: Readonly<IRegister> = {
  login: '',
  firstName: '',
  middleName: '',
  lastName: '',
  email: '',
  password: '',
};
