import http from 'http';
import { Server } from "socket.io";
import { Quiz } from "./common/models/quiz.model.js";
import { QuizStatusEnum } from "./common/enums/quizStatusEnum.js";

export function initializeSockets(server: http.Server): Server {
    const io = new Server(server, { cors: { origin: '*' } })

    io.on('connection', (socket) => {
        console.log('connected', socket.id);

        socket.on('join', async (code: string, name: string, ack) => {
            const quizCode = Number(code);
            if (!Number.isInteger(quizCode)) {
                return ack?.({
                    success: false,
                    error: 'Invalid code'
                });
            }

            const quiz = await Quiz.findOneAndUpdate({
                code: quizCode,
                status: QuizStatusEnum.WAITING,
                'players.socket': { $ne: socket.id },
            }, {
                $push: {
                    players: {
                        name,
                        score: 0,
                        correct_answers: 0,
                        socket: socket.id
                    },
                },
            }, { returnDocument: 'after' });

            // Protects against double clicks and non existent quizzes
            if (!quiz) {
                const existing = await Quiz.findOne({ code: quizCode, 'players.socket': socket.id });
                if (existing) {
                    socket.join(code);
                    return ack?.({ success: true });
                }

                return ack?.({ success: false, error: 'Quiz not found' });
            }

            socket.join(code);
            ack?.({ success: true });
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