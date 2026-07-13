

export interface UserResponse {
    id: number;
    firstName: string;
    lastName: string;
    email: string;
    birthdate: string;
    phoneNumber: string;
    avatar_url: string;
}

export interface UserHireDetails {
    id: number;
    firstName: string;
    lastName: string;
    companyIds: number[];
    avatar_url: string;
}
