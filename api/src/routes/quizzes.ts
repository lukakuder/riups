import { Router, type Request, type Response } from "express";
import { Quiz } from "../common/models/quiz.model.js";
import { QuizStatusEnum } from "../common/enums/quizStatusEnum.js";

const router = Router();

router.post('/', async (req: Request, res: Response) => {
    try {
        const { name, subject, difficulty, question_number } = req.body ?? [];

        if (!name || !subject || !difficulty || !question_number) {
            console.error('Missing required fields in request body');
            return res.status(400).send({
                message: 'Missing required fields in request body'
            });
        }

        const quiz = await Quiz.create({
            code: 123456,
            status: QuizStatusEnum.WAITING,
            name: name,
            subject: subject,
            difficulty: difficulty,
            question_number: question_number,
            questions: [],
            players: []
        });

        res.status(200).send(quiz);
    } catch (error) {
        console.error(error);
        res.status(400).send({
            message: 'Error creating quiz',
            error
        })
    }
})

export default router;