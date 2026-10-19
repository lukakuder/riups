// @ts-ignore
import express, { type Request, type Response } from 'express';
import http from 'http';
import mongoose from 'mongoose';
import dotenv from 'dotenv';
import { Server } from 'socket.io';

const app = express();
dotenv.config();

async function connectWithRetry(tries = 10) {
    for (let i = 1; i <= tries; i++) {
        try {
            await mongoose.connect(process.env.MONGO_URI!, { serverSelectionTimeoutMS: 3000 });
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