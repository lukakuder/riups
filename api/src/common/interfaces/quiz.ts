import { QuestionInterface } from "./question.js";
import { PlayerInterface } from "./player.js";
import { SubjectEnum } from "../enums/subjectEnum.js";

export interface QuizInterface {
    id: number;
    code: number;
    name: string;
    subject: SubjectEnum;
    difficulty: number;
    question_number: number;

    questions: QuestionInterface[];
    players: PlayerInterface[];
}