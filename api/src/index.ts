// @ts-ignore
import express from 'express';
import http from 'http';
import dotenv from 'dotenv';
import { Server } from 'socket.io';
import { connectToDatabase } from './config/db.js';
import quizRouter from './routes/quizzes.js'

const app = express();
app.use(express.json());
dotenv.config();

await connectToDatabase();

// Definicija routov
app.use('/api/quizzes', quizRouter);

const server = http.createServer(app);
const io = new Server(server, { cors: { origin: '*' } });

// Test za povezavo z socketi
io.on('connection', (socket) => {
    console.log('connected', socket.id);

    let n = 0;
    const timer = setInterval(() => socket.emit('tick', n++), 1000);

    socket.on('echo', (msg: string, ack?: (reply: string) => void) => {
        console.log('echo:', msg);
        ack?.(`server got: ${msg}`);
    });

    socket.on('disconnect', () => {
        clearInterval(timer);
        console.log('disconnected', socket.id);
    });
});

// Zagon strežnika
server.listen(parseInt(process.env.PORT!), '0.0.0.0', () => {
    console.log(`Server runs on : http://localhost:${process.env.PORT}`);
});