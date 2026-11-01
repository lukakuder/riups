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
                    () => generateNaturalSequenceQuestion(level),
                    () => generateEqualityQuestion(level),
                ];
            case 2:
                return [
                    () => generateNaturalDivisionQuestion(level),
                    () => generateFractionsQuestion(level),
                    () => generateAreaPerimeterQuestion(level),
                    () => generateExpressionsAndNumberLineQuestion(level),
                    () => generateMultiplesAndDivisorsQuestion(level),
                    () => generateRoundingQuestion(level),
                    () => generateDecimalSequenceQuestion(level),
                    () => generateMeasurementCalculationQuestion(level),
                    () => generateLargestNumberQuestion(level),
                ];
            case 3:
                return [
                    () => generatePercentagesQuestion(level),
                    () => generateLinearEquationQuestion(level),
                    () => generateFunctionsAndGraphsQuestion(level),
                    () => generatePythagoreanQuestion(level),
                    () => generatePowersAndRootsQuestion(level),
                    () => generatePrimeNumberQuestion(level),
                    () => generateStatisticsQuestion(level),
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
        answer: typeof answer === "number" ? formatDecimal(answer) : answer,
        available_time: QUESTION_TIME_BY_DIFFICULTY[timeLevel],
    };
}

function formatDecimal(value: number | string): string {
    return String(value).replace(".", ",");
}

export function generateAdditionQuestion(level = 1): QuestionInterface {
    return generateAdditionSubtractionQuestion(level, true);
}

export function generateSubtractionQuestion(level = 1): QuestionInterface {
    return generateAdditionSubtractionQuestion(level, false);
}

export function generateMultiplicationQuestion(level = 1): QuestionInterface {
    const minimum = level === 1 ? 1 : level === 2 ? 2 : 5;
    const maximum = level === 1 ? 5 : level === 2 ? 10 : 15;
    const first = randomNumber(minimum, maximum);
    const second = randomNumber(minimum, maximum);
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
    const minimum = level === 1 ? 1 : -maximum;
    const first = randomNumber(minimum, maximum);
    const second = addition
        ? randomNumber(minimum, maximum)
        : randomNumber(minimum, maximum);
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
    const type = randomNumber(0, level >= 2 ? 3 : 1);
    if (type === 0) {
        return createQuestion("Dopolnjevanje računov", `Katero število manjka: ? + ${other} = ${missing + other}?`, missing, level);
    }
    if (type === 1) {
        return createQuestion("Dopolnjevanje računov", `Katero število manjka: ${missing} + ? = ${missing + other}?`, other, level);
    }
    if (type === 2) {
        return createQuestion("Dopolnjevanje računov", `Katero število manjka: ? × ${other} = ${missing * other}?`, missing, level);
    }
    return createQuestion("Dopolnjevanje računov", `Katero število manjka: ${missing * other} ÷ ? = ${missing}?`, other, level);
}

export function generateTimeAndUnitsQuestion(level = 1): QuestionInterface {
    if (Math.random() >= 0.5) {
        const units = [
            { name: "mm", factor: 1 },
            { name: "cm", factor: 10 },
            { name: "dm", factor: 100 },
            { name: "m", factor: 1000 },
        ] as const;
        const fromIndex = randomNumber(0, units.length - 1);
        let toIndex = randomNumber(0, units.length - 1);
        while (toIndex === fromIndex) {
            toIndex = randomNumber(0, units.length - 1);
        }
        const from = units[fromIndex]!;
        const to = units[toIndex]!;
        const denominator = from.factor < to.factor ? to.factor / from.factor : 1;
        const amount = randomNumber(1, level === 1 ? 5 : level === 2 ? 12 : 25) * denominator;
        return createQuestion(
            "Čas in merske enote",
            `Koliko ${to.name} je ${amount} ${from.name}?`,
            amount * from.factor / to.factor,
            level
        );
    }

    const units = [
        { name: "sekund", factor: 1 },
        { name: "minut", factor: 60 },
        { name: "ur", factor: 3600 },
        { name: "dni", factor: 86400 },
    ] as const;
    const fromIndex = randomNumber(0, units.length - 1);
    let toIndex = randomNumber(0, units.length - 1);
    while (toIndex === fromIndex) {
        toIndex = randomNumber(0, units.length - 1);
    }
    const from = units[fromIndex]!;
    const to = units[toIndex]!;
    const denominator = from.factor < to.factor ? to.factor / from.factor : 1;
    const amount = randomNumber(1, level === 1 ? 3 : level === 2 ? 8 : 20) * denominator;
    return createQuestion(
        "Čas in merske enote",
        `Koliko ${to.name} je ${amount} ${from.name}?`,
        amount * from.factor / to.factor,
        level
    );
}

