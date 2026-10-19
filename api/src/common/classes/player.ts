import {PlayerInterface} from "../interfaces/player.js";

export class Player implements PlayerInterface {
    id: number;
    name: string;
    score: number;
    correct_answers: number;

    constructor(id: number, name: string) {
        this.id = id;
        this.name = name;
        this.score = 0;
        this.correct_answers = 0;
    }
}