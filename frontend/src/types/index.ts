export interface Interest {
    id: number;
    name: string;
}

export interface InterestWeight {
    interestName: string;
    weight: number;
}

export interface RegisterRequest {
    login: string;
    password: string;
    name: string;
    interests: InterestWeight[];
}

export interface User {
    id: string;
    login: string;
    name: string;
}

export interface MatchDto {
    userId: string;
    name: string;
    score: number;
}