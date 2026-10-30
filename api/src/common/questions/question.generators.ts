import { SubjectEnum } from "../enums/subjectEnum.js";
import type { QuestionInterface } from "../interfaces/question.js";

const QUESTION_TIME_BY_DIFFICULTY = { 1: 45, 2: 30, 3: 20 } as const;
type QuestionGenerator = () => QuestionInterface;
type GradeLevel = 1 | 2 | 3;
export type DifficultyMode = "fixed" | "progressive";

export function generateQuestions(
    subject: SubjectEnum,
    grade: number,
    difficulty: number,
    count: number,
    mode: DifficultyMode = "fixed"
): QuestionInterface[] {
    if (!Number.isInteger(count) || count <= 0) {
        throw new Error("Question count must be a positive integer");
    }

    const generators = getGenerators(subject, grade, difficulty, mode);
    if (generators.length === 0) {
        throw new Error("No question generators available");
    }

    return Array.from({ length: count }, () => {
        const generator = generators[randomNumber(0, generators.length - 1)];
        if (!generator) {
            throw new Error("Failed to select a question generator");
        }
        return generator();
    });
}

function getGenerators(
    subject: SubjectEnum,
    grade: number,
    difficulty: number,
    mode: DifficultyMode = "fixed"
): QuestionGenerator[] {
    if (!Number.isInteger(grade) || grade < 1 || grade > 9) {
        throw new Error(`Unsupported math grade: ${grade}`);
    }
    if (!Number.isInteger(difficulty) || difficulty < 1 || difficulty > 3) {
        throw new Error(`Unsupported math difficulty: ${difficulty}`);
    }
    if (subject !== SubjectEnum.MATH) {
        if (subject === SubjectEnum.PHYSICS) {
            throw new Error("Physics question generators are not implemented yet");
        }
        throw new Error(`Unsupported subject: ${subject}`);
    }
    if (mode !== "fixed" && mode !== "progressive") {
        throw new Error(`Unsupported difficulty mode: ${mode}`);
    }

    const selectedGrade = Math.ceil(grade / 3) as GradeLevel;
    const grades = mode === "progressive"
        ? ([1, 2, 3].filter((grade) => grade <= selectedGrade) as GradeLevel[])
        : [selectedGrade];
    const level = difficulty as 1 | 2 | 3;

    return grades.flatMap((grade) => {
        switch (grade) {
            case 1:
                return [
                    () => generateMultiplicationQuestion(level),
                    () => generateDivisionQuestion(level),
                    () => generateAdditionSubtractionQuestion(level),
                    () => generateCompletionQuestion(level),
                    () => generateTimeAndUnitsQuestion(level),
                ];
            case 2:
                return [
                    () => generateNaturalDivisionQuestion(level),
                    () => generateFractionsQuestion(level),
                    () => generateAreaPerimeterQuestion(level),
                    () => generateExpressionsAndNumberLineQuestion(level),
                    () => generateMultiplesAndDivisorsQuestion(level),
                ];
            case 3:
                return [
                    () => generatePercentagesQuestion(level),
                    () => generateLinearEquationQuestion(level),
                    () => generateFunctionsAndGraphsQuestion(level),
                    () => generatePythagoreanQuestion(level),
                    () => generatePowersAndRootsQuestion(level),
                ];
        }
    });
}

function randomNumber(min: number, max: number): number {
    return Math.floor(Math.random() * (max - min + 1)) + min;
}

function createQuestion(
    title: string,
    content: string,
    answer: number | string,
    difficulty = 2
): QuestionInterface {
    const timeLevel = Math.min(3, Math.max(1, difficulty)) as 1 | 2 | 3;
    return {
        title,
        content,
        answer: String(answer),
        available_time: QUESTION_TIME_BY_DIFFICULTY[timeLevel],
    };
}

export function generateAdditionQuestion(level = 1): QuestionInterface {
    return generateAdditionSubtractionQuestion(level, true);
}

export function generateSubtractionQuestion(level = 1): QuestionInterface {
    return generateAdditionSubtractionQuestion(level, false);
}

export function generateMultiplicationQuestion(level = 1): QuestionInterface {
    const maximum = level === 1 ? 10 : level === 2 ? 20 : 100;
    const first = randomNumber(1, maximum);
    const second = randomNumber(1, maximum);
    return createQuestion("Množenje", `Koliko je ${first} × ${second}?`, first * second, level);
}

export function generateDivisionQuestion(level = 1): QuestionInterface {
    const divisor = randomNumber(1, level === 1 ? 10 : 20);
    const quotient = randomNumber(1, level === 1 ? 10 : 25);
    return createQuestion("Deljenje brez ostanka", `Koliko je ${divisor * quotient} ÷ ${divisor}?`, quotient, level);
}

export function generateAdditionSubtractionQuestion(
    level = 1,
    addition = Math.random() >= 0.5
): QuestionInterface {
    const maximum = level === 1 ? 100 : level === 2 ? 1000 : 10000;
    const first = randomNumber(1, maximum);
    const second = addition ? randomNumber(1, maximum) : randomNumber(1, first);
    return createQuestion(
        "Seštevanje in odštevanje",
        `Koliko je ${first} ${addition ? "+" : "-"} ${second}?`,
        addition ? first + second : first - second,
        level
    );
}

