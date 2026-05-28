import { useState, useEffect } from 'react';
import { fetchUsers, fetchMatches } from '../api/albyApi';
import type { User, MatchDto } from '../types';

const BASE_URL = 'http://localhost:8080/api';

async function createChat(user1Id: string, user2Id: string): Promise<string> {
    const response = await fetch(`${BASE_URL}/chats`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ user1Id, user2Id }),
    });
    if (!response.ok) throw new Error('Failed to create chat');
    const data = await response.json();
    return data.chatId;
}

export default function Matches() {
    const [users, setUsers] = useState<User[]>([]);
    const [selectedUserId, setSelectedUserId] = useState('');
    const [matches, setMatches] = useState<MatchDto[]>([]);
    const [message, setMessage] = useState('');

    useEffect(() => {
        fetchUsers().then(setUsers).catch(console.error);
    }, []);

    const handleFindMatches = async () => {
        if (!selectedUserId) return;
        const result = await fetchMatches(selectedUserId);
        setMatches(result);
    };

    const handleWrite = async (matchUserId: string, matchName: string) => {
        try {
            const chatId = await createChat(selectedUserId, matchUserId);
            setMessage(`✅ Chat with ${matchName} created! ID: ${chatId}`);
        } catch (err: any) {
            setMessage(`❌ ${err.message}`);
        }
    };

    return (
        <div>
            <h3>Find Matches</h3>
            <select value={selectedUserId} onChange={e => setSelectedUserId(e.target.value)}>
                <option value="">-- Select user --</option>
                {users.map(u => (
                    <option key={u.id} value={u.id}>{u.name} ({u.login})</option>
                ))}
            </select>
            <button onClick={handleFindMatches}>Find</button>

            {message && <p>{message}</p>}

            {matches.length > 0 && (
                <table border={1} cellPadding={6} style={{ marginTop: 16 }}>
                    <thead>
                    <tr>
                        <th>Name</th>
                        <th>Score</th>
                        <th>Action</th>
                    </tr>
                    </thead>
                    <tbody>
                    {matches.map((m, index) => (
                        <tr key={m.userId}>
                            <td>{m.name}</td>
                            <td>{m.score}</td>
                            <td>
                                {index === 0 && (
                                    <button onClick={() => handleWrite(m.userId, m.name)}>
                                        Написать
                                    </button>
                                )}
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            )}
        </div>
    );
}