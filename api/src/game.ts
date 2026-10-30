import { Server } from "socket.io";
import { Quiz } from "./common/models/quiz.model.js";

export async function startGame(io: Server, id: string) {
    try {
        console.log(`Starting game for quiz with ID: ${id}`);

        const quiz = await Quiz.findById(id);
        if (!quiz) {
            console.error('Quiz not found');
            return;
        }

        const room = String(quiz.code);

        // TODO: Dodaj neki quiz begin ki se posle na room, in se pokaze zaslon kao naj so vsi propravljeni

        for (let i = 0; i < quiz.questions.length; i++) {
            const question = quiz.questions[i]!;

            console.log(`Sending question ${i + 1} to room ${room}:`, question);

            await Quiz.findByIdAndUpdate(id, { active_question: i, correct_count: 0, question_started_at: new Date() });

            const { answer, ...publicQuestion } = question.toObject();

            io.to(room).emit('question', {...publicQuestion, index: i + 1, total: quiz.questions.length,});
            await sleep(question.available_time * 1000);
            io.to(room).emit('question_end', question);

            // TODO: Po tem ko to backend zazna (to je treba se dodat), naj se pokaze neki tranzition zaslon, da ni sam takojsna sprememba teksta vprasanja

            await Quiz.findByIdAndUpdate(id, { active_question: -1 });

            await sleep(5000);
        }

        const results = await Quiz.findById(id);
        io.to(room).emit('results', results);

        // PO tem naj se na frontendu pojavijo rezultati
    } catch (error) {
        console.log(error);
    }
}

function sleep(milliseconds: number): Promise<void> {
    return new Promise((resolve) => {
        setTimeout(resolve, milliseconds);
    });
}