export function generateCompletionQuestion(level = 1): QuestionInterface {
    const missing = randomNumber(1, level === 1 ? 50 : 100);
    const other = randomNumber(1, level === 1 ? 10 : 20);
    if (level >= 2 && Math.random() >= 0.5) {
        return createQuestion("Dopolnjevanje računov", `Katero število manjka: ? × ${other} = ${missing * other}?`, missing, level);
    }
    return createQuestion("Dopolnjevanje računov", `Katero število manjka: ${missing} + ? = ${missing + other}?`, other, level);
}

export function generateTimeAndUnitsQuestion(level = 1): QuestionInterface {
    if (Math.random() >= 0.5) {
        const hours = randomNumber(1, level === 1 ? 5 : 12);
        return createQuestion("Čas in merske enote", `Koliko minut je ${hours} ur?`, hours * 60, level);
    }
    const meters = randomNumber(1, level === 1 ? 5 : 20);
    return createQuestion("Čas in merske enote", `Koliko centimetrov je ${meters} metrov?`, meters * 100, level);
}

export function generateNaturalDivisionQuestion(level = 1): QuestionInterface {
    const divisor = randomNumber(2, level === 1 ? 12 : 30);
    const quotient = randomNumber(2, level === 1 ? 30 : 100);
    return createQuestion("Deljenje naravnih števil", `Koliko je ${divisor * quotient} ÷ ${divisor}?`, quotient, level);
}

export function generateFractionsQuestion(level = 1): QuestionInterface {
    const denominator = randomNumber(2, level === 1 ? 6 : 12);
    const first = randomNumber(1, denominator - 1);
    const second = randomNumber(1, denominator - 1);
    return createQuestion("Ulomki", `Koliko je ${first}/${denominator} + ${second}/${denominator}?`, `${first + second}/${denominator}`, level);
}

export function generateAreaPerimeterQuestion(level = 1): QuestionInterface {
    const length = randomNumber(2, level === 1 ? 12 : 30);
    const width = randomNumber(2, level === 1 ? 10 : 20);
    if (Math.random() >= 0.5) {
        return createQuestion("Obseg in ploščina", `Kolikšen je obseg pravokotnika ${length} cm × ${width} cm?`, 2 * (length + width), level);
    }
    return createQuestion("Obseg in ploščina", `Kolikšna je ploščina pravokotnika ${length} cm × ${width} cm?`, length * width, level);
}

export function generateExpressionsAndNumberLineQuestion(level = 1): QuestionInterface {
    const number = randomNumber(1, level === 1 ? 30 : 100);
    const change = randomNumber(1, level === 1 ? 10 : 25);
    const greater = Math.random() >= 0.5;
    return createQuestion("Številski izrazi in številska premica", `Katero število je ${change} ${greater ? "večje" : "manjše"} od ${number}?`, greater ? number + change : number - change, level);
}

export function generateMultiplesAndDivisorsQuestion(level = 1): QuestionInterface {
    const divisor = randomNumber(2, level === 1 ? 10 : 20);
    const multiple = divisor * randomNumber(2, level === 1 ? 10 : 20);
    return createQuestion("Večkratniki in delitelji", `Ali je ${multiple} deljivo z ${divisor}?`, "Da", level);
}

export function generatePercentagesQuestion(level = 1): QuestionInterface {
    const percentage = randomNumber(1, level === 1 ? 50 : 90);
    const base = randomNumber(2, level === 1 ? 20 : 200) * 10;
    return createQuestion("Odstotki", `Koliko je ${percentage} % od ${base}?`, base * percentage / 100, level);
}

export function generateLinearEquationQuestion(level = 1): QuestionInterface {
    const x = randomNumber(1, level === 1 ? 10 : 50);
    const coefficient = randomNumber(1, level === 1 ? 4 : 10);
    const constant = randomNumber(1, level === 1 ? 10 : 30);
    return createQuestion("Linearne enačbe", `Reši enačbo: ${coefficient}x + ${constant} = ${coefficient * x + constant}.`, x, level);
}

export function generateFunctionsAndGraphsQuestion(level = 1): QuestionInterface {
    const slope = randomNumber(1, level === 1 ? 4 : 10) * (Math.random() >= 0.5 ? 1 : -1);
    const intercept = randomNumber(-10, 10);
    const x = randomNumber(1, level === 1 ? 5 : 20);
    return createQuestion("Funkcije in grafi", `Če je y = ${slope}x ${intercept >= 0 ? "+" : "-"} ${Math.abs(intercept)}, koliko je y pri x = ${x}?`, slope * x + intercept, level);
}

export function generatePythagoreanQuestion(level = 1): QuestionInterface {
    const triples = [[3, 4, 5], [5, 12, 13], [6, 8, 10]] as const;
    const [a, b, c] = triples[randomNumber(0, triples.length - 1)]!;
    return createQuestion("Pitagorov izrek", `Kateti pravokotnega trikotnika merita ${a} cm in ${b} cm. Koliko meri hipotenuza?`, c, level);
}

export function generatePowersAndRootsQuestion(level = 1): QuestionInterface {
    if (Math.random() >= 0.5) {
        const root = randomNumber(2, level === 1 ? 10 : 20);
        return createQuestion("Potence in kvadratni koreni", `Koliko je √${root ** 2}?`, root, level);
    }
    const base = randomNumber(2, level === 1 ? 5 : 10);
    const exponent = randomNumber(2, level === 1 ? 4 : 5);
    return createQuestion("Potence in kvadratni koreni", `Koliko je ${base}^${exponent}?`, base ** exponent, level);
}
