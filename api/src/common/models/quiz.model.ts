import { Schema, model } from 'mongoose';

const quizSchema = new Schema(
    {
        code: { type: Number, required: true },
        name: { type: String, required: true },
        subject: { type: String, required: true },
        difficulty: { type: Number, required: true },
        question_number: { type: Number, required: true },
        status: { type: String, required: true },

        questions: { type: Array },
        players: { type: Array },
    },
    {
        timestamps: true,
    }
);

export const Quiz = model("Quiz", quizSchema);