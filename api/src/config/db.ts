import mongoose from "mongoose";

export async function connectToDatabase(tries = 10) {
    for (let i = 1; i <= tries; i++) {
        try {
            await mongoose.connect(process.env.MONGO_URI!, { serverSelectionTimeoutMS: 3000 });
            console.log("MongoDB connected");
            return;
        } catch (e) {
            console.log(`Mongo not ready (${i}/${tries}), retrying...`);
            await new Promise((r) => setTimeout(r, 2000));
        }
    }
    throw new Error("Could not connect to MongoDB");
}