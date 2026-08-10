import {Role} from "../http/auth.model";

export const ROLE_OPTIONS = [
    {
        label: 'User',
        value: Role.ROLE_USER
    },
    {
        label: 'Admin',
        value: Role.ROLE_ADMIN
    },
    {
        label: 'Admin Viewer',
        value: Role.ROLE_ADMIN_VIEWER
    }
] as const;