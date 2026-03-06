
export function arraysEqual(a: any[], b: any[]) {
    if (a === b) return true;
    if (a == null || b == null) return false;
    if (a.length !== b.length) return false;

    for (var i = 0; i < a.length; ++i) {
        if (a[i] !== b[i]) return false;
    }
    return true;
}

export function getUserAvatar(avatar: string | null | undefined): string {
    if (!avatar) return 'images/user_default_avatar.png';

    if(avatar.includes('data:image/jpeg;base64,')) return avatar;

    return `data:image/jpeg;base64,${avatar}`;
}

export function getCompanyLogo(logo: string | null | undefined) {
    if (!logo) return 'images/default_logo_company.png';

    if(logo.includes('data:image/jpeg;base64,')) return logo;

    return `data:image/jpeg;base64,${logo}`;
}
