import { useState, useEffect } from 'react';
import { fetchUsers } from '../api/albyApi';
import type {User} from '../types';

export default function UsersList() {
    const [users, setUsers] = useState<User[]>([]);

    useEffect(() => {
        fetchUsers().then(setUsers).catch(console.error);
    }, []);

    return (
        <div>
            <h3>All Users</h3>
            <table border={1} cellPadding={6}>
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Login</th>
                    <th>Name</th>
                </tr>
                </thead>
                <tbody>
                {users.map(user => (
                    <tr key={user.id}>
                        <td>{user.id}</td>
                        <td>{user.login}</td>
                        <td>{user.name}</td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
}