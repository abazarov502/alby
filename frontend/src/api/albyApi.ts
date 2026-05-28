import type {RegisterRequest, User, Interest, MatchDto} from '../types';

const BASE_URL = 'http://localhost:8080/api';

export async function registerUser(data: RegisterRequest): Promise<User> {
    const response = await fetch(`${BASE_URL}/users/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data),
    });
    if (!response.ok) {
        const error = await response.text();
        throw new Error(error || 'Registration failed');
    }
    return response.json();
}

export async function fetchInterests(): Promise<Interest[]> {
    const response = await fetch(`${BASE_URL}/interests`);
    if (!response.ok) throw new Error('Failed to fetch interests');
    return response.json();
}

export async function fetchUsers(): Promise<User[]> {
    const response = await fetch(`${BASE_URL}/users`);
    if (!response.ok) throw new Error('Failed to fetch users');
    return response.json();
}

export async function fetchMatches(userId: string, limit = 10): Promise<MatchDto[]> {
    const response = await fetch(`${BASE_URL}/matches?userId=${userId}&limit=${limit}`);
    if (!response.ok) throw new Error('Failed to fetch matches');
    return response.json();
}