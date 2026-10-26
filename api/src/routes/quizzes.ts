import { Router, type Request, type Response } from "express";
import { Quiz } from "../common/models/quiz.model.js";
import { QuizStatusEnum } from "../common/enums/quizStatusEnum.js";
import { generateUniqueCode } from "../common/utils/quiz.util.js";
import { Server } from "socket.io";
import { startGame } from "../game.js";
import { generateQuestions } from "../common/questions/question.generators.js";

export function createQuizRouter(io: Server) {
    const router = Router();

    router.post('/', async (req: Request, res: Response) => {
        try {
            const {
                name,
                subject,
                difficulty,
                question_number,
                difficulty_mode
            } = req.body ?? [];

            if (!name || !subject || !difficulty || !question_number) {
                console.error('Missing required fields in request body');
                return res.status(400).send({
                    message: 'Missing required fields in request body'
                });
            }

            const code = await generateUniqueCode();

            const questions = generateQuestions(
                subject,
                difficulty,
                question_number,
                difficulty_mode ?? 'fixed'
            );

            const quiz = await Quiz.create({
                code: code,
                status: QuizStatusEnum.WAITING,
                name: name,
                subject: subject,
                difficulty: difficulty,
                question_number: question_number,
                active_question: -1,
                questions: questions,
                players: []
            });

            res.status(200).send(quiz);
        } catch (error) {
            console.error(error);
            res.status(400).send({
                message: 'Error creating quiz',
                error
            });
        }
    });

    router.post('/start', async (req: Request, res: Response) => {
        try {
            const { id } = req.body ?? [];

            if (!id) {
                console.error('Missing required fields in request body');
                return res.status(400).send({
                    message: 'Missing required fields in request body'
                });
            }

            const quiz = await Quiz.findByIdAndUpdate(
                id,
                { status: QuizStatusEnum.IN_PROGRESS },
                { new: true }
            );

            if (!quiz) {
                return res.status(404).send({ message: 'Quiz not found' });
            }

            res.status(200).send(quiz);

            await startGame(io, String(quiz._id));

            // for (const question in quiz.questions) {
            //     io.to(String(quiz.code)).emit('question', question);
            //
            //     setTimeout()
            //
            //     io.to(String(quiz.code)).emit('question-end', question);
            //
            //     sleep(5seconds);
            // }
            //
            // const results = await Quiz.findById(id);
            // io.to(String(quiz.code)).emit('results', results);
        } catch (error) {
            console.error(error);
            res.status(400).send({
                message: 'Error starting a quiz',
                error
            });
        }
    });

    return router;
}