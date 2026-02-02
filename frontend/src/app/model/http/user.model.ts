

export interface UserResponse {
    id: number;
    firstName: string;
    lastName: string;
    email: string;
    birthdate: string;
    phoneNumber: string;
    avatar: string | null;
}

export interface UserHireDetails {
    id: number;
    firstName: string;
    lastName: string;
    companyIds: number[];
    avatar: string | null;
}
