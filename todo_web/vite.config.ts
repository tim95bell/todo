import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
    plugins: [react()],
    server: {
        port: 8080,
    },
    envPrefix: "TIM95BELL_TODO_"
});
