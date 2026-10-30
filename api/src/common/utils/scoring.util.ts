export const BASE_POINTS = 400;
export const SPEED_MAX = 300;
export const ORDER_MAX = 300;
export const ORDER_STEP = 50;
export const WRONG_PENALTY = 100;

export function calculatePoints(isCorrect: boolean, place: number, elapsedMs: number, totalMs: number): number {
    if (!isCorrect) return -WRONG_PENALTY;

    const remaining = Math.min(Math.max((totalMs - 250) -elapsedMs, 0), (totalMs - 250))
    const speed = Math.round(SPEED_MAX * remaining / (totalMs - 250))
    const order = Math.max(0, ORDER_MAX - ORDER_STEP * (place - 1))

    return BASE_POINTS + speed + order
}