import {ErrorMessage} from "../response/error-response.model";

export enum Role {
    ROLE_ADMIN = "ROLE_ADMIN",
    ROLE_USER = "ROLE_USER",
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