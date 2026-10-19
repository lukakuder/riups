import { QuizInterface } from "../interfaces/quiz.js";
import { Player } from "./player.js";
import { Question } from "./question.js";

export class Quiz implements QuizInterface {
    id: number;
    code: number;
    name: string;
    questions: Question[];
    players: Player[];

    constructor(id: number, name: string, code: number, questions: Question[]) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.questions = questions;
        this.players = [];
    }
}