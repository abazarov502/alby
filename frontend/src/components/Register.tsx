import { useState, useEffect } from 'react';
import type {Interest} from '../types';
import { registerUser, fetchInterests } from '../api/albyApi';

export default function Register() {
    const [login, setLogin] = useState('');
    const [password, setPassword] = useState('');
    const [name, setName] = useState('');
    const [interests, setInterests] = useState<Interest[]>([]);
    const [selectedInterests, setSelectedInterests] = useState<Map<number, number>>(new Map());
    const [message, setMessage] = useState('');

    useEffect(() => {
        fetchInterests().then(setInterests).catch(console.error);
    }, []);

    const handleWeightChange = (interestId: number, weight: number) => {
        const next = new Map(selectedInterests);
        if (weight === 0) next.delete(interestId);
        else next.set(interestId, weight);
        setSelectedInterests(next);
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        const payload = {
            login,
            password,
            name,
            interests: Array.from(selectedInterests.entries()).map(([id, weight]) => ({
                interestName: interests.find(i => i.id === id)!.name,
                weight,
            })),
        };
        try {
            const user = await registerUser(payload);
            setMessage(`✅ User ${user.name} registered successfully!`);
        } catch (err: any) {
            setMessage(`❌ ${err.message}`);
        }
    };

    return (
        <form onSubmit={handleSubmit}>
            <input value={login} onChange={e => setLogin(e.target.value)} placeholder="Login" required />
            <input value={password} onChange={e => setPassword(e.target.value)} placeholder="Password" type="password" required />
            <input value={name} onChange={e => setName(e.target.value)} placeholder="Name" />
            <h4>Interests (0 = skip, 1..10 = like)</h4>
            {interests.map(interest => (
                <div key={interest.id}>
                    <label>{interest.name}</label>
                    <input
                        type="number"
                        min={0}
                        max={10}
                        defaultValue={0}
                        onChange={e => handleWeightChange(interest.id, Number(e.target.value))}
                    />
                </div>
            ))}
            <button type="submit">Register</button>
            {message && <p>{message}</p>}
        </form>
    );
}