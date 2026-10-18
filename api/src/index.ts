// @ts-ignore
import express, { type Request, type Response } from 'express';
import mongoose from 'mongoose';

const app = express();
const PORT: number = 3000;
const MONGO_URI = 'mongodb://user:root@mongodb:27017/mydatabase?authSource=admin';

// Osnovna pot s tipiziranimi zahtevami in odzivi
app.get('/', (req: Request, res: Response) => {
    res.send('Works hey here here here');
});

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

// Zagon strežnika
app.listen(PORT, () => {
    console.log(`Server runs on : http://localhost:${PORT}`);
});
