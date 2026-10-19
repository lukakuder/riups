import { QuestionInterface } from "./question.js";
import { PlayerInterface } from "./player.js";

export interface QuizInterface {
    id: number;
    code: number;
    name: string;

    questions: QuestionInterface[];
    players: PlayerInterface[];
}