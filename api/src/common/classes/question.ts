import { QuestionInterface } from "../interfaces/question.js";

export class Question implements QuestionInterface {
    title: string;
    content: string;
    answer: string;
    available_time: number;

    constructor(title: string, content: string, answer: string, available_time: number) {
        this.title = title;
        this.content = content;
        this.answer = answer;
        this.available_time = available_time;
    }
}