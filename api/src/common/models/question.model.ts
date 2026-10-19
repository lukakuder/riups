import { Schema, model } from 'mongoose';

const questionSchema = new Schema(
    {
        title: { type: String, required: true },
        content: { type: String, required: true },
        answer: { type: String, required: true },
        available_time: { type: Number, required: true },
    },
    {
        timestamps: true
    }
)

export const Question = model("Question", questionSchema);