export function generateNaturalDivisionQuestion(level = 1): QuestionInterface {
    const divisor = randomNumber(2, level === 1 ? 12 : 30);
    const quotient = randomNumber(2, level === 1 ? 30 : 100);
    return createQuestion("Deljenje naravnih števil", `Koliko je ${divisor * quotient} ÷ ${divisor}?`, quotient, level);
}

export function generateFractionsQuestion(level = 1): QuestionInterface {
    const firstDenominator = randomNumber(2, level === 1 ? 6 : 12);
    const secondDenominator = level === 1
        ? firstDenominator
        : randomNumber(2, 12);
    const first = randomNumber(1, firstDenominator - 1);
    const second = randomNumber(1, secondDenominator - 1);
    const numerator = first * secondDenominator + second * firstDenominator;
    const denominator = firstDenominator * secondDenominator;
    const divisor = greatestCommonDivisor(numerator, denominator);
    return createQuestion(
        "Ulomki",
        `Koliko je ${first}/${firstDenominator} + ${second}/${secondDenominator}?`,
        `${numerator / divisor}/${denominator / divisor}`,
        level
    );
}

function greatestCommonDivisor(first: number, second: number): number {
    while (second !== 0) {
        [first, second] = [second, first % second];
    }
    return first;
}

export function generateAreaPerimeterQuestion(level = 1): QuestionInterface {
    const length = randomNumber(2, level === 1 ? 12 : 30);
    const width = randomNumber(2, level === 1 ? 10 : 20);
    const type = randomNumber(0, 3);
    if (type === 0) {
        return createQuestion("Obseg in ploščina", `Kolikšen je obseg pravokotnika ${length} cm × ${width} cm?`, 2 * (length + width), level);
    }
    if (type === 1) {
        return createQuestion("Obseg in ploščina", `Kolikšna je ploščina pravokotnika ${length} cm × ${width} cm?`, length * width, level);
    }
    if (type === 2) {
        return createQuestion("Obseg in ploščina", `Kolikšen je obseg kvadrata s stranico ${length} cm?`, 4 * length, level);
    }
    return createQuestion("Obseg in ploščina", `Kolikšna je ploščina trikotnika z osnovnico ${length} cm in višino ${width} cm?`, length * width / 2, level);
}

export function generateExpressionsAndNumberLineQuestion(level = 1): QuestionInterface {
    const number = randomNumber(1, level === 1 ? 30 : 100);
    const change = randomNumber(1, level === 1 ? 10 : 25);
    const greater = Math.random() >= 0.5;
    return createQuestion("Številski izrazi in številska premica", `Katero število je za ${change} ${greater ? "večje" : "manjše"} od ${number}?`, greater ? number + change : number - change, level);
}

export function generateMultiplesAndDivisorsQuestion(level = 1): QuestionInterface {
    const divisor = randomNumber(2, level === 1 ? 10 : 20);
    const divisible = Math.random() >= 0.5;
    const multiple = divisible
        ? divisor * randomNumber(2, level === 1 ? 10 : 20)
        : divisor * randomNumber(2, level === 1 ? 10 : 20) + randomNumber(1, divisor - 1);
    return createQuestion(
        "Večkratniki in delitelji",
        `Ali je ${multiple} deljivo z ${divisor}?`,
        divisible ? "Da" : "Ne",
        level
    );
}

export function generateNaturalSequenceQuestion(level = 1): QuestionInterface {
    const difference = randomNumber(level === 1 ? 1 : level === 2 ? 4 : 7, level === 1 ? 3 : level === 2 ? 6 : 12);
    const ascending = Math.random() >= 0.5;
    const start = ascending
        ? randomNumber(1, 50)
        : randomNumber(4 * difference + 1, 100);
    const sequence = Array.from({ length: 5 }, (_, index) =>
        start + (ascending ? 1 : -1) * difference * index
    );
    const missingIndex = randomNumber(0, sequence.length - 1);
    const answer = sequence[missingIndex]!;
    sequence[missingIndex] = 0;
    return createQuestion(
        "Nadaljuj zaporedje",
        `Katero število manjka v zaporedju: ${sequence.map((value) => value === 0 ? "?" : value).join(", ")}?`,
        answer,
        level
    );
}

export function generateEqualityQuestion(level = 1): QuestionInterface {
    const maximum = level === 1 ? 20 : level === 2 ? 50 : 100;
    const left = randomNumber(1, maximum);
    const right = Math.random() >= 0.5 ? left : randomNumber(1, maximum);
    const correctOperator = left === right ? "=" : left > right ? ">" : "<";
    const displayedOperator = Math.random() >= 0.5
        ? correctOperator
        : correctOperator === "=" ? ">" : correctOperator === ">" ? "<" : "=";
    const statementIsCorrect = displayedOperator === correctOperator;
    return createQuestion(
        "Primerjanje števil",
        `Ali velja ${left} ${displayedOperator} ${right}?`,
        statementIsCorrect ? "Da" : "Ne",
        level
    );
}

