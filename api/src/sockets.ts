import http from 'http';
import { Server } from "socket.io";

export function initializeSockets(server: http.Server): Server {
    const io = new Server(server, { cors: { origin: '*' } })

    io.on('connection', (socket) => {
        console.log('connected', socket.id);

        socket.on('join', (code: string) => {
            socket.join(code);
            console.log(`Socket ${socket.id} joined game: ${code}`);
        });

        socket.on('answer', (code: string, answer: string) => {
            // check if the answer for the active question of the quiz with the given code is correct and award points
        });

        socket.on('disconnect', () => {
            console.log('disconnected', socket.id);
        });
    });

    return io;
}