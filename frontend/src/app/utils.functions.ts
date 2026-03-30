
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

export function idToColor(id: number): string {
    const r = (id * 123) % 256;
    const g = (id * 456) % 256;
    const b = (id * 789) % 256;
    return `rgb(${r}, ${g}, ${b})`;
}

export function getWeekdayNumber(dateStr: string): number {
    const date = new Date(dateStr.replace(' ', 'T'));
    const day = date.getDay();

    return (day + 6) % 7;
}