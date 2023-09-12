import { v4 as uuidv4 } from 'uuid';

const jwtToken = localStorage.getItem('token');

export async function fetchChatList(userId: string) {
  const response = await fetch(`http://localhost:8080/chat/${userId}`, {
    method: 'GET',
    headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${jwtToken}` }
  });
  return response.json();
}

export async function createChat(userId: string) {
  const id = uuidv4();
  const response = await fetch('http://localhost:8080/chat', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${jwtToken}` },
    body: JSON.stringify({ id, userId }),
  });
  await response.json();
  return id;
}

export async function deleteChat(id: string) {
  const response = await fetch(`http://localhost:8080/chat/${id}`, {
    method: 'DELETE',
    headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${jwtToken}` },
  });
  return response.json();
}

export async function postChatMessage(id: string, message: string) {
  const response = await fetch('http://localhost:8080/chat/message', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${jwtToken}` },
    body: JSON.stringify({ id, message }),
  });
  return response.json();
}