export function generateRoundingQuestion(level = 1): QuestionInterface {
    const decimalsToRound = level - 1;
    const sourceDecimals = decimalsToRound + randomNumber(1, 2);
    const scale = 10 ** sourceDecimals;
    const integerPart = randomNumber(10, level === 1 ? 100 : level === 2 ? 1000 : 10000);
    const fractionalPart = randomNumber(1, scale - 1);
    const value = integerPart + fractionalPart / scale;
    const factor = 10 ** decimalsToRound;
    const rounded = Math.round(value * factor) / factor;
    return createQuestion(
        "Zaokroževanje",
        `Zaokroži število ${formatDecimal(value.toFixed(sourceDecimals))} na ${decimalsToRound} decimalnih mest.`,
        decimalsToRound === 0 ? String(rounded) : formatDecimal(rounded.toFixed(decimalsToRound)),
        level
    );
}

export function generateDecimalSequenceQuestion(level = 1): QuestionInterface {
    const decimalPlaces = level;
    const scale = 10 ** decimalPlaces;
    const difference = randomNumber(level === 1 ? 1 : level === 2 ? 10 : 25, level === 1 ? 9 : level === 2 ? 30 : 100) / scale;
    const ascending = Math.random() >= 0.5;
    const start = randomNumber(100, level === 1 ? 1000 : level === 2 ? 5000 : 10000) / scale;
    const sequence = Array.from({ length: 5 }, (_, index) =>
        Number((start + (ascending ? 1 : -1) * difference * index).toFixed(decimalPlaces))
    );
    const missingIndex = randomNumber(0, sequence.length - 1);
    const answer = sequence[missingIndex]!;
    return createQuestion(
        "Zaporedja z decimalnimi števili",
        `Katero število manjka v zaporedju: ${sequence.map((value, index) => index === missingIndex ? "?" : formatDecimal(value.toFixed(decimalPlaces))).join(", ")}?`,
        answer,
        level
    );
}

export function generateMeasurementCalculationQuestion(level = 1): QuestionInterface {
    const conversions = [
        { from: "mm", to: "cm", factor: 0.1 },
        { from: "cm", to: "dm", factor: 0.1 },
        { from: "dm", to: "m", factor: 0.1 },
        { from: "m", to: "cm", factor: 100 },
        { from: "ml", to: "l", factor: 0.001 },
        { from: "g", to: "kg", factor: 0.001 },
        { from: "h", to: "min", factor: 60 },
        { from: "dni", to: "ur", factor: 24 },
    ] as const;
    const conversion = conversions[randomNumber(0, conversions.length - 1)]!;
    const amount = randomNumber(1, level === 1 ? 10 : level === 2 ? 50 : 100);
    return createQuestion(
        "Računanje z merskimi enotami",
        `Koliko ${conversion.to} je ${amount} ${conversion.from}?`,
        amount * conversion.factor,
        level
    );
}

export function generateLargestNumberQuestion(level = 1): QuestionInterface {
    const digitCount = level === 1 ? 3 : level === 2 ? 5 : 7;
    const digits = Array.from({ length: digitCount }, () => randomNumber(0, 9));
    if (digits.every((digit) => digit === 0)) {
        digits[0] = 1;
    }
    const largest = [...digits].sort((a, b) => b - a).join("");
    const smallestDigits = [...digits].sort((a, b) => a - b);
    if (smallestDigits[0] === 0) {
        const nonZeroIndex = smallestDigits.findIndex((digit) => digit !== 0);
        [smallestDigits[0], smallestDigits[nonZeroIndex]] = [smallestDigits[nonZeroIndex]!, smallestDigits[0]!];
    }
    const smallest = smallestDigits.join("");
    const findLargest = Math.random() >= 0.5;
    return createQuestion(
        "Največje število iz števk",
        `Zapiši ${findLargest ? "največje" : "najmanjše"} možno število iz števk: ${digits.join(", ")}.`,
        findLargest ? largest : smallest,
        level
    );
}

function isPrime(number: number): boolean {
    if (number < 2 || !Number.isInteger(number)) {
        return false;
    }
    for (let divisor = 2; divisor * divisor <= number; divisor += 1) {
        if (number % divisor === 0) {
            return false;
        }
    }
    return true;
}

