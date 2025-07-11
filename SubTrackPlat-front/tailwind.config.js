/** @type {import('tailwindcss').Config} */
export default {
   content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: {
          500: '#00d4ff', // 主色
          600: '#00b8e0'
        },
        dark: {
          900: '#0f1419' // 深色背景
      },
    },
  },
  plugins: [],
}
}
