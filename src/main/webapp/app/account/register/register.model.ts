export class Registration {
  constructor(
    public roleChoice: string,
    public libraryId: number,
    public firstName: string,
    public lastName: string,
    public dateOfBirth: string,
    public phoneNumber: string,
    public streetAddress: string,
    public postalCode: string,
    public city: string,
    public stateProvince: string,
    public login: string,
    public email: string,
    public password: string,
    public langKey: string,
  ) {}
}
