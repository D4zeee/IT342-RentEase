import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import "./index.css"
import { ThemeProvider } from "@material-tailwind/react"
import App from './App.jsx'

createRoot(document.getElementById('root')).render(
  <ThemeProvider>
  <StrictMode>
    <App />
  </StrictMode>,
  </ThemeProvider>
)