import {ErrorMessage} from "./error.model";

export enum Role {
    ROLE_ADMIN = "ROLE_ADMIN",
    ROLE_USER = "ROLE_USER",
    ROLE_TEST = "ROLE_TEST",
}

export interface LoginWrapper {
    success: boolean,
    errorMessage?: ErrorMessage,
    loggedUser?: UserContextModel
}

export interface AuthRequest{
    email: string;
    password: string;
}

export interface UserContextModel {
    id: number;
    roles: Role[],
    token: string,
    refreshToken: string
}

export interface UserContextWrapper{
    loggedIn: boolean,
    userContext?: UserContextModel
}

export interface RefreshTokenResponse {
    accessToken: string,
    refreshToken: string
}

export interface UserDataRequest {
    firstName: string,
    lastName: string,
    dateOfBirth: string,
    phoneNumber: string,
    photo: string | null,
}

export interface UserAccountRequest{
    addressEmail: string;
    password: string;
    userData: UserDataRequest;
}