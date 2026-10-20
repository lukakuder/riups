import { Schema, model } from 'mongoose';

export const playerSchema: Schema = new Schema(
    {
        name: { type: String, required: true },
        score: { type: Number, required: true },
        correct_answers: { type: Number, required: true },
        last_answered: { type: Number, required: true, default: -1 },
        socket: { type: String, required: true },
    },
    {
        timestamps: true,
    }
);

export const Player = model("Player", playerSchema);