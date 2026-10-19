import { Schema, model } from 'mongoose';

const playerSchema: Schema = new Schema(
    {
        id: { type: Number, required: true },
        name: { type: String, required: true },
        score: { type: Number, required: true },
        correct_answers: { type: Number, required: true },
    },
    {
        timestamps: true,
    }
);

export const Player = model("Player", playerSchema);