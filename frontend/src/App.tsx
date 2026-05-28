import { useState } from 'react';
import Register from './components/Register';
import UsersList from './components/UsersList';
import Matches from './components/Matches';
import Chats from './components/Chats';

function App() {
    const [tab, setTab] = useState<'register' | 'users' | 'matches' | 'chats'>('register');

    return (
        <div style={{ padding: 24 }}>
            <h1>Alby – Match Your Interests</h1>
            <div style={{ marginBottom: 16 }}>
                <button onClick={() => setTab('register')}>Register</button>
                <button onClick={() => setTab('users')}>Users</button>
                <button onClick={() => setTab('matches')}>Matches</button>
                <button onClick={() => setTab('chats')}>Chats</button>
            </div>
            {tab === 'register' && <Register />}
            {tab === 'users' && <UsersList />}
            {tab === 'matches' && <Matches />}
            {tab === 'chats' && <Chats />}
        </div>
    );
}

export default App;