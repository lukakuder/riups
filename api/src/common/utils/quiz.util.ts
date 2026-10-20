import { randomInt } from "node:crypto";
import { QuizStatusEnum } from "../enums/quizStatusEnum.js";
import { Quiz } from "../models/quiz.model.js";

function generateCode(): number {
    return randomInt(100000, 1000000);
}

export async function generateUniqueCode() {
    while (true) {
        const code = generateCode();
        const exists = await Quiz.exists({ code: code, status: QuizStatusEnum.WAITING });

        if (!exists) {
            return code;
        }
    }
}