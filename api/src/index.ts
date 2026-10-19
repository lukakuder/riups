// @ts-ignore
import express from 'express';
import http from 'http';
import dotenv from 'dotenv';
import { Server } from 'socket.io';
import { connectToDatabase } from './config/db.js';
import { createQuizRouter } from './routes/quizzes.js'

const app = express();
app.use(express.json());
dotenv.config();

const server = http.createServer(app);
const io = new Server(server, { cors: { origin: '*' } });

await connectToDatabase();

// Definicija routov
app.use('/api/quizzes', createQuizRouter(io));

// Test za povezavo z socketi
io.on('connection', (socket) => {
    console.log('connected', socket.id);

    socket.on('join', (code: string) => {
        socket.join(code);
        console.log(`Socket ${socket.id} joined game: ${code}`);
    });

    socket.on('disconnect', () => {
        console.log('disconnected', socket.id);
    });
});

// Zagon strežnika
server.listen(parseInt(process.env.PORT!), '0.0.0.0', () => {
    console.log(`Server runs on : http://localhost:${process.env.PORT}`);
});