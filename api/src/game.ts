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

        for (let i = 0; i < quiz.questions.length; i++) {
            const question = quiz.questions[i]!;

            await Quiz.findByIdAndUpdate(id, { active_question: i + 1 });

            io.to(room).emit('question', question);
            //setTimeout(() => {}, question.available_time);
            await sleep(question.available_time * 1000);
            io.to(room).emit('question_end', question);

            await Quiz.findByIdAndUpdate(id, { active_question: -1 });

            //setTimeout(() => {}, 5000);
            await sleep(5000);
        }

        const results = await Quiz.findById(id);
        io.to(room).emit('results', results);
    } catch (error) {
        console.log(error);
    }
}

function sleep(milliseconds: number): Promise<void> {
    return new Promise((resolve) => {
        setTimeout(resolve, milliseconds);
    });
}