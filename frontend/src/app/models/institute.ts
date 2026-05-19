export interface InterfaceInstitute {
    hasLogo: boolean;
    id: number;
    code: string;
    name: string;
    shortName: string;
    address: string;
    city: string;
    province: string;
    country: string;
    phone: string;
    email: string;
    webUrl: string;
    status: boolean;
    createdDate?: Date | string;
}