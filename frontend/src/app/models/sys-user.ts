export interface SysUserInterface {

    id: number;
    firstname: string;
    secondname?: string;
    firstLastname: string;
    secondLastname?: string;
    dni: string;
    phone?: string;
    address?: string;
    status: boolean;
    createdAt?: string;
    updatedAt?: string;
    email: string;

    role: {
        id: number;
        name: string;
    };
    institute: {
        id: number;
        name: string;
    };
}

export interface ProfileResponse {
    id: number;
    firstname: string;
    secondname?: string;
    firstLastname: string;
    secondLastname?: string;
    email: string;
    phone?: string;
    address?: string;
    dni: string;
    role: string;
    institute: string;
    status: boolean;
}

export interface UpdateProfileDTO {
    firstname:       string;
    secondname?:     string;
    firstLastname:   string;
    secondLastname?: string;
    email:           string;
    phone?:          string;
    address?:        string;
}

export interface ChangePasswordDTO {
    currentPassword: string;
    newPassword:     string;
    confirmPassword: string;
}