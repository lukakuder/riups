import { Server } from "socket.io";
import { Quiz } from "./common/models/quiz.model.js";

export async function startGame(io: Server, id: string) {
    try {
        const quiz = await Quiz.findById(id);
        if (!quiz) {
            console.error('Quiz not found');
            return;
        }

        const room = String(quiz.code);

        for (const question of quiz.questions) {
            io.to(room).emit('question', question);
            setTimeout(() => {}, question.available_time);
            io.to(room).emit('question_end', question);
            setTimeout(() => {}, 5000);
        }

        const results = await Quiz.findById(id);
        io.to(room).emit('results', results);
    } catch (error) {
        console.log(error);
    }
}