import { Router, type Request, type Response } from "express";

const router = Router();

router.post('/', async (req: Request, res: Response) => {
    const { name, subject, difficulty, question_number } = req.body ?? [];

    if (!name || !subject || !difficulty || !question_number) {
        return res.status(400).json({
            message: 'Please fill all the fields!'
        })
    }
})

export default router;