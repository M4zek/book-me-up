import {UserStatus} from "../http/auth.model";

export const USER_STATUS_OPTIONS = [
    { label: 'Active', value: UserStatus.ACTIVE },
    { label: 'Suspended', value: UserStatus.SUSPENDED },
    { label: 'Blocked', value: UserStatus.BLOCK },
    { label: 'Not active', value: UserStatus.NOT_ACTIVE },
] as const;