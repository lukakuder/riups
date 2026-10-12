import express, { Request, Response } from 'express';

const app = express();
const PORT: number = 3000;

// Osnovna pot s tipiziranimi zahtevami in odzivi
app.get('/', (req: Request, res: Response) => {
    res.send('Works hey here here here');
});

// Zagon strežnika
app.listen(PORT, () => {
    console.log(`Server runs on : http://localhost:${PORT}`);
});
