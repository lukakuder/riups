// @ts-ignore
import express, { type Request, type Response } from 'express';
import http from 'http';
import mongoose from 'mongoose';
import { Server } from 'socket.io';

const app = express();
const PORT: number = 3000;
const MONGO_URI = 'mongodb://user:root@mongodb:27017/quizzler?authSource=admin';

async function connectWithRetry(tries = 10) {
    for (let i = 1; i <= tries; i++) {
        try {
            await mongoose.connect(MONGO_URI, { serverSelectionTimeoutMS: 3000 });
            console.log("MongoDB connected");
            return;
        } catch (e) {
            console.log(`Mongo not ready (${i}/${tries}), retrying...`);
            await new Promise((r) => setTimeout(r, 2000));
        }
    }
    throw new Error("Could not connect to MongoDB");
}

await connectWithRetry();

// Osnovna pot
app.get('/', (req: Request, res: Response) => {
    res.send('Works hey here here here');
});

// HTTP server shared by Express and Socket.IO
const server = http.createServer(app);
const io = new Server(server, { cors: { origin: '*' } });

// Socket test: counter that ticks every second + echo with ack
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
server.listen(PORT, '0.0.0.0', () => {
    console.log(`Server runs on : http://localhost:${PORT}`);
});