export function generatePrimeNumberQuestion(level = 1): QuestionInterface {
    const count = level === 1 ? 5 : level === 2 ? 7 : 9;
    const primeCandidates = [11, 13, 17, 19, 23, 29, 31, 37, 41];
    const numbers = [
        primeCandidates[randomNumber(0, primeCandidates.length - 1)]!,
        10,
        ...Array.from({ length: count - 2 }, () => randomNumber(10, level === 1 ? 100 : level === 2 ? 300 : 1000)),
    ];
    for (let index = 2; index < numbers.length; index += 1) {
        if (isPrime(numbers[index]!)) {
            numbers[index] = numbers[index]! + 1;
        }
    }
    const primes = numbers.filter(isPrime);
    const largest = Math.random() >= 0.5;
    const primeAnswer = largest ? Math.max(...primes) : Math.min(...primes);
    return createQuestion(
        "Praštevila",
        `Izmed števil ${numbers.join(", ")} zapiši ${largest ? "največje" : "najmanjše"} praštevilo.`,
        primeAnswer,
        level
    );
}

export function generateStatisticsQuestion(level = 1): QuestionInterface {
    const count = Math.random() >= 0.5 ? 8 : 9;
    const maximum = level === 1 ? 20 : 100;
    const mode = randomNumber(1, maximum);
    const numbers = [mode, mode];
    const used = new Set(numbers);
    while (numbers.length < count) {
        const number = randomNumber(1, maximum);
        if (!used.has(number)) {
            numbers.push(number);
            used.add(number);
        }
    }
    if (level === 1) {
        return createQuestion("Statistika", `Določi modus števil: ${numbers.join(", ")}.`, numbers[0]!, level);
    }
    if (level === 2) {
        const sorted = [...numbers].sort((a, b) => a - b);
        const median = sorted.length % 2 === 0
            ? (sorted[sorted.length / 2 - 1]! + sorted[sorted.length / 2]!) / 2
            : sorted[Math.floor(sorted.length / 2)]!;
        return createQuestion("Statistika", `Določi mediano števil: ${numbers.join(", ")}.`, median, level);
    }
    const average = numbers.reduce((sum, number) => sum + number, 0) / numbers.length;
    return createQuestion("Statistika", `Določi aritmetično sredino števil: ${numbers.join(", ")}.`, average, level);
}

export function generatePercentagesQuestion(level = 1): QuestionInterface {
    const percentage = randomNumber(1, level === 1 ? 50 : 90);
    const base = randomNumber(2, level === 1 ? 20 : 200) * 10;
    const type = level === 1 ? 0 : randomNumber(0, 2);
    if (type === 1) {
        const result = base * percentage / 100;
        return createQuestion("Odstotki", `${percentage} % nekega števila je ${formatDecimal(result)}. Katero je število?`, base, level);
    }
    if (type === 2) {
        const result = base * percentage / 100;
        return createQuestion("Odstotki", `Kateri odstotek števila ${base} je ${formatDecimal(result)}?`, percentage, level);
    }
    return createQuestion("Odstotki", `Koliko je ${percentage} % od ${base}?`, base * percentage / 100, level);
}

export function generateLinearEquationQuestion(level = 1): QuestionInterface {
    const x = randomNumber(1, level === 1 ? 10 : 50);
    const coefficient = randomNumber(1, level === 1 ? 4 : level === 2 ? 10 : 10);
    const constant = randomNumber(1, level === 1 ? 10 : 30);
    if (level === 3 && Math.random() >= 0.5) {
        const leftCoefficient = Math.max(2, coefficient);
        const rightCoefficient = randomNumber(1, leftCoefficient - 1);
        const rightConstant = leftCoefficient * x + constant - rightCoefficient * x;
        return createQuestion("Linearne enačbe", `Reši enačbo: ${leftCoefficient}x + ${constant} = ${rightCoefficient}x + ${rightConstant}.`, x, level);
    }
    return createQuestion("Linearne enačbe", `Reši enačbo: ${coefficient}x + ${constant} = ${coefficient * x + constant}.`, x, level);
}

export function generateFunctionsAndGraphsQuestion(level = 1): QuestionInterface {
    const slope = randomNumber(1, level === 1 ? 4 : 10) * (Math.random() >= 0.5 ? 1 : -1);
    const intercept = randomNumber(-10, 10);
    const x = randomNumber(1, level === 1 ? 5 : 20);
    return createQuestion("Funkcije in grafi", `Če je y = ${slope}x ${intercept >= 0 ? "+" : "-"} ${Math.abs(intercept)}, koliko je y pri x = ${x}?`, slope * x + intercept, level);
}

export function generatePythagoreanQuestion(level = 1): QuestionInterface {
    const triples = level === 1
        ? [[3, 4, 5], [6, 8, 10]] as const
        : level === 2
            ? [[5, 12, 13], [8, 15, 17]] as const
            : [[7, 24, 25], [9, 40, 41]] as const;
    const [a, b, c] = triples[randomNumber(0, triples.length - 1)]!;
    if (level === 3 && Math.random() >= 0.5) {
        return createQuestion("Pitagorov izrek", `Hipotenuza pravokotnega trikotnika meri ${c} cm, ena kateta pa ${a} cm. Koliko meri druga kateta?`, b, level);
    }
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
