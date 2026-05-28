import { useState, useEffect } from 'react';
import { fetchUsers } from '../api/albyApi';

const BASE_URL = 'http://localhost:8080/api';

interface ChatInfo {
    chatId: string;
    partnerName: string;
    partnerId: string;
}

interface MessageInfo {
    id: string;
    senderId: string;
    senderName: string;
    content: string;
    sentAt: string;
}

export default function Chats() {
    const [users, setUsers] = useState<any[]>([]);
    const [selectedUserId, setSelectedUserId] = useState('');
    const [chats, setChats] = useState<ChatInfo[]>([]);
    const [selectedChatId, setSelectedChatId] = useState('');
    const [messages, setMessages] = useState<MessageInfo[]>([]);
    const [newMessage, setNewMessage] = useState('');

    useEffect(() => {
        fetchUsers().then(setUsers).catch(console.error);
    }, []);

    // Загрузить чаты выбранного пользователя
    const loadChats = async () => {
        if (!selectedUserId) return;
        const response = await fetch(`${BASE_URL}/chats?userId=${selectedUserId}`);
        if (response.ok) {
            const data = await response.json();
            setChats(data);
        }
    };

    // Загрузить сообщения выбранного чата
    const loadMessages = async (chatId: string) => {
        setSelectedChatId(chatId);
        const response = await fetch(`${BASE_URL}/chats/${chatId}/messages`);
        if (response.ok) {
            const data = await response.json();
            setMessages(data);
        }
    };

    // Отправить сообщение
    const sendMessage = async () => {
        if (!newMessage.trim() || !selectedChatId || !selectedUserId) return;
        const response = await fetch(`${BASE_URL}/chats/${selectedChatId}/messages`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ senderId: selectedUserId, content: newMessage }),
        });
        if (response.ok) {
            setNewMessage('');
            loadMessages(selectedChatId); // перезагружаем сообщения
        }
    };

    return (
        <div style={{ display: 'flex', gap: 24 }}>
            {/* Левая панель: выбор пользователя и список чатов */}
            <div style={{ width: 300 }}>
                <h3>Chats</h3>
                <select value={selectedUserId} onChange={e => setSelectedUserId(e.target.value)}>
                    <option value="">-- Select user --</option>
                    {users.map(u => (
                        <option key={u.id} value={u.id}>{u.name} ({u.login})</option>
                    ))}
                </select>
                <button onClick={loadChats} style={{ marginLeft: 8 }}>Load chats</button>

                <ul style={{ listStyle: 'none', padding: 0, marginTop: 16 }}>
                    {chats.map(chat => (
                        <li key={chat.chatId} style={{ marginBottom: 8 }}>
                            <button onClick={() => loadMessages(chat.chatId)}>
                                {chat.partnerName}
                            </button>
                        </li>
                    ))}
                </ul>
            </div>

            {/* Правая панель: сообщения и отправка */}
            <div style={{ flex: 1 }}>
                {selectedChatId ? (
                    <>
                        <h3>Messages</h3>
                        <div style={{ border: '1px solid #ccc', padding: 12, minHeight: 300, marginBottom: 12 }}>
                            {messages.map(msg => (
                                <div key={msg.id} style={{ marginBottom: 8 }}>
                                    <strong>{msg.senderName}</strong>: {msg.content}
                                    <div style={{ fontSize: '0.75rem', color: '#999' }}>{new Date(msg.sentAt).toLocaleString()}</div>
                                </div>
                            ))}
                        </div>
                        <div style={{ display: 'flex', gap: 8 }}>
                            <input
                                type="text"
                                value={newMessage}
                                onChange={e => setNewMessage(e.target.value)}
                                onKeyDown={e => e.key === 'Enter' && sendMessage()}
                                placeholder="Type a message..."
                                style={{ flex: 1, padding: 8 }}
                            />
                            <button onClick={sendMessage}>Send</button>
                        </div>
                    </>
                ) : (
                    <p>Select a chat from the left panel</p>
                )}
            </div>
        </div>
    );
}