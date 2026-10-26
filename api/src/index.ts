// @ts-ignore
import dotenv from 'dotenv';
dotenv.config();

import express from 'express';
import http from 'http';
import { connectToDatabase } from './config/db.js';
import { createQuizRouter } from './routes/quizzes.js'
import { initializeSockets } from "./sockets.js";

const app = express();
app.use(express.json());

const server = http.createServer(app);
const io = initializeSockets(server);

await connectToDatabase();

// Definicija routov
app.use('/api/quizzes', createQuizRouter(io));

// Zagon strežnika
server.listen(parseInt(process.env.PORT!), '0.0.0.0', () => {
    console.log(`Server runs on : http://localhost:${process.env.PORT}`);
});