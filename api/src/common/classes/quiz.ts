import { QuizInterface } from "../interfaces/quiz.js";
import { Player } from "./player.js";
import { Question } from "./question.js";
import { SubjectEnum } from "../enums/subjectEnum.js";

export class Quiz implements QuizInterface {
    id: number;
    code: number;
    name: string;
    subject: SubjectEnum;
    grade: number;
    difficulty: number;
    question_number: number;

    questions: Question[];
    players: Player[];

    constructor(
        id: number,
        name: string,
        subject: string,
        grade: number,
        difficulty: number,
        question_number: number,
        code: number,
        questions: Question[]
    ) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.subject = subject as SubjectEnum;
        this.grade = grade;
        this.difficulty = difficulty;
        this.question_number = question_number;
        this.questions = questions;
        this.players = [];
    }
}