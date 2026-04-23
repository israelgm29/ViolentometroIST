export interface AppUserResponse {
    id: number;
    dni: string;
    gender: MasterCatalog;
    birthdate: Date;
    institute: MasterCatalog;
    region: MasterCatalog;
    disability: MasterCatalog;
    ethnicity: MasterCatalog;
    status: boolean;
}

export interface AppUserRequest {
    dni: string;
    idGender: string;
    birthdate: string;
    idInstitute: number;
    idRegion: number;
    idDisability: number;
    idEthnicity: number;
}

export interface MasterCatalog {
    id: number;
    name: string;
}