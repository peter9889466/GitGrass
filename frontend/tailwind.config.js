/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        darkBg: "#0b0f19",
        darkCard: "#161b22",
        grassGreen: {
          100: "#0e4429",
          200: "#006d32",
          300: "#26a641",
          400: "#39d353",
        }
      }
    },
  },
  plugins: [],
}
