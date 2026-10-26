import http from 'http';
import { Server } from "socket.io";
import { Quiz } from "./common/models/quiz.model.js";
import { QuizStatusEnum } from "./common/enums/quizStatusEnum.js";

export function initializeSockets(server: http.Server): Server {
    const io = new Server(server, { cors: { origin: '*' } })

    io.on('connection', (socket) => {
        console.log('connected', socket.id);

        socket.on('join', async (code: string, name: string, ack) => {
            console.log('join', { code, name, socket: socket.id });

            try {
                const quizCode = Number(code);
                if (!Number.isInteger(quizCode)) {
                    return ack?.({ success: false, error: 'Invalid code' });
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

                if (!quiz) {
                    const existing = await Quiz.findOne({ code: quizCode, 'players.socket': socket.id });
                    if (existing) {
                        socket.join(code);
                        return ack?.({ success: true });
                    }

                    return ack?.({ success: false, error: 'Quiz not found' });
                }

                socket.join(code);

                io.to(code).emit("players", quiz.players.map((p) => p.name));

                ack?.({ success: true });
            } catch (err) {
                console.error('join error', err);
                return ack?.({ success: false, error: 'Server error' });
            }
        });

        socket.on("watch", async (code: string, ack) => {
            console.log('watch', { code, socket: socket.id });

            try {
                const quizCode = Number(code);
                if (!Number.isInteger(quizCode)) {
                    return ack?.({ success: false, error: 'Invalid code' });
                }

                const quiz = await Quiz.findOne({
                    code: quizCode,
                    status: QuizStatusEnum.WAITING,
                });

                if (!quiz) {
                    return ack?.({ success: false, error: "Quiz not found" });
                }

                socket.join(code);

                socket.emit("players", quiz.players.map((p) => p.name));

                ack?.({ success: true });
            } catch (err) {
                console.error('watch error', err);
                return ack?.({ success: false, error: 'Server error' });
            }
        });

        socket.on('answer', async (code: string, answer: string, ack) => {
            try {
                const quizCode = Number(code);
                if (!Number.isInteger(quizCode)) {
                    return ack?.({ success: false, error: 'Invalid code' });
                }

                const quiz = await Quiz.findOne({
                    code: quizCode,
                    status: QuizStatusEnum.IN_PROGRESS,
                }).lean();

                if (!quiz) {
                    return ack?.({ success: false, error: 'Quiz not found' });
                }

                const question = quiz.questions[quiz.active_question];
                if (quiz.active_question === -1 || !question) {
                    return ack?.({ success: false, error: 'Wrong active question' });
                }

                const player = quiz.players.find(p => p.socket === socket.id);
                if (!player) {
                    return ack?.({ success: false, error: 'No such player' });
                }

                if (player.last_answered === quiz.active_question) {
                    return ack?.({ success: false, error: 'You have already tried answering this question' });
                }

                const is_correct = String(question.answer) === String(answer);

                await Quiz.updateOne(
                    {
                        code: quizCode,
                        status: QuizStatusEnum.IN_PROGRESS,
                        active_question: quiz.active_question,
                        players: {
                            $elemMatch: {
                                socket: socket.id,
                                last_answered: { $ne: quiz.active_question },
                            },
                        },
                    },
                    {
                        $set: { 'players.$.last_answered': quiz.active_question },
                        $inc: {
                            // TODO: change how scoring works
                            'players.$.score': is_correct ? 1 : 0,
                            'players.$.correct_answers': is_correct ? 1 : 0,
                        },
                    }
                );

                ack?.({ success: true, is_correct });
            } catch (err) {
                console.error('answer error', err);
                ack?.({ success: false, error: 'Server error' });
            }
        });

        socket.on('disconnect', (reason: any) => {
            console.log('disconnected', socket.id, reason);
        });
    });

    return io;
}