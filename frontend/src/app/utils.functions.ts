
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

export function getRandomRange(min: number, max: number): number {
    return Math.floor(Math.random() * (max - min + 1)) + min;
}

export function formatNumber(value: number | string): string {
    if (value === null || value === undefined) {
        return '';
    }

    return value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ' ');
}


export function getTimeAgo(date: Date | string): string {
    const now = new Date();
    const target = new Date(date);

    const diffInSeconds = Math.floor((now.getTime() - target.getTime()) / 1000);

    if (diffInSeconds < 60) {
        return 'just now';
    }

    const minutes = Math.floor(diffInSeconds / 60);
    if (minutes < 60) {
        return `${minutes} min`;
    }

    const hours = Math.floor(minutes / 60);
    if (hours < 24) {
        return `${hours}h`;
    }

    const days = Math.floor(hours / 24);
    if (days < 7) {
        return `${days} day${days > 1 ? 's' : ''}`;
    }

    const weeks = Math.floor(days / 7);
    if (weeks < 5) {
        return `${weeks} week${weeks > 1 ? 's' : ''}`;
    }

    const months = Math.floor(days / 30);
    if (months < 12) {
        return `${months} month${months > 1 ? 's' : ''}`;
    }

    const years = Math.floor(days / 365);
    return `${years} year${years > 1 ? 's' : ''}`;
}


export function dateToText(date: Date | string) {
    const d = new Date(date);

    return `${d.getFullYear()}-${d.getMonth() + 1}-${d.getDate()}`;
}

