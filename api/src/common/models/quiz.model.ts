import { Schema, model } from 'mongoose';
import { questionSchema } from "./question.model.js";
import { playerSchema } from "./player.model.js";

const quizSchema = new Schema(
    {
        code: { type: Number, required: true },
        name: { type: String, required: true },
        subject: { type: String, required: true },
        grade: { type: Number, required: true },
        difficulty: { type: Number, required: true },
        question_number: { type: Number, required: true },
        status: { type: String, required: true },
        active_question: { type: Number, required: true },

        questions: [questionSchema],
        players: [playerSchema],
    },
    {
        timestamps: true,
    }
);

export const Quiz = model("Quiz", quizSchema);